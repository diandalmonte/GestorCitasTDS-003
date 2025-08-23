package com.jamd.modelo;

public class Contacto {
    private int id;
    private String nombre, apellido, empresa, telefono, correo;

    public Contacto(){}
   
    //Este constructor sera para retornar clases de la BD, ya que el id solo lo asigna la BD
    public Contacto(int id, String nombre, String apellido, String empresa, String telefono, String correo){
        this.id = id;
        setNombre(nombre);
        setApellido(apellido);
        setEmpresa(empresa);
        setTelefono(telefono);
        setCorreo(correo);
    }

    //Constructores para enviar las clases a la BD:

    public Contacto(String nombre, String apellido, String empresa, String telefono, String correo){
        setNombre(nombre);
        setApellido(apellido);
        setEmpresa(empresa);
        setTelefono(telefono);
        setCorreo(correo);
    }

    public Contacto(String nombre, String empresa, String telefono, String correo){
        setNombre(nombre);
        setEmpresa(empresa);
        setTelefono(telefono);
        setCorreo(correo);
    }


   public String getNombre() {
        return nombre;
    }

   public void setNombre(String nombre) {
        if (ValidacionModelo.isStringNull(nombre) && nombre.length() <= 50){
            this.nombre = nombre;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

   public String getApellido() {
        return apellido;
    }

   public void setApellido(String apellidos) {
        if (ValidacionModelo.isStringNull(apellidos) && apellidos.length() <= 50){
            this.apellido = apellidos;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

   public String getEmpresa() {
        return empresa;
    }

   public void setEmpresa(String empresa) {
        if (ValidacionModelo.isStringNull(empresa) && empresa.length() <= 50){
            this.empresa = empresa;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

   public String getTelefono() {
        return telefono;
    }

   public void setTelefono(String telefono) {
        if (ValidacionModelo.isTelefonoValid(telefono)){
            this.telefono = telefono;
        }
    }

    public String getCorreo(){
        return correo;
    }

   public void setCorreo(String correo) {
        if (ValidacionModelo.isStringNull(correo) && correo.length() <= 100){
            this.correo = correo;
        } //Issues: This doesnt do anything for when its not valid, also doesnt specify which isnt correct
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString(){
        return String.format("ID: %dNombre: %s, Apellido: %s, Empresa: %s, Telefono: %s, Correo: %s",
         getId(), getNombre(), getApellido(), getEmpresa(), getTelefono(), getCorreo());
    }

   

    
}
