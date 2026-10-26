import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private double precio;
    private int cantidad;

    public Producto(String codigo, String nombre, double precio, int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public double getValorTotal() {
        return precio * cantidad;
    }

    public String toLinea() {
        return codigo + ";" + nombre + ";" + precio + ";" + cantidad;
    }

    public static Producto fromLinea(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length != 4) {
            return null;
        }
        try {
            return new Producto(
                    partes[0].trim(),
                    partes[1].trim(),
                    Double.parseDouble(partes[2].trim()),
                    Integer.parseInt(partes[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s %-22s RD$%,12.2f %8d", codigo, nombre, precio, cantidad);
    }
}