package logica;

public class Prenda {

    private String tipo;
    private String color;
    private String talla;
    private String textoPersonalizado;
    private byte[] imagen;

    public Prenda(String tipo, String color, String talla, String textoPersonalizado, byte[] imagen) {
        this.tipo = tipo;
        this.color = color;
        this.talla = talla;
        this.textoPersonalizado = textoPersonalizado;
        this.imagen = imagen;
    }

    public String getTipo() {
        return tipo;
    }

    public String getColor() {
        return color;
    }

    public String getTalla() {
        return talla;
    }

    public String getTextoPersonalizado() {
        return textoPersonalizado;
    }

    public byte[] getImagen() {
        return imagen;
    }
}
