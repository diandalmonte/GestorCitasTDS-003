package com.jamd.enums;

public enum CamposCita{
    ENCABEZADO("encabezado"),
    FECHA("fecha_cita"),
    DESCRIPCION("descripcion");

    private final String campo;

    CamposCita(String campo){
        this.campo = campo;
    }

    public String getValor(){
        return campo;
    }

}

