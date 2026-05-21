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

        // Si hay errores de validación (email mal formado, teléfono mal, etc.)
        // volvemos al formulario para que los corrija
        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }

        // Comprobamos que el nombre de usuario no esté ya en uso
        if (userService.existeUsername(usuario.getUsername())) {
            bindingResult.rejectValue("username", "username.exists",
                "Ese nombre de usuario ya está en uso, elige otro");
            return "auth/registro";
        }

        userService.registrar(usuario);
        return "redirect:/auth/login?registrado=true";
    }
}
