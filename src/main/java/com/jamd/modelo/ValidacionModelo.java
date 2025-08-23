package com.jamd.modelo;

import java.time.LocalDateTime;

public class ValidacionModelo {

    public static boolean isStringNull(String string){
        if (string == null || string == ""){
            return false;
        }
        return true;
    }

    public static boolean isTelefonoValid(String telefono){
        if (telefono.length() > 10 || telefono.length() < 11){
            try{
                Double.parseDouble(telefono);
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        } else { return false; }
    }

    public static boolean isFechaValid(LocalDateTime fecha){
        if (fecha.isAfter(LocalDateTime.now())){
            return true;
        } else {return false;}
    }

    /*public static boolean isStringLengthValid(String string, int maxLengthAllowed){
        if (string.length() > maxLengthAllowed){
            return false;
        } else { return true; }
    } This seems a bit unnecessary*/
    
    
}