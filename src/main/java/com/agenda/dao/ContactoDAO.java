package com.agenda.dao;

import com.agenda.model.Contacto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactoDAO {

    public boolean insertar(Contacto contacto) {
        String sql = "INSERT INTO contactos (nombre, telefono, correo, direccion) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contacto.getNombre());
            ps.setString(2, contacto.getTelefono());
            ps.setString(3, contacto.getCorreo());
            ps.setString(4, contacto.getDireccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar contacto: " + e.getMessage());
            return false;
        }
    }

    // BÚSQUEDA AVANZADA: Filtra por Nombre, Teléfono o Correo.
    public List<Contacto> listar(String filtro) {
        List<Contacto> lista = new ArrayList<>();
        String sql = (filtro == null || filtro.trim().isEmpty())
                ? "SELECT * FROM contactos ORDER BY nombre ASC"
                : "SELECT * FROM contactos WHERE nombre LIKE ? OR telefono LIKE ? OR correo LIKE ? ORDER BY nombre ASC";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            if (filtro != null && !filtro.trim().isEmpty()) {
                String parametro = "%" + filtro.trim().toUpperCase() + "%"; // Convertimos a mayúscula para coincidir
                ps.setString(1, parametro);
                ps.setString(2, parametro);
                ps.setString(3, parametro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Contacto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("correo"),
                            rs.getString("direccion")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar contactos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Contacto contacto) {
        String sql = "UPDATE contactos SET nombre = ?, telefono = ?, correo = ?, direccion = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contacto.getNombre());
            ps.setString(2, contacto.getTelefono());
            ps.setString(3, contacto.getCorreo());
            ps.setString(4, contacto.getDireccion());
            ps.setInt(5, contacto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar contacto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM contactos WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar contacto: " + e.getMessage());
            return false;
        }
    }
}