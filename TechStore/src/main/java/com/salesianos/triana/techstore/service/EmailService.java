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
// Si el SMTP falla, se loguea por consola para no romper el flujo de login
// y para que el código siga siendo visible en defensa.
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
    // se loguea el código por consola para no bloquear la verificación
    // (útil cuando el SMTP no está configurado o falla en local).
    private void enviar(String to, String asunto, String plantilla, Map<String, Object> vars, String codigo) {
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
            log.info("Email enviado a {} con código {}", to, codigo);
        } catch (MessagingException | java.io.UnsupportedEncodingException | RuntimeException e) {
            // No relanzamos: el código sigue válido en sesión.
            log.warn("No se ha podido enviar el email a {}. Código de verificación = {}", to, codigo);
            log.warn("Causa: {}", e.getMessage());
        }
    }
}
