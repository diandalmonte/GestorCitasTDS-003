package com.jamd.modelo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class Cita {
    private int id;
    private String encabezado, descripcion;
    private List<Contacto> contactos;
    private LocalDateTime fecha;

    public Cita(){
        this.contactos = new ArrayList<>();
    }

    public Cita(int id, String encabezado, String descripcion, List<Contacto> contactos, LocalDateTime fecha){
        this.id = id;
        setEncabezado(encabezado);
        setDescripcion(descripcion);
        setContactos(contactos);
        setFecha(fecha);
    }

    public Cita(String encabezado, String descripcion, List<Contacto> contactos, LocalDateTime fecha){
        setEncabezado(encabezado);
        setDescripcion(descripcion);
        setContactos(contactos);
        setFecha(fecha);
    }

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        if (ValidacionModelo.isStringNull(encabezado) && encabezado.length() <= 50){
            this.encabezado = encabezado;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (ValidacionModelo.isStringNull(descripcion) && descripcion.length() <= 800){
            this.descripcion = descripcion;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    public void addContacto(Contacto contacto){
        if (this.contactos == null){
            this.contactos = new ArrayList<>();
        }
        this.contactos.add(contacto);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        if (ValidacionModelo.isFechaValid(fecha)){
            this.fecha = fecha;
        } else {
            throw new IllegalArgumentException("Fecha no puede ser pasada");
        }
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        return String.format("ID: %d Encabezado: %s Fecha: %s Contactos relacionados: %d Descripcion: %s", 
        getId(), getEncabezado(), getFecha().format(formatter), getContactos().size(), getDescripcion());
    }

    
}
