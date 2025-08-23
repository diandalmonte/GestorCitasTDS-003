package com.jamd.controllers;

import com.jamd.dao.ContactoManager;
import com.jamd.modelo.Contacto;
import com.jamd.service.ContactoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contactos")
public class ContactoController {
    
    private final ContactoService contactoService;
    
    @Autowired
    private ContactoManager contactoManager;
    
    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }
    
    // Vista principal con formulario de creación arriba y tabla abajo
    @GetMapping
    public String vistaPrincipal(Model model) {
        model.addAttribute("contacto", new Contacto()); // Empty contact for creation form
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "contactos/principal";
    }
    
    // Crear nuevo contacto
    @PostMapping("/crear")
    public String crearContacto(@ModelAttribute Contacto contacto, RedirectAttributes redirectAttrs) {
        try {
            contactoService.crearContacto(contacto);
            redirectAttrs.addFlashAttribute("mensaje", "Contacto creado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contactos";
    }
    
    // Eliminar contacto
    @PostMapping("/eliminar/{id}")
    public String eliminarContacto(@PathVariable("id") Integer id, RedirectAttributes redirectAttrs) {
        try {
            Contacto contacto = contactoManager.findById(id);
            
            if (contacto != null) {
                contactoManager.eliminar(id);
                redirectAttrs.addFlashAttribute("mensaje", "Contacto eliminado correctamente");
                System.out.println("Contacto con ID " + id + " eliminado exitosamente.");
            } else {
                redirectAttrs.addFlashAttribute("error", "No se encontró el contacto con ID: " + id);
                System.out.println("Intento de eliminar contacto no existente con ID: " + id);
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede eliminar el contacto porque tiene citas asociadas. Elimine las citas primero.");
            System.err.println("Error de integridad referencial al eliminar contacto con ID " + id + ": " + e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", 
                "Error inesperado al eliminar el contacto: " + e.getMessage());
            System.err.println("Error inesperado al eliminar contacto con ID " + id + ": " + e.getMessage());
        }
        
        return "redirect:/contactos";
    }

    // Mostrar formulario de edición en página separada
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model) {
        Contacto contacto = contactoManager.findById(id);
        if (contacto != null) {
            model.addAttribute("contacto", contacto);
            return "contactos/editar"; // Separate edit page
        } else {
            return "redirect:/contactos";
        }
    }

    // Procesar actualización de contacto
    @PostMapping("/editar/{id}")
    public String actualizarContacto(@PathVariable("id") Integer id, 
                                    @ModelAttribute Contacto contacto, 
                                    RedirectAttributes redirectAttrs) {
        try {
            Contacto contactoExistente = contactoManager.findById(id);
            if (contactoExistente != null) {
                // Update all fields
                contactoExistente.setNombre(contacto.getNombre());
                contactoExistente.setApellido(contacto.getApellido());
                contactoExistente.setEmpresa(contacto.getEmpresa());
                contactoExistente.setTelefono(contacto.getTelefono());
                contactoExistente.setCorreo(contacto.getCorreo());
                
                contactoManager.guardar(contactoExistente);
                redirectAttrs.addFlashAttribute("mensaje", "Contacto actualizado correctamente");
            } else {
                redirectAttrs.addFlashAttribute("error", "Contacto no encontrado");
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar contacto: " + e.getMessage());
        }
        return "redirect:/contactos";
    }
}