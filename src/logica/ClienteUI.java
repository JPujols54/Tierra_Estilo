package logica;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ClienteUI extends JFrame {
    private JComboBox<String> comboTipo, comboColor, comboTalla;
    private JTextField campoTexto;
    private JLabel vistaImagen;
    private Image imagenSeleccionada = null;

    public ClienteUI(String nombreCliente, int idCliente) {
        setTitle("Bienvenido " + nombreCliente + " - Personaliza tu prenda");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de opciones
        JPanel panelOpciones = new JPanel(new GridLayout(6, 2));
        comboTipo = new JComboBox<>(new String[]{"Camiseta", "Sudadera", "Gorra"});
        comboColor = new JComboBox<>(new String[]{"Negro", "Blanco", "Rojo", "Azul"});
        comboTalla = new JComboBox<>(new String[]{"S", "M", "L", "XL"});

        campoTexto = new JTextField();

        JButton btnSubirImagen = new JButton("Subir imagen");
        vistaImagen = new JLabel("Vista previa", SwingConstants.CENTER);
        vistaImagen.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton btnGuardar = new JButton("Guardar diseño");

        btnSubirImagen.addActionListener(e -> subirImagen());
        btnGuardar.addActionListener(e -> guardarPrenda(idCliente));

        panelOpciones.add(new JLabel("Tipo:"));
        panelOpciones.add(comboTipo);
        panelOpciones.add(new JLabel("Color:"));
        panelOpciones.add(comboColor);
        panelOpciones.add(new JLabel("Talla:"));
        panelOpciones.add(comboTalla);
        panelOpciones.add(new JLabel("Texto personalizado:"));
        panelOpciones.add(campoTexto);
        panelOpciones.add(new JLabel("Imagen:"));
        panelOpciones.add(btnSubirImagen);
        panelOpciones.add(new JLabel(""));
        panelOpciones.add(btnGuardar);

        add(panelOpciones, BorderLayout.NORTH);
        add(vistaImagen, BorderLayout.CENTER);
    }

    private void subirImagen() {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            try {
                imagenSeleccionada = ImageIO.read(archivo);
                Image scaled = imagenSeleccionada.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                vistaImagen.setIcon(new ImageIcon(scaled));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen");
            }
        }
    }

    private void guardarPrenda(int idUsuario) {
        String tipo = (String) comboTipo.getSelectedItem();
        String color = (String) comboColor.getSelectedItem();
        String talla = (String) comboTalla.getSelectedItem();
        String texto = campoTexto.getText();

        if (imagenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe subir una imagen antes de guardar.");
            return;
        }

        try (Connection con = DBConnection.conectar()) {
            String sql = "INSERT INTO prenda (tipo, color, talla, texto_personalizado, imagen, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tipo);
            ps.setString(2, color);
            ps.setString(3, talla);
            ps.setString(4, texto);

            // Convertir imagen a bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(toBufferedImage(imagenSeleccionada), "png", baos);
            byte[] bytes = baos.toByteArray();
            ps.setBytes(5, bytes);
            ps.setInt(6, idUsuario);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Prenda guardada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    // Conversión de Image a BufferedImage
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bimage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bimage;
    }
}

