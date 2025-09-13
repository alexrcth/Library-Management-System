import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Usuario {
    private String nombre;
    private String numeroIdentificacion;
    private ArrayList<Libro> librosPrestados;

    public Usuario(String nombre, String numeroIdentificacion) {
        this.nombre = nombre;
        this.numeroIdentificacion = numeroIdentificacion;
        this.librosPrestados = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public ArrayList<Libro> getLibrosPrestados() { return librosPrestados; }

    public void prestarLibro(Libro libro) {
        if (libro != null && libro.isDisponible()) {
            librosPrestados.add(libro);
            libro.setDisponible(false);
            System.out.println("Libro '" + libro.getTitulo() + "' prestado a " + nombre);
        } else {
            System.out.println("El libro no está disponible para préstamo");
        }
    }

    public void devolverLibro(Libro libro) {
        if (librosPrestados.contains(libro)) {
            librosPrestados.remove(libro);
            libro.setDisponible(true);
            System.out.println("Libro '" + libro.getTitulo() + "' devuelto por " + nombre);
        } else {
            System.out.println("Este usuario no tiene prestado ese libro");
        }
    }

    public void mostrarInfo() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Número de identificación: " + numeroIdentificacion);
        System.out.println("Libros prestados: " + librosPrestados.size());
        if (!librosPrestados.isEmpty()) {
            System.out.println("Libros en posesión:");
            for (Libro libro : librosPrestados) {
                System.out.println("  - " + libro.getTitulo() + " (" + libro.getAutor() + ")");
            }
        }
        System.out.println("------------------------");
    }

    // --- Persistencia ---
    public String toFileString() {
        return nombre + "," + numeroIdentificacion;
    }

    public static Usuario fromFileString(String fileString) {
        String[] partes = fileString.split(",");
        if (partes.length >= 2) {
            return new Usuario(partes[0], partes[1]);
        }
        return null;
    }
}

class Libro {
    private String titulo;
    private String autor;
    private int anioPublicacion;
    private String genero;
    private boolean disponible;

