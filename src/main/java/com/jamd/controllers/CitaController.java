package com.jamd.controllers;

import com.jamd.modelo.Cita;
import com.jamd.modelo.Contacto;
import com.jamd.service.CitaService;
import com.jamd.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/citas")
public class CitaController {
    
    private final CitaService citaService;
    private final ContactoService contactoService;
    
    public CitaController(CitaService citaService, ContactoService contactoService) {
        this.citaService = citaService;
        this.contactoService = contactoService;
    }
    
    // Vista principal con formulario arriba y tabla abajo
    @GetMapping
    public String vistaPrincipal(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("citas", citaService.getCitasPorVenir());
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "citas/principal";
    }
    
    // Crear nueva cita
    @PostMapping("/crear")
    public String crearCita(@ModelAttribute Cita cita, 
                           @RequestParam(required = false) List<Integer> contactoIds,
                           Model model) {
        try {
            // Asignar contactos seleccionados
            if (contactoIds != null && !contactoIds.isEmpty()) {
                for (Integer contactoId : contactoIds) {
                    Contacto contacto = contactoService.getContactoPorId(contactoId);
                    cita.addContacto(contacto);
                }
            }
            
            citaService.crearCita(cita);
            model.addAttribute("mensaje", "Cita creada exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        
        model.addAttribute("citas", citaService.getCitasPorVenir());
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "citas/principal";
    }
    
    // Eliminar cita
    @GetMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable int id, Model model) {
        try {
            citaService.deleteAppointment(id);
            model.addAttribute("mensaje", "Cita eliminada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        
        model.addAttribute("cita", new Cita());
        model.addAttribute("citas", citaService.getCitasPorVenir());
        model.addAttribute("contactos", contactoService.getAllContactos());
        return "citas/principal";
    }
}
