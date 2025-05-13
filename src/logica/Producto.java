package logica;

public class Producto {
    private String nombre;
    private String talla;
    private double precio;

    public Producto(String nombre, String talla, double precio) {
        this.nombre = nombre;
        this.talla = talla;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTalla() {
        return talla;
    }

    public double getPrecio() {
        return precio;
    }
}