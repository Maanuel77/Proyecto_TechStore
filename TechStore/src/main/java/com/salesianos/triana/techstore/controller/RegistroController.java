package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Alta de nuevos clientes desde el formulario público de registro.
@Controller
@RequestMapping("/auth/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final UserService userService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new User());
        return "auth/registro";
    }

    @PostMapping
    public String procesarRegistro(@Valid @ModelAttribute("usuario") User usuario,
                                   BindingResult bindingResult,
                                   Model model) {

        // 1) Validaciones de Jakarta (@Email, @Pattern, @Size...).
        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }

        // 2) Username único: lo comprobamos aparte porque depende de BD.
        if (userService.existeUsername(usuario.getUsername())) {
            bindingResult.rejectValue("username", "username.exists",
                "Ese nombre de usuario ya está en uso, elige otro");
            return "auth/registro";
        }

        userService.registrar(usuario);
        return "redirect:/auth/login?registrado=true";
    }
}
