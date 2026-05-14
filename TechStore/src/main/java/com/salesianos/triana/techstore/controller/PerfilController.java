package com.salesianos.triana.techstore.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.security.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String perfil(@AuthenticationPrincipal User usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "perfil/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@AuthenticationPrincipal User usuario,
                                  @RequestParam String nuevaPassword,
                                  RedirectAttributes ra) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(usuario);
        ra.addFlashAttribute("passwordCambiado", true);
        return "redirect:/perfil";
    }
}
