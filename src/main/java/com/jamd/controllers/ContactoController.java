package com.jamd.controllers;

import com.jamd.modelo.Contacto;
import com.jamd.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contactos")
public class ContactoController {
    
    private final ContactoService contactoService;
    
    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }
    
    // Vista principal con formulario arriba y tabla abajo
    @GetMapping
    public String vistaPrincipal(Model model) {
        model.addAttribute("contacto", new Contacto());
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "contactos/principal";
    }
    
    // Crear nuevo contacto
    @PostMapping("/crear")
    public String crearContacto(@ModelAttribute Contacto contacto, Model model) {
        try {
            contactoService.crearContacto(contacto);
            model.addAttribute("mensaje", "Contacto creado exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "contactos/principal";
    }
    
    // Eliminar contacto
    @GetMapping("/eliminar/{id}")
    public String eliminarContacto(@PathVariable int id, Model model) {
        try {
            contactoService.eliminarContacto(id);
            model.addAttribute("mensaje", "Contacto eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("contacto", new Contacto());
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "contactos/principal";
    }
}