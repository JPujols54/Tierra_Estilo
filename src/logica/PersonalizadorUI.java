package logica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.nio.file.Files;

public class PersonalizadorUI extends JFrame {

    private JComboBox<String> comboTipo;
    private JComboBox<String> comboColor;
    private JComboBox<String> comboTalla;
    private JTextField textoPersonalizado;
    private JLabel labelImagen;
    private byte[] imagenSeleccionada = null;
    private String rutaImagenSeleccionada = "";

    public PersonalizadorUI() {
        setTitle("Tierra & Estilo - Personaliza tu prenda");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelOpciones = new JPanel(new GridLayout(6, 2));
        comboTipo = new JComboBox<>(new String[]{"Camiseta", "Sudadera", "Gorra"});
        comboColor = new JComboBox<>(new String[]{"Rojo", "Azul", "Negro", "Blanco"});
        comboTalla = new JComboBox<>(new String[]{"S", "M", "L", "XL"});
        textoPersonalizado = new JTextField();

        JButton btnSubirImagen = new JButton("Subir Imagen");
        btnSubirImagen.addActionListener(e -> subirImagen());

        JButton btnGuardar = new JButton("Guardar diseño");
        btnGuardar.addActionListener(e -> guardarPrenda());

        labelImagen = new JLabel("Vista previa aquí", SwingConstants.CENTER);
        labelImagen.setPreferredSize(new Dimension(300, 200));

        comboTipo.addActionListener(e -> actualizarVistaPrevia());

        panelOpciones.add(new JLabel("Tipo de prenda:"));
        panelOpciones.add(comboTipo);
        panelOpciones.add(new JLabel("Color:"));
        panelOpciones.add(comboColor);
        panelOpciones.add(new JLabel("Talla:"));
        panelOpciones.add(comboTalla);
        panelOpciones.add(new JLabel("Texto personalizado:"));
        panelOpciones.add(textoPersonalizado);
        panelOpciones.add(new JLabel("Imagen:"));
        panelOpciones.add(btnSubirImagen);
        panelOpciones.add(new JLabel(""));
        panelOpciones.add(btnGuardar);

        add(panelOpciones, BorderLayout.NORTH);
        add(labelImagen, BorderLayout.CENTER);

        actualizarVistaPrevia();
    }

    private void actualizarVistaPrevia() {
        String tipo = (String) comboTipo.getSelectedItem();
        String rutaBase = "imagenes/" + tipo.toLowerCase() + ".png";
        File archivo = new File(rutaBase);
        if (archivo.exists()) {
            ImageIcon base = new ImageIcon(new ImageIcon(rutaBase).getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH));
            labelImagen.setIcon(base);
            labelImagen.setText("");
        } else {
            labelImagen.setIcon(null);
            labelImagen.setText("Vista previa no disponible");
        }
    }

    private void subirImagen() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            rutaImagenSeleccionada = archivo.getAbsolutePath();
            imagenSeleccionada = leerImagenComoBytes(archivo);
            ImageIcon icono = new ImageIcon(new ImageIcon(rutaImagenSeleccionada).getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH));
            labelImagen.setIcon(icono);
            labelImagen.setText("");
        }
    }

    private byte[] leerImagenComoBytes(File archivo) {
        try {
            return Files.readAllBytes(archivo.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarPrenda() {
        Prenda prenda = new Prenda(
                (String) comboTipo.getSelectedItem(),
                (String) comboColor.getSelectedItem(),
                (String) comboTalla.getSelectedItem(),
                textoPersonalizado.getText(),
                imagenSeleccionada
        );
        guardarEnBD(prenda);
    }

    private void guardarEnBD(Prenda prenda) {
        Connection con = DBConnection.conectar();
        if (con != null) {
            try {
                String sql = "INSERT INTO prenda (tipo, color, talla, texto_personalizado, imagen) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, prenda.getTipo());
                ps.setString(2, prenda.getColor());
                ps.setString(3, prenda.getTalla());
                ps.setString(4, prenda.getTextoPersonalizado());
                ps.setBytes(5, prenda.getImagen());
                ps.executeUpdate();
                ps.close();
                con.close();
                JOptionPane.showMessageDialog(this, "Diseño guardado en base de datos.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar en BD: " + e.getMessage());
            }
        }
    }
}
