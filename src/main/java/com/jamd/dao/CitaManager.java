package com.jamd.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.jamd.enums.CamposCita;
import com.jamd.modelo.Cita;
import com.jamd.modelo.Contacto;

@Repository
public class CitaManager implements CrudManager<Cita, com.jamd.enums.CamposCita> {
    ManagerDB db;

    public CitaManager(ManagerDB db){
        this.db = db;
    }
    
    @Override
    public void guardar(Cita cita){
        String query = "INSERT INTO Citas(encabezado, fecha_cita, descripcion) " +
                        "VALUES (?, ?, ?)";
        try(Connection connection = db.conectar();//!!!!!Consider what to do here instead of hardcoding that  eliminar has admin
            PreparedStatement pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            pStatement.setString(1, cita.getEncabezado());
            pStatement.setTimestamp(2, Timestamp.valueOf(cita.getFecha()));
            pStatement.setString(3, cita.getDescripcion());

            pStatement.executeUpdate();

            try (ResultSet rSet = pStatement.getGeneratedKeys()){
                if (rSet.next()){
                    int id_cita = rSet.getInt(1);
                    cita.setId(id_cita);
                } else {
                    throw new SQLException("No se generó id_cita");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void guardar(Cita cita, List<Contacto> contactos){ //Metodo para crear cita asociada a contactos
        String query = "INSERT INTO Citas(encabezado, fecha_cita, descripcion) " +
                        "VALUES (?, ?, ?) ";
        String queryRelacion = "INSERT INTO Citas_Contactos(id_cita, id_contacto) " +
                                "VALUES (?, ?)";
        try(Connection connection = db.conectar();//!!!!!Consider what to do here instead of hardcoding that  eliminar has admin
            PreparedStatement pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            pStatement.setString(1, cita.getEncabezado());
            pStatement.setTimestamp(2, Timestamp.valueOf(cita.getFecha()));
            pStatement.setString(3, cita.getDescripcion());

            pStatement.executeUpdate();

            try (ResultSet rSet = pStatement.getGeneratedKeys()){
                if (rSet.next()){
                    int id_cita = rSet.getInt(1);
                    cita.setId(id_cita);
                } else {
                    throw new SQLException("No se generó id_cita");
                }
            }

            try(PreparedStatement psRelacion = connection.prepareStatement(queryRelacion)){
                for (Contacto contacto : contactos){
                psRelacion.setInt(1, cita.getId());
                psRelacion.setInt(2, contacto.getId());
                psRelacion.executeUpdate();

            }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void actualizar(int id, CamposCita campo, Object valor){
        String query = "UPDATE Citas SET " + campo.getValor() + " = ? WHERE id_cita = ?";
        try(Connection connection = db.conectar();
            PreparedStatement pStatement = connection.prepareStatement(query)){

            pStatement.setObject(1, valor);
            pStatement.setInt(2, id);

            pStatement.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Cita> obtener(){
        String query = "SELECT id_cita, encabezado, fecha_cita, descripcion FROM Citas";
        //Options to consider, hashMap??
        List<Cita> resultados = new ArrayList<>();
        try (Connection connection = db.conectar(); 
            PreparedStatement pStatement = connection.prepareStatement(query);
            ResultSet rSet = pStatement.executeQuery()){

            while (rSet.next()){
                int id = rSet.getInt("id_cita");
                String encabezado = rSet.getString("encabezado");
                // java.sql.ResultSet no tiene metodo getLocalDateTime(), entonces tomamos Timestamp y luego lo convertimos a LocalDateTime
                LocalDateTime fecha = rSet.getTimestamp("fecha_cita").toLocalDateTime();
                String descripcion = rSet.getString("descripcion");

                Cita cita = new Cita(id, encabezado, descripcion, new ArrayList<>(), fecha);
                
                cargarContactosDeCita(connection, cita);
                resultados.add(cita);
                
            }
            

        } catch (Exception e){
            e.printStackTrace();
        }
    

        return resultados;
    }

    public void eliminar(int id){
        String query = "DELETE FROM Citas WHERE id_cita = ?";
        try(Connection connection = db.conectar(); //Consider what to do here instead of hardcoding that  eliminar has admin

            PreparedStatement pStatement = connection.prepareStatement(query)){
            pStatement.setInt(1, id);

            pStatement.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }
        

    }

    public List<Cita> obtenerCitasDespuesDeFecha(LocalDateTime dateTime){
        String query = "SELECT id_cita, encabezado, fecha_cita, descripcion FROM Citas";
        List<Cita> resultados = new ArrayList<>();
        try (Connection connection = db.conectar(); 
            PreparedStatement pStatement = connection.prepareStatement(query);
            ResultSet rSet = pStatement.executeQuery()){

            while (rSet.next()){
                
                int id = rSet.getInt("id_cita");
                String encabezado = rSet.getString("encabezado");
                // java.sql.ResultSet no tiene metodo getLocalDateTime(), entonces tomamos Timestamp y luego lo convertimos a LocalDateTime
                LocalDateTime fecha = rSet.getTimestamp("fecha_cita").toLocalDateTime();
                String descripcion = rSet.getString("descripcion");

                Cita cita = new Cita(id, encabezado, descripcion, new ArrayList<>(), fecha);
                
                if (cita.getFecha().isAfter(dateTime)){
                    resultados.add(cita);
                }
                
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultados;
    }

    public boolean citaExiste(int id) {
        String query = "SELECT COUNT(*) FROM Citas WHERE id_cita = ?";

        try (Connection connection = db.conectar();
                PreparedStatement pStatement = connection.prepareStatement(query)) {
            
            pStatement.setInt(1, id);
            
            try (ResultSet rSet = pStatement.executeQuery()) {
                if (rSet.next()) {
                    int count = rSet.getInt(1);
                    return count > 0;  // Si el count es mayor a 0, porque el cita existe, retorna true
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Cita obtenerPorId(int id) {
        Cita cita = null;
        String query = "SELECT id_cita, encabezado, fecha_cita, descripcion FROM Citas WHERE id_cita = ?";
        
        try (Connection connection = db.conectar(); 
            PreparedStatement pStatement = connection.prepareStatement(query)) {
            
            pStatement.setInt(1, id);
            ResultSet rSet = pStatement.executeQuery();

            if (rSet.next()) {
                String encabezado = rSet.getString("encabezado");
                LocalDateTime fecha = rSet.getTimestamp("fecha_cita").toLocalDateTime();
                String descripcion = rSet.getString("descripcion");
                
                cita = new Cita(id, encabezado, descripcion, new ArrayList<>(), fecha);
                
                cargarContactosDeCita(connection, cita);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (cita == null) {
            throw new RuntimeException("Cita con ID " + id + " no encontrada");
        }
        
        return cita;
    }


    private void cargarContactosDeCita(Connection connection, Cita cita) {
        String queryContactos = "SELECT c.id_contacto, c.nombre, c.apellido, c.empresa, c.telefono, c.correo " +
                            "FROM Citas_Contactos cc " +
                            "JOIN Contactos c ON cc.id_contacto = c.id_contacto " +
                            "WHERE cc.id_cita = ?";
        
        try (PreparedStatement pStatement = connection.prepareStatement(queryContactos)) {
            
            pStatement.setInt(1, cita.getId());
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()) {
                int id_contacto = rSet.getInt("id_contacto");
                String nombre = rSet.getString("nombre");
                String apellido = rSet.getString("apellido");
                String empresa = rSet.getString("empresa");
                String telefono = rSet.getString("telefono");
                String correo = rSet.getString("correo");

                cita.addContacto(new Contacto(id_contacto, nombre, apellido, empresa, telefono, correo));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
