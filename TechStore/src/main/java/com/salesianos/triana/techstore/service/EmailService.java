package com.salesianos.triana.techstore.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

// Encapsula el envío de emails (códigos de verificación 2FA).
//
// SEGURIDAD: nunca se loggea el código de verificación en los logs por defecto.
// Si el SMTP falla, el código sigue válido en sesión (el cliente puede pedir
// "reenviar"). Solo si techstore.mail.log-code=true (perfil dev) se imprime
// el código por consola, para poder probar el flujo sin Gmail configurado.
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${techstore.mail.from}")
    private String from;

    @Value("${techstore.mail.from-name}")
    private String fromName;

    // Flag de debug: solo true en local cuando no se tiene SMTP configurado.
    @Value("${techstore.mail.log-code:false}")
    private boolean logCode;

    // Email de verificación al iniciar sesión (cliente).
    public void enviarCodigoLogin(String emailDestino, String fullname, String codigo) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("fullname", fullname);
        vars.put("codigo", codigo);
        enviar(emailDestino, "Tu código de acceso a TechStore", "emails/codigo-login", vars, codigo);
    }

    // Email de verificación al cambiar la contraseña desde /perfil.
    public void enviarCodigoCambioPassword(String emailDestino, String fullname, String codigo) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("fullname", fullname);
        vars.put("codigo", codigo);
        enviar(emailDestino, "Confirma el cambio de contraseña en TechStore", "emails/codigo-password", vars, codigo);
    }

    // Renderiza la plantilla Thymeleaf y la manda. Si algo falla,
    // el código sigue válido en sesión (el usuario puede pedir reenviar).
    // El código solo se imprime por consola si techstore.mail.log-code=true
    // (perfil dev), nunca en producción.
    private void enviar(String to, String asunto, String plantilla, Map<String, Object> vars, String codigo) {
        // Defensa contra Email Header Injection: si el destinatario contiene
        // CR/LF, un atacante podría inyectar headers SMTP extra (BCc, etc.).
        validarEmailSano(to);

        try {
            Context ctx = new Context();
            ctx.setVariables(vars);
            String html = templateEngine.process(plantilla, ctx);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(asunto);
            helper.setText(html, true);

            mailSender.send(mime);
            log.info("Email enviado a {}", to);
        } catch (MessagingException | java.io.UnsupportedEncodingException | RuntimeException e) {
            // No relanzamos: el código sigue válido en sesión.
            log.warn("No se ha podido enviar el email a {}. Causa: {}", to, e.getMessage());
            // SOLO en dev (techstore.mail.log-code=true): mostrar código por
            // consola para poder probar el flujo sin Gmail configurado.
            if (logCode) {
                log.warn("[DEV] Código de verificación para {}: {}", to, codigo);
            }
        }
    }

    // Rechaza emails que contengan CR/LF (header injection en SMTP).
    private static void validarEmailSano(String email) {
        if (email == null || email.indexOf('\r') >= 0 || email.indexOf('\n') >= 0 || email.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Email de destino inválido");
        }
    }
}
