package com.agenda.controller;

import com.agenda.dao.ContactoDAO;
import com.agenda.model.Contacto;
import com.agenda.view.AgendaView;
import com.agenda.view.ContactoDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgendaController {
    private final AgendaView vista;
    private final ContactoDAO dao;

    private final Set<Integer> idsParaEliminar = new HashSet<>();
    private boolean cargandoTabla = false;
    private boolean modoEliminarActivo = false;

    public AgendaController(AgendaView vista, ContactoDAO dao) {
        this.vista = vista;
        this.dao = dao;
        enlazarEventos();
        cargarContactos("");
    }

    private void enlazarEventos() {
        vista.btnNuevo.addActionListener(e -> abrirCreacion());
        vista.btnCancelar.addActionListener(e -> desactivarModoEliminar());

        vista.btnEliminar.addActionListener(e -> {
            if (!modoEliminarActivo) {
                activarModoEliminar();
            } else {
                ejecutarEliminacion();
            }
        });

        vista.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }

            private void filtrar() {
                cargarContactos(vista.txtBuscar.getText().trim());
            }
        });

        vista.tablaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tablaContactos.rowAtPoint(e.getPoint());
                int columna = vista.tablaContactos.columnAtPoint(e.getPoint());
                if (columna == 6 && fila >= 0 && !modoEliminarActivo) {
                    abrirEdicion(fila);
                }
            }
        });

        vista.modeloTabla.addTableModelListener(e -> {
            if (!cargandoTabla && e.getColumn() == 0) {
                int fila = e.getFirstRow();
                if (fila >= 0) {
                    boolean estaMarcado = (Boolean) vista.modeloTabla.getValueAt(fila, 0);
                    int id = (Integer) vista.modeloTabla.getValueAt(fila, 1);
                    if (estaMarcado) {
                        idsParaEliminar.add(id);
                    } else {
                        idsParaEliminar.remove(id);
                    }
                }
            }
        });
    }

    private void activarModoEliminar() {
        modoEliminarActivo = true;
        idsParaEliminar.clear();
        vista.alternarModoEliminar(true);
        cargarContactos(vista.txtBuscar.getText().trim());
    }

    private void desactivarModoEliminar() {
        modoEliminarActivo = false;
        idsParaEliminar.clear();
        vista.alternarModoEliminar(false);
        cargarContactos(vista.txtBuscar.getText().trim());
    }

    private void ejecutarEliminacion() {
        if (idsParaEliminar.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Selecciona al menos un contacto marcando la casilla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            desactivarModoEliminar();
            return;
        }

        int resp = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas eliminar permanentemente los " + idsParaEliminar.size()
                        + " contactos seleccionados?",
                "Confirmación de Borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            for (Integer id : idsParaEliminar) {
                dao.eliminar(id);
            }
            JOptionPane.showMessageDialog(vista, "Contactos eliminados exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        desactivarModoEliminar();
    }

    private void abrirCreacion() {
        ContactoDialog dialog = new ContactoDialog(vista, null);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            dao.insertar(dialog.getContacto());
            cargarContactos(vista.txtBuscar.getText().trim());
        }
    }

    private void abrirEdicion(int fila) {
        int id = Integer.parseInt(vista.modeloTabla.getValueAt(fila, 1).toString());
        String nombre = vista.modeloTabla.getValueAt(fila, 2).toString();
        String telefono = vista.modeloTabla.getValueAt(fila, 3).toString();
        String correo = vista.modeloTabla.getValueAt(fila, 4).toString();
        String direccion = vista.modeloTabla.getValueAt(fila, 5).toString();

        Contacto seleccionado = new Contacto(id, nombre, telefono, correo, direccion);
        ContactoDialog dialog = new ContactoDialog(vista, seleccionado);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            dao.actualizar(dialog.getContacto());
            cargarContactos(vista.txtBuscar.getText().trim());
        }
    }

    private void cargarContactos(String criterio) {
        cargandoTabla = true;
        vista.modeloTabla.setRowCount(0);
        List<Contacto> lista = dao.listar(criterio);
        for (Contacto c : lista) {
            boolean marcado = idsParaEliminar.contains(c.getId());
            vista.modeloTabla.addRow(new Object[] {
                    marcado, c.getId(), c.getNombre(), c.getTelefono(), c.getCorreo(), c.getDireccion(), ""
            });
        }
        cargandoTabla = false;
    }
}