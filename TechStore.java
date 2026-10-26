import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TechStore {
    private static final String ARCHIVO_TXT = "inventario.txt";
    private static final String ARCHIVO_DAT = "inventario.dat";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("");
            switch (opcion) {
                case 1: registrarProducto(); break;
                case 2: mostrarInventario(); break;
                case 3: buscarProducto(); break;
                case 4: calcularValorInventario(); break;
                case 5: crearCopiaSeguridad(); break;
                case 6: recuperarCopiaSeguridad(); break;
                case 7: System.out.println("Hasta luego."); break;
                default: System.out.println("Opcion invalida. Elija un numero del 1 al 7.");
            }
        } while (opcion != 7);
        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========== TECHSTORE ==========");
        System.out.println("   SISTEMA DE INVENTARIO");
        System.out.println("1. Registrar producto");
        System.out.println("2. Mostrar inventario");
        System.out.println("3. Buscar producto");
        System.out.println("4. Calcular valor del inventario");
        System.out.println("5. Crear copia de seguridad");
        System.out.println("6. Recuperar copia de seguridad");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    // ---------- Lectura segura de datos ----------

    private static int leerEntero(String mensaje) {
        while (true) {
            if (!mensaje.isEmpty()) System.out.print(mensaje);
            String entrada = sc.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Introduzca un numero entero: ");
                mensaje = "";
            }
        }
    }

    private static int leerCantidad(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= 0) return valor;
            System.out.println("La cantidad no puede ser negativa.");
            mensaje = "Cantidad: ";
        }
    }

    private static double leerPrecio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(sc.nextLine().trim());
                if (valor >= 0) return valor;
                System.out.println("El precio no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Introduzca un numero (ej. 1200.50).");
            }
            mensaje = "Precio (RD$): ";
        }
    }

    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("Este campo no puede estar vacio.");
            } else if (texto.contains(";")) {
                System.out.println("No se permite el caracter ';'.");
            } else {
                return texto;
            }
        }
    }

    private static List<Producto> cargarProductos() {
        List<Producto> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO_TXT);
        if (!archivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Producto p = Producto.fromLinea(linea);
                if (p != null) lista.add(p);
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + ARCHIVO_TXT + ": " + e.getMessage());
        }
        return lista;
    }

    private static void registrarProducto() {
        System.out.println("\n--- Registrar producto ---");
        String codigo = leerTexto("Codigo: ");

        for (Producto p : cargarProductos()) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                System.out.println("Ya existe un producto con el codigo " + codigo + ".");
                return;
            }
        }

        String nombre = leerTexto("Nombre: ");
        double precio = leerPrecio("Precio (RD$): ");
        int cantidad = leerCantidad("Cantidad: ");

        Producto nuevo = new Producto(codigo, nombre, precio, cantidad);

        // append = true para no borrar los registros existentes
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_TXT, true))) {
            bw.write(nuevo.toLinea());
            bw.newLine();
            System.out.println("Producto registrado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir en " + ARCHIVO_TXT + ": " + e.getMessage());
        }
    }

    private static void mostrarInventario() {
        List<Producto> productos = cargarProductos();
        if (productos.isEmpty()) {
            System.out.println("\nEl inventario esta vacio.");
            return;
        }
        System.out.println("\n--- Inventario ---");
        System.out.printf("%-8s %-22s %15s %8s%n", "CODIGO", "NOMBRE", "PRECIO", "CANT.");
        System.out.println("-------------------------------------------------------------");
        for (Producto p : productos) {
            System.out.println(p);
        }
    }

    private static void buscarProducto() {
        System.out.print("\nCodigo a buscar: ");
        String codigo = sc.nextLine().trim();

        for (Producto p : cargarProductos()) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                System.out.println("\nProducto encontrado:");
                System.out.println("Codigo:   " + p.getCodigo());
                System.out.println("Nombre:   " + p.getNombre());
                System.out.printf("Precio:   RD$%,.2f%n", p.getPrecio());
                System.out.println("Cantidad: " + p.getCantidad());
                return;
            }
        }
        System.out.println("Producto no encontrado en el inventario");
    }

    private static void calcularValorInventario() {
        List<Producto> productos = cargarProductos();
        if (productos.isEmpty()) {
            System.out.println("\nEl inventario esta vacio.");
            return;
        }
        double total = 0;
        System.out.println("\n--- Valor del inventario ---");
        for (Producto p : productos) {
            double valor = p.getValorTotal();
            total += valor;
            System.out.printf("%-22s %d x RD$%,.2f = RD$%,.2f%n",
                    p.getNombre(), p.getCantidad(), p.getPrecio(), valor);
        }
        System.out.printf("%nVALOR TOTAL DEL INVENTARIO: RD$%,.2f%n", total);
    }

    private static void crearCopiaSeguridad() {
        List<Producto> productos = cargarProductos();
        if (productos.isEmpty()) {
            System.out.println("\nNo hay productos para respaldar.");
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DAT))) {
            oos.writeObject(productos); // ArrayList es Serializable
            System.out.println("Copia de seguridad creada en " + ARCHIVO_DAT
                    + " (" + productos.size() + " productos).");
        } catch (IOException e) {
            System.out.println("Error al crear la copia de seguridad: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void recuperarCopiaSeguridad() {
        File archivo = new File(ARCHIVO_DAT);
        if (!archivo.exists()) {
            System.out.println("\nNo existe ninguna copia de seguridad (" + ARCHIVO_DAT + ").");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            List<Producto> recuperados = (List<Producto>) ois.readObject();

            System.out.println("\n--- Productos recuperados de la copia de seguridad ---");
            System.out.printf("%-8s %-22s %15s %8s%n", "CODIGO", "NOMBRE", "PRECIO", "CANT.");
            for (Producto p : recuperados) {
                System.out.println(p);
            }

            System.out.print("\nDesea restaurar estos datos en " + ARCHIVO_TXT
                    + " (sobrescribe el actual)? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_TXT, false))) {
                    for (Producto p : recuperados) {
                        bw.write(p.toLinea());
                        bw.newLine();
                    }
                    System.out.println("Inventario restaurado correctamente.");
                }
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Error al recuperar la copia de seguridad: " + e.getMessage());
        }
    }
}