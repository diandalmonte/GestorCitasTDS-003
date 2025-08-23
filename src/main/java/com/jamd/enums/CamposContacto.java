package com.jamd.enums;

public enum CamposContacto{
    NOMBRE("nombre"),
    APELLIDO("apellido"),
    EMPRESA("empresa"),
    TELEFONO("telefono"),
    CORREO("correo");

    private final String campo;

    CamposContacto(String campo){
        this.campo = campo;
    }

    public String getValor(){
        return campo;
    }
}
