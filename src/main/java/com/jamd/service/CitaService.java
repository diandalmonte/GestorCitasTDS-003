package com.jamd.service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jamd.dao.CitaManager;
import com.jamd.dao.ContactoManager;
import com.jamd.modelo.Cita;
import com.jamd.modelo.Contacto;

@Service
@Transactional
public class CitaService {
    private final CitaManager citaManager;
    private final ContactoManager contactoManager;
    
    public CitaService(CitaManager citaManager, ContactoManager contactoManager) {
        this.citaManager = citaManager;
        this.contactoManager = contactoManager;
    }


    //CREAR CITA
    public Cita crearCita(Cita cita) {
        if (cita.getFecha().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Fecha de cita no puede ser del pasado.");
        }

        if (!isHorarioDisponible(cita.getFecha())){
            throw new IllegalArgumentException("Fecha de cita esta ocupada");
        }


        // Verificar si contactos asociados existen
        if (cita.getContactos() != null) {
            for (Contacto contacto : cita.getContactos()){
                if (contacto.getId() == 0 || !contactoManager.contactoExiste(contacto.getId())){
                    throw new IllegalArgumentException("Contacto no pudo ser encontrado.");
                }
            }
        }


        citaManager.guardar(cita);
        return cita;
    }

    //LEER CITA
    public List<Cita> getAllCitas(){
        return citaManager.obtener();
    }

    public List<Cita> getCitasPorVenir(){
        return citaManager.obtenerCitasDespuesDeFecha(LocalDateTime.now());
    }

    //ACTUALIZAR CITA
    public Cita actualizarCita(int id, Cita cambioCita) {
        Cita citaExistente = null;
        if (!citaManager.citaExiste(id)){
            throw new IllegalArgumentException("Cita con ID: " + id + "no existe");
        }

        citaExistente = citaManager.obtenerPorId(id);

        // Actualizar solo campos permitidos
        if (cambioCita.getEncabezado() != null) {
            citaExistente.setEncabezado(cambioCita.getEncabezado());
        }
        
        if (cambioCita.getDescripcion() != null) {
            citaExistente.setDescripcion(cambioCita.getDescripcion());
        }
        
        if (cambioCita.getFecha() != null) {
            
            // Verificar si el horario está disponible (excluyendo la cita actual)
            if (!isHorarioDisponibleParaActualizar(id, cambioCita.getFecha())) {
                throw new IllegalArgumentException("El horario no está disponible");
            }
            
            citaExistente.setFecha(cambioCita.getFecha());
        }
        

        
        // Actualizar contactos asociados
        if (cambioCita.getContactos() != null) {
            for (Contacto contacto : cambioCita.getContactos()) {
                if (contacto.getId() == 0 || !contactoManager.contactoExiste(contacto.getId())) {
                    throw new IllegalArgumentException("Contacto no encontrado con ID: " + contacto.getId());
                }
            }
            citaExistente.setContactos(cambioCita.getContactos());
        }
        
        citaManager.guardar(citaExistente);
        return citaExistente;
    }

    public void eliminarCita(int id) {
        Cita cita = citaManager.obtenerPorId(id);
        
        // Cita no se puede borrar si ya ocurrió
        if (cita.getFecha().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot delete past appointments");
        }
    
        citaManager.eliminar(cita.getId());
    }



    private boolean isHorarioDisponible(LocalDateTime nuevaFecha) {
        List<Cita> futurasCitas = citaManager.obtenerCitasDespuesDeFecha(LocalDateTime.now());

        for (Cita cita : futurasCitas){
            if (cita.getFecha().isEqual(nuevaFecha)){
                return false;
            }
        }
        return true;
    }

    private boolean isHorarioDisponibleParaActualizar(int idCitaExcluir, LocalDateTime nuevaFecha) {
        List<Cita> futurasCitas = citaManager.obtenerCitasDespuesDeFecha(LocalDateTime.now());

        for (Cita cita : futurasCitas){
            if (cita.getId() != idCitaExcluir && cita.getFecha().isEqual(nuevaFecha)){
                return false;
            }
        }
        return true;
    }



}