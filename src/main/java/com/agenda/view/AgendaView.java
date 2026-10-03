package com.agenda.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgendaView extends JFrame {
    public JTextField txtBuscar = new JTextField();
    
    public JButton btnNuevo = new JButton("+ Nuevo Contacto");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar"); // Botón nuevo para salir del modo eliminar

    public DefaultTableModel modeloTabla;
    public JTable tablaContactos;

    private final Color AZUL_OSCURO   = new Color(30, 58, 138);   
    private final Color AZUL_PRIMARIO = new Color(37, 99, 235);   
    private final Color AZUL_CLARO    = new Color(219, 234, 254); 
    private final Color ROJO_PELIGRO  = new Color(239, 68, 68);   
    private final Color FONDO_GRIS    = new Color(248, 250, 252); 

    public AgendaView() {
        setTitle("Directorio de Contactos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(FONDO_GRIS);

        JPanel panelPrincipal = new JPanel(new BorderLayout(20, 20));
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        panelPrincipal.setBackground(FONDO_GRIS);
        setContentPane(panelPrincipal);

        // --- BARRA SUPERIOR ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setOpaque(false);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscador Inteligente:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(AZUL_OSCURO);
        
        txtBuscar.setPreferredSize(new Dimension(300, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        estilizarBoton(btnNuevo, AZUL_PRIMARIO, Color.WHITE);
        estilizarBoton(btnEliminar, ROJO_PELIGRO, Color.WHITE);
        estilizarBoton(btnCancelar, new Color(100, 116, 139), Color.WHITE);
        
        btnCancelar.setVisible(false); // Oculto por defecto

        panelBotones.add(btnNuevo);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnEliminar);
        panelSuperior.add(panelBotones, BorderLayout.EAST);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // --- TABLA ---
        modeloTabla = new DefaultTableModel(
                new String[]{"☑", "ID", "Nombre", "Teléfono", "Correo", "Dirección", "Acción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 0; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; 
                return super.getColumnClass(columnIndex);
            }
        };

        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaContactos.setRowHeight(40); 
        tablaContactos.setSelectionBackground(AZUL_CLARO);
        tablaContactos.setSelectionForeground(AZUL_OSCURO);
        tablaContactos.setGridColor(new Color(226, 232, 240));

        DefaultTableCellRenderer renderEncabezado = new DefaultTableCellRenderer();
        renderEncabezado.setBackground(AZUL_OSCURO);
        renderEncabezado.setForeground(Color.WHITE);
        renderEncabezado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        renderEncabezado.setHorizontalAlignment(SwingConstants.CENTER);
        
        tablaContactos.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaContactos.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < tablaContactos.getColumnModel().getColumnCount(); i++) {
            tablaContactos.getColumnModel().getColumn(i).setHeaderRenderer(renderEncabezado);
        }

        // OCULTAR COLUMNA DE CHECKBOX POR DEFECTO
        tablaContactos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaContactos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaContactos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaContactos.getColumnModel().getColumn(1).setMaxWidth(50);
        tablaContactos.getColumnModel().getColumn(6).setMaxWidth(100);

        tablaContactos.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            JButton btnRender = new JButton("Editar ✏️");
            {
                btnRender.setBackground(new Color(14, 165, 233));
                btnRender.setForeground(Color.WHITE);
                btnRender.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnRender.setOpaque(true);
                btnRender.setBorderPainted(false);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return btnRender;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaContactos);
        scroll.setBorder(new LineBorder(AZUL_OSCURO, 1, true));
        panelPrincipal.add(scroll, BorderLayout.CENTER);
    }

    private void estilizarBoton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // MÉTODO PARA MOSTRAR/OCULTAR LOS CHECKS
    public void alternarModoEliminar(boolean activar) {
        if (activar) {
            tablaContactos.getColumnModel().getColumn(0).setMinWidth(40);
            tablaContactos.getColumnModel().getColumn(0).setMaxWidth(40);
            tablaContactos.getColumnModel().getColumn(0).setPreferredWidth(40);
            btnEliminar.setText("Eliminar Seleccionados");
            btnCancelar.setVisible(true);
            btnNuevo.setVisible(false);
        } else {
            tablaContactos.getColumnModel().getColumn(0).setMinWidth(0);
            tablaContactos.getColumnModel().getColumn(0).setMaxWidth(0);
            tablaContactos.getColumnModel().getColumn(0).setPreferredWidth(0);
            btnEliminar.setText("Eliminar");
            btnCancelar.setVisible(false);
            btnNuevo.setVisible(true);
        }
    }
}