package com.jamd.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import com.jamd.enums.CamposContacto;
import com.jamd.modelo.Contacto;

@Repository
public class ContactoManager implements CrudManager<Contacto, CamposContacto>{
    ManagerDB db;
    
    public ContactoManager(ManagerDB db){
        this.db = db;
    }
    
    @Override
    public void guardar(Contacto contacto){
        String query = "INSERT INTO Contactos(nombre, apellido, empresa, telefono, correo) " +
                        "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = db.conectar();//!!!!!Consider what to do here instead of hardcoding that  eliminar has admin
            PreparedStatement pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            pStatement.setString(1, contacto.getNombre());
            pStatement.setString(2, contacto.getApellido());
            pStatement.setString(3, contacto.getEmpresa());
            pStatement.setString(4, contacto.getTelefono());
            pStatement.setString(5, contacto.getCorreo());

            pStatement.executeUpdate();

            try (ResultSet rSet = pStatement.getGeneratedKeys()){
                if (rSet.next()){
                int id_contacto = rSet.getInt(1);
                contacto.setId(id_contacto);
                } else {
                    throw new SQLException("No se generó id_cita");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        
    }

    @Override
    public void actualizar(int id, CamposContacto campo, Object valor){
        String query = "UPDATE Contactos SET " + campo.getValor() + " = ? WHERE id_contacto = ?";
        try(Connection connection = db.conectar();
            PreparedStatement pStatement = connection.prepareStatement(query)){

            pStatement.setObject(1, valor);
            pStatement.setInt(2, id);

            pStatement.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Contacto> obtener(){
        String query = "SELECT id_contacto, nombre, apellido, empresa, telefono, correo FROM Contactos";
        List<Contacto> resultados = new ArrayList<>();
        try(Connection connection = db.conectar(); 
            
            PreparedStatement pStatement = connection.prepareStatement(query);
            ResultSet rSet = pStatement.executeQuery()){

            while (rSet.next()){
                int id = rSet.getInt("id_contacto");
                String nombre = rSet.getString("nombre");
                String apellido = rSet.getString("apellido");
                String empresa = rSet.getString("empresa");
                String telefono = rSet.getString("telefono");
                String correo = rSet.getString("correo");

                Contacto contacto = new Contacto(id, nombre, apellido, empresa, telefono, correo);
                resultados.add(contacto);
                
            }
            
            return resultados;

        } catch (Exception e){
            e.printStackTrace();
            return resultados;
        }
    } 


    @Override
    public void eliminar(int id){
        if (hasForeignKey(id)){
            String queryDeleteFK = "DELETE FROM Citas_Contactos WHERE id_contacto = ?";

            try(Connection connection = db.conectar();
            PreparedStatement pStatement = connection.prepareStatement(queryDeleteFK)){
                pStatement.setInt(1, id);
                pStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String query = "DELETE FROM Contacto WHERE id_contacto = ?";
        try(Connection connection = db.conectar(); //Consider what to do here instead of hardcoding that  eliminar has admin
            PreparedStatement pStatement = connection.prepareStatement(query)){

            pStatement.setInt(1, id);
            pStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
        

    }



    public boolean hasForeignKey(int id_contacto){
        String query = "SELECT 1 FROM Citas_Contactos WHERE id_contacto = ?";

        try(Connection connection = db.conectar();
            PreparedStatement pStatement = connection.prepareStatement(query)){

                pStatement.setInt(1, id_contacto);
                try(ResultSet rSet = pStatement.executeQuery()){
                    return (rSet.next());
                } 

        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(String.format("Error al buscar restricciones FK de contacto con ID: %d", id_contacto), e);
            
        }
    }

    public boolean contactoExiste(int id) {
        String query = "SELECT COUNT(*) FROM Contactos WHERE id_contacto = ?";

        try (Connection connection = db.conectar();
                PreparedStatement pStatement = connection.prepareStatement(query)) {
            
            pStatement.setInt(1, id);
            
            try (ResultSet rSet = pStatement.executeQuery()) {
                if (rSet.next()) {
                    int count = rSet.getInt(1);
                    return count > 0;  // Si el count es mayor a 0, porque el contacto existe, retorna true
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Contacto> obtenerPorNombre(String nombre){
        Contacto contacto = null;
        List<Contacto> resultados = new ArrayList<>();
        String query = "SELECT id_contacto, nombre, apellido, empresa, telefono, correo FROM Contactos " +
                        "WHERE nombre = ?";
        
        try (Connection connection = db.conectar(); 
            PreparedStatement pStatement = connection.prepareStatement(query)) {
            
            pStatement.setString(1, nombre);
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()){
                int id = rSet.getInt("id_contacto");
                String apellido = rSet.getString("apellido");
                String empresa = rSet.getString("empresa");
                String telefono = rSet.getString("telefono");
                String correo = rSet.getString("correo");

                contacto = new Contacto(id, nombre, apellido, empresa, telefono, correo);
                resultados.add(contacto);
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (contacto == null) {
            throw new RuntimeException("Contacto con nombre " + nombre + " no encontrada");
        }
        
        return resultados;
    }

    public boolean correoExiste(String correo) {
        try {
            findId(correo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

public Contacto findById(int id) {
    String query = "SELECT nombre, apellido, empresa, telefono, correo " +
                   "FROM Contactos WHERE id_contacto = ?";
    Contacto contacto = null;

    try (Connection connection = db.conectar();
         PreparedStatement pStatement = connection.prepareStatement(query)) {

        pStatement.setInt(1, id);
        ResultSet rSet = pStatement.executeQuery();

        if (rSet.next()) {
            String nombre = rSet.getString("nombre");
            String apellido = rSet.getString("apellido");
            String empresa = rSet.getString("empresa");
            String telefono = rSet.getString("telefono");
            String correo = rSet.getString("correo");

            contacto = new Contacto(nombre, apellido, empresa, telefono, correo);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return contacto;
}

    public int findId(String correo) throws NoSuchElementException, SQLException{
        String query = "SELECT id_contacto FROM Contactos " +
                        "WHERE correo = ?";
        try (Connection connection = db.conectar();
            PreparedStatement pStatement = connection.prepareStatement(query)){

            pStatement.setString(1, correo);
            try (ResultSet rSet =  pStatement.executeQuery()){
                if (rSet.next()){
                    return rSet.getInt("id_contacto");
                } else{
                    throw new NoSuchElementException("Correo: " + correo + " no se pudo encontrar");
                }
            }
        

            } catch (SQLException e){
                e.printStackTrace();
                throw e;
            }
    }
}
