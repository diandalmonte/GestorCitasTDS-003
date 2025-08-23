package com.jamd.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.jamd.dao.ContactoManager;
import com.jamd.enums.CamposContacto;
import com.jamd.modelo.Contacto;
import com.jamd.modelo.ValidacionModelo;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ContactoService {
    private final ContactoManager contactoManager;
    
    public ContactoService(ContactoManager contactoManager) {
        this.contactoManager = contactoManager;
    }

    // CREAR CONTACTO
    public Contacto crearContacto(Contacto contacto) {
        // Validar campos obligatorios
        if (!ValidacionModelo.isStringNull(contacto.getNombre()) || contacto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del contacto no puede ser nulo.");
        }
        
        if (!ValidacionModelo.isTelefonoValid(contacto.getTelefono())) {
            throw new IllegalArgumentException("El teléfono del contacto no es válido.");
        }
        
        if (!ValidacionModelo.isStringNull(contacto.getCorreo()) || contacto.getCorreo().isEmpty()) {
            throw new IllegalArgumentException("El correo del contacto es obligatorio.");
        }
        
        // Verificar si el correo ya existe
        if (contactoManager.correoExiste(contacto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un contacto con este correo electrónico.");
        }

        contactoManager.guardar(contacto);
        return contacto;
    }

    // LEER CONTACTO
    public List<Contacto> getAllContactos(){
        return contactoManager.obtener();
    }

    public Contacto getContactoPorId(int id) {
        if (!contactoManager.contactoExiste(id)) {
            throw new NoSuchElementException("Contacto con ID: " + id + " no existe");
        }
        
        // Necesitaríamos agregar un método obtenerPorId en el ContactoManager
        // Por ahora, filtramos de la lista completa (no es eficiente para grandes volúmenes)
        return contactoManager.obtener().stream()
                .filter(contacto -> contacto.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Contacto no encontrado"));
    }

    public Contacto getContactoPorCorreo(String correo) {
        try {
            int id = contactoManager.findId(correo);
            return getContactoPorId(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException("Contacto con correo: " + correo + " no existe");
        }
    }

    // ACTUALIZAR CONTACTO
    public Contacto actualizarContacto(int id, CamposContacto campo, Object valor) {
        if (!contactoManager.contactoExiste(id)) {
            throw new IllegalArgumentException("Contacto con ID: " + id + " no existe");
        }
        
        contactoManager.actualizar(id, campo, valor);
        
        return getContactoPorId(id);
    }

    // Actualización completa del contacto
    public Contacto actualizarContactoCompleto(int id, Contacto contactoActualizado) {
        if (!contactoManager.contactoExiste(id)) {
            throw new IllegalArgumentException("Contacto con ID: " + id + " no existe");
        }
        
        Contacto contactoExistente = getContactoPorId(id);
        
        // Validar y actualizar campos
        if (contactoActualizado.getNombre() != null && 
            ValidacionModelo.isStringNull(contactoActualizado.getNombre()) && 
            contactoActualizado.getNombre().length() <= 50) {
            contactoManager.actualizar(id, CamposContacto.NOMBRE, contactoActualizado.getNombre());
        }
        
        if (contactoActualizado.getApellido() != null && 
            ValidacionModelo.isStringNull(contactoActualizado.getApellido()) && 
            contactoActualizado.getApellido().length() <= 50) {
            contactoManager.actualizar(id, CamposContacto.APELLIDO, contactoActualizado.getApellido());
        }
        
        if (contactoActualizado.getEmpresa() != null && 
            ValidacionModelo.isStringNull(contactoActualizado.getEmpresa()) && 
            contactoActualizado.getEmpresa().length() <= 50) {
            contactoManager.actualizar(id, CamposContacto.EMPRESA, contactoActualizado.getEmpresa());
        }
        
        if (contactoActualizado.getTelefono() != null && 
            ValidacionModelo.isTelefonoValid(contactoActualizado.getTelefono())) {
            contactoManager.actualizar(id, CamposContacto.TELEFONO, contactoActualizado.getTelefono());
        }
        
        if (contactoActualizado.getCorreo() != null && 
            ValidacionModelo.isStringNull(contactoActualizado.getCorreo()) && 
            contactoActualizado.getCorreo().length() <= 100) {
            // Verificar si el correo ya existe (excepto para este contacto)
            if (!contactoActualizado.getCorreo().equals(contactoExistente.getCorreo()) && 
                contactoManager.correoExiste(contactoActualizado.getCorreo())) {
                throw new IllegalArgumentException("Ya existe otro contacto con este correo electrónico.");
            }
            contactoManager.actualizar(id, CamposContacto.CORREO, contactoActualizado.getCorreo());
        }
        
        return getContactoPorId(id);
    }

    // ELIMINAR CONTACTO
    public void eliminarContacto(int id) {
        if (!contactoManager.contactoExiste(id)) {
            throw new IllegalArgumentException("Contacto con ID: " + id + " no existe");
        }
        
        contactoManager.eliminar(id);
    }

}
