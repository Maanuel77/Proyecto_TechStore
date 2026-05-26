package com.salesianos.triana.techstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.salesianos.triana.techstore.security.Cliente;
import com.salesianos.triana.techstore.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Alta de nuevos clientes desde el formulario público de registro.
// El formulario trabaja directamente con un Cliente (subclase de Usuario)
// para reaprovechar las validaciones de Jakarta de la clase base.
@Controller
@RequestMapping("/auth/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final UserService userService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Cliente());
        return "auth/registro";
    }

    @PostMapping
    public String procesarRegistro(@Valid @ModelAttribute("usuario") Cliente usuario,
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

        userService.registrar(usuario.getUsername(), usuario.getPassword(),
                              usuario.getEmail(), usuario.getFullname(),
                              usuario.getTelefono());
        return "redirect:/auth/login?registrado=true";
    }
}