    public Libro(String titulo, String autor, int anioPublicacion, String genero) {
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.genero = genero;
        this.disponible = true;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public String getGenero() { return genero; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public void mostrarInfo() {
        System.out.println("Título: " + titulo);
        System.out.println("Autor: " + autor);
        System.out.println("Año: " + anioPublicacion);
        System.out.println("Género: " + genero);
        System.out.println("Disponible: " + (disponible ? "Sí" : "No"));
        System.out.println("------------------------");
    }

    // --- Persistencia ---
    public String toFileString() {
        return titulo + "," + autor + "," + anioPublicacion + "," + genero + "," + disponible;
    }

    public static Libro fromFileString(String fileString) {
        String[] partes = fileString.split(",");
        if (partes.length >= 5) {
            Libro libro = new Libro(partes[0], partes[1], Integer.parseInt(partes[2]), partes[3]);
            libro.setDisponible(Boolean.parseBoolean(partes[4]));
            return libro;
        }
        return null;
    }
}

class Biblioteca {
    private ArrayList<Libro> libros;
    private ArrayList<Usuario> usuarios;

    public Biblioteca() {
        this.libros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public void agregarLibro(Libro libro) { libros.add(libro); }
    public void agregarUsuario(Usuario usuario) { usuarios.add(usuario); }
    public ArrayList<Libro> getLibros() { return libros; }
    public ArrayList<Usuario> getUsuarios() { return usuarios; }

    public Libro buscarLibroPorTitulo(String titulo) {
        for (Libro libro : libros) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                return libro;
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNumeroIdentificacion().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    public ArrayList<Libro> filtrarLibrosPorGenero(String genero) {
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getGenero().equalsIgnoreCase(genero)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public ArrayList<Libro> filtrarLibrosPorAutor(String autor) {
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getAutor().equalsIgnoreCase(autor)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public ArrayList<Libro> filtrarLibrosPorDisponibilidad(boolean disponible) {
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.isDisponible() == disponible) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    // --- Guardar datos ---
    public void guardarDatos() {
        try {
            // Guardar libros
            BufferedWriter libroWriter = new BufferedWriter(new FileWriter("libros.txt"));
            for (Libro libro : libros) {
                libroWriter.write(libro.toFileString());
                libroWriter.newLine();
            }
            libroWriter.close();

            // Guardar usuarios
            BufferedWriter usuarioWriter = new BufferedWriter(new FileWriter("usuarios.txt"));
            for (Usuario usuario : usuarios) {
                usuarioWriter.write(usuario.toFileString());
                usuarioWriter.newLine();
            }
            usuarioWriter.close();

            // Guardar préstamos
            BufferedWriter prestamosWriter = new BufferedWriter(new FileWriter("prestamos.txt"));
            for (Usuario usuario : usuarios) {
                for (Libro libroPrestado : usuario.getLibrosPrestados()) {
                    prestamosWriter.write(usuario.getNumeroIdentificacion() + "," + libroPrestado.getTitulo());
                    prestamosWriter.newLine();
                }
            }
            prestamosWriter.close();

            System.out.println("Datos guardados exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    // --- Cargar datos ---
    public void cargarDatos() {
        try {
            // Cargar libros
            File libroFile = new File("libros.txt");
            if (libroFile.exists()) {
                BufferedReader libroReader = new BufferedReader(new FileReader(libroFile));
                String linea;
                while ((linea = libroReader.readLine()) != null) {
                    Libro libro = Libro.fromFileString(linea);
                    if (libro != null) {
                        libros.add(libro);
                    }
                }
                libroReader.close();
            }

            // Cargar usuarios
            File usuarioFile = new File("usuarios.txt");
            if (usuarioFile.exists()) {
                BufferedReader usuarioReader = new BufferedReader(new FileReader(usuarioFile));
                String linea;
                while ((linea = usuarioReader.readLine()) != null) {
                    Usuario usuario = Usuario.fromFileString(linea);
                    if (usuario != null) {
                        usuarios.add(usuario);
                    }
                }
                usuarioReader.close();
            }

            // Cargar préstamos
            File prestamosFile = new File("prestamos.txt");
            if (prestamosFile.exists()) {
                BufferedReader prestamosReader = new BufferedReader(new FileReader(prestamosFile));
                String linea;
                while ((linea = prestamosReader.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 2) {
                        String idUsuario = datos[0];
                        String tituloLibro = datos[1];

                        Usuario usuario = buscarUsuarioPorId(idUsuario);
                        Libro libro = buscarLibroPorTitulo(tituloLibro);

                        if (usuario != null && libro != null) {
                            usuario.prestarLibro(libro);
                        }
                    }
                }
                prestamosReader.close();
            }

            System.out.println("Datos cargados exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al cargar los datos: " + e.getMessage());
        }
    }
}

// ========================= MAIN =========================
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Biblioteca biblioteca = new Biblioteca();

        biblioteca.cargarDatos();

        int opcion;
        do {
            mostrarMenu();
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1: registrarLibros(sc, biblioteca); break;
                case 2: registrarUsuarios(sc, biblioteca); break;
                case 3: mostrarLibros(biblioteca.getLibros()); break;
                case 4: mostrarUsuarios(biblioteca.getUsuarios()); break;
                case 5: prestarLibro(sc, biblioteca); break;
                case 6: devolverLibro(sc, biblioteca); break;
                case 7: listarLibrosFiltrados(sc, biblioteca); break;
                case 8: biblioteca.guardarDatos(); break;
                case 9:
                    biblioteca.guardarDatos();
                    System.out.println("¡Gracias por usar el sistema de biblioteca!");
                    break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 9);

        sc.close();
    }

    // Método para mostrar el menú
    public static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE GESTIÓN DE BIBLIOTECA ===");
        System.out.println("1. Registrar nuevo libro");
        System.out.println("2. Registrar nuevo usuario");
        System.out.println("3. Mostrar todos los libros");
        System.out.println("4. Mostrar todos los usuarios");
        System.out.println("5. Prestar libro");
        System.out.println("6. Devolver libro");
        System.out.println("7. Listar libros (filtros)");
        System.out.println("8. Guardar datos");
        System.out.println("9. Salir");
    }

    // Método para registrar libros
    public static void registrarLibros(Scanner sc, Biblioteca biblioteca) {
        System.out.println("\n--- REGISTRAR NUEVO LIBRO ---");
        System.out.print("Título: ");
        String titulo = sc.nextLine();
        System.out.print("Autor: ");
        String autor = sc.nextLine();
        System.out.print("Año de publicación: ");
        int anio = sc.nextInt();
        sc.nextLine();
        System.out.print("Género: ");
        String genero = sc.nextLine();

        Libro nuevoLibro = new Libro(titulo, autor, anio, genero);
        biblioteca.agregarLibro(nuevoLibro);
        System.out.println("Libro registrado exitosamente!");
    }

    // Método para registrar usuarios
    public static void registrarUsuarios(Scanner sc, Biblioteca biblioteca) {
        System.out.println("\n--- REGISTRAR NUEVO USUARIO ---");
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Número de identificación: ");
        String id = sc.nextLine();

        Usuario nuevoUsuario = new Usuario(nombre, id);
        biblioteca.agregarUsuario(nuevoUsuario);
        System.out.println("Usuario registrado exitosamente!");
    }

    // Método para mostrar libros
    public static void mostrarLibros(ArrayList<Libro> libros) {
        System.out.println("\n--- LISTA DE LIBROS ---");
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            for (Libro libro : libros) {
                libro.mostrarInfo();
            }
        }
    }

    // Método para mostrar usuarios
    public static void mostrarUsuarios(ArrayList<Usuario> usuarios) {
        System.out.println("\n--- LISTA DE USUARIOS ---");
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            for (Usuario usuario : usuarios) {
                usuario.mostrarInfo();
            }
        }
    }

    // Método para prestar libros
    public static void prestarLibro(Scanner sc, Biblioteca biblioteca) {
        System.out.println("\n--- PRESTAR LIBRO ---");
        System.out.print("Ingrese el ID del usuario: ");
        String idUsuario = sc.nextLine();
        System.out.print("Ingrese el título del libro: ");
        String tituloLibro = sc.nextLine();

        Usuario usuario = biblioteca.buscarUsuarioPorId(idUsuario);
        Libro libro = biblioteca.buscarLibroPorTitulo(tituloLibro);

        if (usuario != null && libro != null) {
            usuario.prestarLibro(libro);
        } else {
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
            }
            if (libro == null) {
                System.out.println("Libro no encontrado.");
            }
        }
    }

    // Método para devolver libros
    public static void devolverLibro(Scanner sc, Biblioteca biblioteca) {
        System.out.println("\n--- DEVOLVER LIBRO ---");
        System.out.print("Ingrese el ID del usuario: ");
        String idUsuario = sc.nextLine();
        System.out.print("Ingrese el título del libro: ");
        String tituloLibro = sc.nextLine();

        Usuario usuario = biblioteca.buscarUsuarioPorId(idUsuario);
        Libro libro = biblioteca.buscarLibroPorTitulo(tituloLibro);

        if (usuario != null && libro != null) {
            usuario.devolverLibro(libro);
        } else {
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
            }
            if (libro == null) {
                System.out.println("Libro no encontrado.");
            }
        }
    }

    // Método para listar libros con filtros
    public static void listarLibrosFiltrados(Scanner sc, Biblioteca biblioteca) {
        System.out.println("\n--- FILTRAR LIBROS ---");
        System.out.println("1. Por género");
        System.out.println("2. Por autor");
        System.out.println("3. Por disponibilidad");
        System.out.print("Seleccione opción: ");

        int filtro = sc.nextInt();
        sc.nextLine();

        ArrayList<Libro> resultado;

        switch (filtro) {
            case 1:
                System.out.print("Ingrese el género: ");
                String genero = sc.nextLine();
                resultado = biblioteca.filtrarLibrosPorGenero(genero);
                break;
            case 2:
                System.out.print("Ingrese el autor: ");
                String autor = sc.nextLine();
                resultado = biblioteca.filtrarLibrosPorAutor(autor);
                break;
            case 3:
                System.out.print("¿Disponibles? (true/false): ");
                boolean disponible = sc.nextBoolean();
                sc.nextLine();
                resultado = biblioteca.filtrarLibrosPorDisponibilidad(disponible);
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        if (resultado.isEmpty()) {
            System.out.println("No se encontraron libros con esos criterios.");
        } else {
            mostrarLibros(resultado);
        }
    }
}