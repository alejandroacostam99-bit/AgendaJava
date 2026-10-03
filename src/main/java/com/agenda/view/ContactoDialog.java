package com.agenda.view;

import com.agenda.model.Contacto;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class ContactoDialog extends JDialog {
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtDireccion = new JTextField();

    private final JButton btnGuardar = new JButton("Guardar Contacto");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JLabel lblTitulo = new JLabel("Registrar Nuevo Contacto");

    private Contacto contactoOriginal = null;
    private Contacto contactoResultado = null;
    private boolean confirmado = false;

    private final Color AZUL_PRIMARIO = new Color(37, 99, 235);
    private final Color AZUL_OSCURO = new Color(30, 58, 138);
    private final Color GRIS_FONDO = new Color(248, 250, 252);
    private final Color ROJO_SUAVE = new Color(239, 68, 68);

    public ContactoDialog(Frame parent, Contacto contactoAEditar) {
        super(parent, true);
        this.contactoOriginal = contactoAEditar;

        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(GRIS_FONDO);
        setLayout(new BorderLayout());

        bloquearLetrasEnTelefono();
        construirInterfaz();
        cargarDatosSiEsEdicion();
    }

    private void construirInterfaz() {
        JPanel pnlCabecera = new JPanel(new BorderLayout());
        pnlCabecera.setBackground(AZUL_PRIMARIO);
        pnlCabecera.setBorder(new EmptyBorder(20, 25, 20, 25));

        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        pnlCabecera.add(lblTitulo, BorderLayout.CENTER);
        add(pnlCabecera, BorderLayout.NORTH);

        JPanel pnlCuerpo = new JPanel(new GridLayout(8, 1, 0, 5));
        pnlCuerpo.setBackground(GRIS_FONDO);
        pnlCuerpo.setBorder(new EmptyBorder(15, 30, 15, 30));

        pnlCuerpo.add(crearEtiqueta("Nombre Completo *"));
        pnlCuerpo.add(estilizarCampo(txtNombre));

        pnlCuerpo.add(crearEtiqueta("Teléfono (Solo números)"));
        pnlCuerpo.add(estilizarCampo(txtTelefono));

        pnlCuerpo.add(crearEtiqueta("Correo Electrónico"));
        pnlCuerpo.add(estilizarCampo(txtCorreo));

        pnlCuerpo.add(crearEtiqueta("Dirección"));
        pnlCuerpo.add(estilizarCampo(txtDireccion));

        add(pnlCuerpo, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBotones.setBackground(GRIS_FONDO);

        estilizarBoton(btnCancelar, ROJO_SUAVE, Color.WHITE);
        estilizarBoton(btnGuardar, AZUL_OSCURO, Color.WHITE);

        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> validarYGuardar());
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setVerticalAlignment(SwingConstants.BOTTOM);
        return lbl;
    }

    private JTextField estilizarCampo(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private void estilizarBoton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
    }

    private void bloquearLetrasEnTelefono() {
        ((AbstractDocument) txtTelefono.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String str = fb.getDocument().getText(0, fb.getDocument().getLength());
                str = str.substring(0, offset) + (text == null ? "" : text) + str.substring(offset + length);
                if (str.matches("[0-9+]*"))
                    super.replace(fb, offset, length, text, attrs);
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text == null)
                    return;
                String str = fb.getDocument().getText(0, fb.getDocument().getLength());
                str = str.substring(0, offset) + text + str.substring(offset);
                if (str.matches("[0-9+]*"))
                    super.insertString(fb, offset, text, attr);
            }
        });
    }

    private void cargarDatosSiEsEdicion() {
        if (contactoOriginal != null) {
            setTitle("Editar Contacto");
            lblTitulo.setText("Modificar Datos: " + contactoOriginal.getNombre());
            btnGuardar.setText("Guardar Cambios");
            txtNombre.setText(contactoOriginal.getNombre());
            txtTelefono.setText(contactoOriginal.getTelefono());
            txtCorreo.setText(contactoOriginal.getCorreo());
            txtDireccion.setText(contactoOriginal.getDireccion());
        }
    }

    private void validarYGuardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // CONVERSIÓN OBLIGATORIA A MAYÚSCULAS ANTES DE GUARDAR
        contactoResultado = new Contacto(
                contactoOriginal != null ? contactoOriginal.getId() : 0,
                txtNombre.getText().trim().toUpperCase(),
                txtTelefono.getText().trim().toUpperCase(),
                txtCorreo.getText().trim().toUpperCase(),
                txtDireccion.getText().trim().toUpperCase());
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Contacto getContacto() {
        return contactoResultado;
    }
}