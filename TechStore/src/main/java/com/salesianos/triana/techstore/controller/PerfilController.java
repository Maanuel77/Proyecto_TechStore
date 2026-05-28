package com.salesianos.triana.techstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.security.Usuario;
import com.salesianos.triana.techstore.service.EmailService;
import com.salesianos.triana.techstore.service.UserService;
import com.salesianos.triana.techstore.service.VerificacionService;
import com.salesianos.triana.techstore.service.VerificacionService.Resultado;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// Perfil del usuario logueado: ver, editar datos y cambiar contraseña.
//
// El cambio de contraseña tiene dos pasos:
//   1. POST /perfil/cambiar-password/solicitar
//      → valida la actual + la nueva, manda código por email y guarda
//        la nueva (en claro) + el código en sesión.
//   2. POST /perfil/cambiar-password/confirmar
//      → valida el código, aplica la contraseña nueva, limpia sesión.
@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UserService userService;
    private final VerificacionService verificacionService;
    private final EmailService emailService;

    @GetMapping
    public String perfil(@AuthenticationPrincipal Usuario usuario,
                         HttpServletRequest request, Model model) {
        model.addAttribute("usuario", usuario);
        // El template decide qué bloque mostrar (form normal vs form del código)
        // según si hay un cambio de contraseña pendiente en sesión.
        HttpSession session = request.getSession();
        boolean pendiente = verificacionService.hayCambioPasswordPendiente(session);
        model.addAttribute("cambioPasswordPendiente", pendiente);
        if (pendiente) {
            model.addAttribute("segundosReenvio",
                    verificacionService.segundosParaReenvio(session, VerificacionService.S_PWD_ULTIMO_ENVIO));
        }
        return "perfil/perfil";
    }

    @PostMapping("/editar")
    public String editarDatos(@AuthenticationPrincipal Usuario usuario,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String fullname,
                              @RequestParam String telefono,
                              RedirectAttributes ra) {
        userService.editarDatos(usuario.getId(), username, email, fullname, telefono);
        ra.addFlashAttribute("datosCambiados", true);
        return "redirect:/perfil";
    }

    //PASO 1: solicitar el código de cambio de contraseña.
    
    @PostMapping("/cambiar-password/solicitar")
    public String solicitarCambioPassword(@AuthenticationPrincipal Usuario usuario,
                                          @RequestParam String passwordActual,
                                          @RequestParam String nuevaPassword,
                                          HttpServletRequest request,
                                          RedirectAttributes ra) {

        if (nuevaPassword == null || nuevaPassword.length() < 4) {
            ra.addFlashAttribute("errorPassword",
                    "La nueva contraseña debe tener al menos 4 caracteres.");
            return "redirect:/perfil";
        }
        if (!userService.contraseñaActualCoincide(usuario.getId(), passwordActual)) {
            ra.addFlashAttribute("errorPassword",
                    "La contraseña actual no es correcta.");
            return "redirect:/perfil";
        }

        HttpSession session = request.getSession(true);
        String codigo = verificacionService.generarCodigo();
        verificacionService.iniciarCambioPassword(session, usuario.getId(), nuevaPassword, codigo);
        emailService.enviarCodigoCambioPassword(usuario.getEmail(), usuario.getFullname(), codigo);

        ra.addFlashAttribute("infoPassword",
                "Te hemos enviado un código a tu correo. Introdúcelo para confirmar el cambio.");
        return "redirect:/perfil";
    }

    //   PASO 2: confirmar con el código y aplicar la nueva contraseña.

    @PostMapping("/cambiar-password/confirmar")
    public String confirmarCambioPassword(@AuthenticationPrincipal Usuario usuario,
                                          @RequestParam String codigo,
                                          HttpServletRequest request,
                                          RedirectAttributes ra) {

        HttpSession session = request.getSession();
        if (!verificacionService.hayCambioPasswordPendiente(session)) {
            return "redirect:/perfil";
        }

        Resultado resultado = verificacionService.validar(
                session, codigo,
                VerificacionService.S_PWD_CODIGO,
                VerificacionService.S_PWD_EXPIRA,
                VerificacionService.S_PWD_INTENTOS,
                () -> verificacionService.limpiarCambioPassword(session));

        switch (resultado) {
            case OK -> {
                String nueva = verificacionService.getPasswordNuevaPendiente(session);
                if (nueva == null) {
                    verificacionService.limpiarCambioPassword(session);
                    return "redirect:/perfil";
                }
                userService.cambiarPassword(usuario.getId(), nueva);
                verificacionService.limpiarCambioPassword(session);
                ra.addFlashAttribute("passwordCambiado", true);
                return "redirect:/perfil";
            }
            case EXPIRADO -> {
                ra.addFlashAttribute("errorPassword",
                        "El código ha caducado. Vuelve a solicitar el cambio.");
                return "redirect:/perfil";
            }
            case AGOTADO_INTENTOS -> {
                ra.addFlashAttribute("errorPassword",
                        "Has superado el máximo de intentos. Vuelve a solicitar el cambio.");
                return "redirect:/perfil";
            }
            case SIN_PENDIENTE -> {
                return "redirect:/perfil";
            }
            case INCORRECTO -> {
                Integer restantes = verificacionService.getIntentosRestantes(
                        session, VerificacionService.S_PWD_INTENTOS);
                ra.addFlashAttribute("errorPassword",
                        "Código incorrecto. Te quedan " + restantes + " intentos.");
                return "redirect:/perfil";
            }
        }
        return "redirect:/perfil";
    }

    //Reenviar el código del cambio de contraseña (cooldown 30s).
    
    @PostMapping("/cambiar-password/reenviar")
    public String reenviarCodigoPassword(@AuthenticationPrincipal Usuario usuario,
                                         HttpServletRequest request,
                                         RedirectAttributes ra) {
        HttpSession session = request.getSession();
        if (!verificacionService.hayCambioPasswordPendiente(session)) {
            return "redirect:/perfil";
        }
        long espera = verificacionService.segundosParaReenvio(session, VerificacionService.S_PWD_ULTIMO_ENVIO);
        if (espera > 0) {
            ra.addFlashAttribute("errorPassword",
                    "Espera " + espera + " segundos antes de reenviar.");
            return "redirect:/perfil";
        }
        String nuevo = verificacionService.generarCodigo();
        verificacionService.regenerar(session, nuevo,
                VerificacionService.S_PWD_CODIGO,
                VerificacionService.S_PWD_EXPIRA,
                VerificacionService.S_PWD_INTENTOS,
                VerificacionService.S_PWD_ULTIMO_ENVIO);
        emailService.enviarCodigoCambioPassword(usuario.getEmail(), usuario.getFullname(), nuevo);
        ra.addFlashAttribute("infoPassword", "Te hemos enviado un código nuevo.");
        return "redirect:/perfil";
    }

    //   Cancelar el cambio (vuelve al estado inicial).
    @PostMapping("/cambiar-password/cancelar")
    public String cancelarCambioPassword(HttpServletRequest request, RedirectAttributes ra) {
        verificacionService.limpiarCambioPassword(request.getSession());
        ra.addFlashAttribute("infoPassword", "Cambio de contraseña cancelado.");
        return "redirect:/perfil";
    }
}
