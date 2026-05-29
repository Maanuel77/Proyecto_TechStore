package com.salesianos.triana.techstore.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.security.Cliente;
import com.salesianos.triana.techstore.security.Usuario;
import com.salesianos.triana.techstore.security.UsuarioRepository;
import com.salesianos.triana.techstore.service.EmailService;
import com.salesianos.triana.techstore.service.LoginAttemptService;
import com.salesianos.triana.techstore.service.VerificacionService;
import com.salesianos.triana.techstore.service.VerificacionService.Resultado;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// Autenticación manual + flujo 2FA por email para los CLIENTE.
//
// Reemplaza el .formLogin() estándar de Spring Security: necesitamos
// interrumpir el login del cliente para mandarle un código por email
// y solo dejarle autenticado cuando lo verifique.
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificacionService verificacionService;
    private final EmailService emailService;
    private final LoginAttemptService loginAttemptService;

    //   POST /auth/login -> procesa credenciales
    // Si las credenciales son válidas:
    //  - ADMIN: autenticamos directamente y vamos al home.
    //  - CLIENTE: guardamos un código en sesión, mandamos email
    //               y redirigimos a /auth/verificar (NO autenticado).
    //
    // SEGURIDAD: con rate limiting básico para evitar fuerza bruta.
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpServletRequest request,
                                RedirectAttributes ra) {

        // Si veníamos arrastrando un 2FA pendiente, lo limpiamos.
        verificacionService.limpiarLogin(request.getSession());

        // Bloqueo por intentos: 5 fallos en 15 min → bloqueo de 15 min.
        if (loginAttemptService.estaBloqueado(username)) {
            long segs = loginAttemptService.segundosRestantesBloqueo(username);
            ra.addFlashAttribute("errorLogin",
                    "Cuenta bloqueada por intentos fallidos. Reintenta en "
                    + (segs / 60 + 1) + " min.");
            return "redirect:/auth/login";
        }

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            loginAttemptService.registrarFallo(username);
            return "redirect:/auth/login?error=true";
        }
        // Credenciales OK: reseteamos el contador para esta cuenta.
        loginAttemptService.resetear(username);

        // ADMIN: entra directo (sin 2FA), igual que el comportamiento previo.
        if (!(usuario instanceof Cliente)) {
            autenticar(usuario, request);
            return "redirect:/";
        }

        // CLIENTE: generamos código, lo guardamos en sesión y mandamos email.
        HttpSession session = request.getSession(true);
        String codigo = verificacionService.generarCodigo();
        verificacionService.iniciarLogin(session, usuario.getId(), codigo);
        emailService.enviarCodigoLogin(usuario.getEmail(), usuario.getFullname(), codigo);

        ra.addFlashAttribute("infoVerif",
                "Te hemos enviado un código a tu correo. Introdúcelo para entrar.");
        return "redirect:/auth/verificar";
    }

    //   GET /auth/verificar -> pantalla del 2FA
    @GetMapping("/verificar")
    public String mostrarVerificar(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        if (!verificacionService.hayLoginPendiente(session)) {
            return "redirect:/auth/login";
        }
        model.addAttribute("segundosReenvio",
                verificacionService.segundosParaReenvio(session, VerificacionService.S_LOGIN_ULTIMO_ENVIO));
        return "auth/verificar";
    }

    //   POST /auth/verificar -> valida el código
    @PostMapping("/verificar")
    public String procesarVerificar(@RequestParam String codigo,
                                    HttpServletRequest request,
                                    RedirectAttributes ra) {

        HttpSession session = request.getSession();
        Long usuarioId = verificacionService.getUsuarioPendienteLogin(session);
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }

        Resultado resultado = verificacionService.validar(
                session, codigo,

                VerificacionService.S_LOGIN_CODIGO,
                VerificacionService.S_LOGIN_EXPIRA,
                VerificacionService.S_LOGIN_INTENTOS,
                () -> verificacionService.limpiarLogin(session));

        switch (resultado) {
            case OK -> {
                Usuario u = usuarioRepository.findById(usuarioId).orElse(null);
                if (u == null) return "redirect:/auth/login";
                verificacionService.limpiarLogin(session);
                autenticar(u, request);
                return "redirect:/";
            }
            case EXPIRADO -> {
                ra.addFlashAttribute("errorVerif",
                        "El código ha caducado. Vuelve a iniciar sesión.");
                return "redirect:/auth/login";
            }
            case AGOTADO_INTENTOS -> {
                ra.addFlashAttribute("errorVerif",
                        "Has superado el máximo de intentos. Inicia sesión de nuevo.");
                return "redirect:/auth/login";
            }
            case SIN_PENDIENTE -> {
                return "redirect:/auth/login";
            }
            case INCORRECTO -> {
                Integer restantes = verificacionService.getIntentosRestantes(
                        session, VerificacionService.S_LOGIN_INTENTOS);
                ra.addFlashAttribute("errorVerif",
                        "Código incorrecto. Te quedan " + restantes + " intentos.");
                return "redirect:/auth/verificar";
            }
        }
        return "redirect:/auth/verificar";
    }

    //   POST /auth/verificar/reenviar -> genera y envía un código nuevo
    @PostMapping("/verificar/reenviar")
    public String reenviarCodigo(HttpServletRequest request, RedirectAttributes ra) {
        HttpSession session = request.getSession();
        Long usuarioId = verificacionService.getUsuarioPendienteLogin(session);
        if (usuarioId == null) {
            return "redirect:/auth/login";
        }
        // Cooldown anti-abuso.
        long espera = verificacionService.segundosParaReenvio(session, VerificacionService.S_LOGIN_ULTIMO_ENVIO);
        if (espera > 0) {
            ra.addFlashAttribute("errorVerif",
                    "Espera " + espera + " segundos antes de reenviar.");
            return "redirect:/auth/verificar";
        }
        Usuario u = usuarioRepository.findById(usuarioId).orElse(null);
        if (u == null) return "redirect:/auth/login";

        String nuevo = verificacionService.generarCodigo();
        verificacionService.regenerar(session, nuevo,
                VerificacionService.S_LOGIN_CODIGO,
                VerificacionService.S_LOGIN_EXPIRA,
                VerificacionService.S_LOGIN_INTENTOS,
                VerificacionService.S_LOGIN_ULTIMO_ENVIO);
        emailService.enviarCodigoLogin(u.getEmail(), u.getFullname(), nuevo);
        ra.addFlashAttribute("infoVerif", "Te hemos enviado un código nuevo.");
        return "redirect:/auth/verificar";
    }

    //   Helper: autenticación programática
    // Cuando NO usamos formLogin() de Spring Security tenemos que dejar
    // al usuario autenticado a mano: crear el Authentication, meterlo en
    // el SecurityContext y guardarlo en sesión.
    //
    // SEGURIDAD (session fixation): antes de meter el SecurityContext
    // regeneramos el ID de la sesión con changeSessionId(). Es lo que hace
    // el .formLogin() estándar y previene que un atacante "preparare" una
    // sesión anónima y se la pase a la víctima para heredar sus permisos
    // después del login.
    private void autenticar(Usuario usuario, HttpServletRequest request) {
        // Asegura que hay una sesión sobre la que podemos regenerar el ID.
        request.getSession(true);
        try {
            request.changeSessionId();
        } catch (IllegalStateException ignored) {
            // No había sesión todavía; getSession(true) la habrá creado
            // con un ID fresco, así que ya estamos protegidos.
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities());

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // Persistir el SecurityContext en la sesión HTTP, para que sobreviva
        // a la redirección. Esta es la clave que usa el filtro estándar.
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
    }
}
