package com.jamd.dao;

import java.util.List;

public interface CrudManager<T, E extends Enum<E>> {

    public void guardar(T obj);

    public void actualizar(int id, E campo, Object valor);

    public List<T> obtener();

    public void eliminar(int id);
}
