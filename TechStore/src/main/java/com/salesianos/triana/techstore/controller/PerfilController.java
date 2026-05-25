package com.salesianos.triana.techstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.UserService;

import lombok.RequiredArgsConstructor;

// Perfil del usuario logueado: ver, editar datos y cambiar contraseña.
@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UserService userService;

    @GetMapping
    public String perfil(@AuthenticationPrincipal User usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "perfil/perfil";
    }

    @PostMapping("/editar")
    public String editarDatos(@AuthenticationPrincipal User usuario,
                               @RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String fullname,
                               @RequestParam String telefono,
                               RedirectAttributes redirectAttributes) {
        userService.editarDatos(usuario.getId(), username, email, fullname, telefono);
        redirectAttributes.addFlashAttribute("datosCambiados", true);
        return "redirect:/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@AuthenticationPrincipal User usuario,
                                  @RequestParam String nuevaPassword,
                                  RedirectAttributes redirectAttributes) {
        userService.cambiarPassword(usuario.getId(), nuevaPassword);
        redirectAttributes.addFlashAttribute("passwordCambiado", true);
        return "redirect:/perfil";
    }
}
