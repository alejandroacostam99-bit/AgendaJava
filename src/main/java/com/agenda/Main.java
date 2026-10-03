package com.agenda;

import com.agenda.controller.AgendaController;
import com.agenda.dao.ContactoDAO;
import com.agenda.dao.DatabaseConnection;
import com.agenda.view.AgendaView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializar la base de datos SQLite y crear la tabla si no existe
        DatabaseConnection.initDatabase();

        // 2. Levantar la interfaz gráfica de forma segura
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplica el estilo visual nativo del sistema operativo (Windows)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            AgendaView vista = new AgendaView();
            ContactoDAO dao = new ContactoDAO();
            new AgendaController(vista, dao);

            vista.setVisible(true);
        });
    }
}