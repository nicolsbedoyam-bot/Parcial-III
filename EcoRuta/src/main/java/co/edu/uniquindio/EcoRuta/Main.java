package co.edu.uniquindio.EcoRuta;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Empresa empresa = new Empresa("EcoRuta", "12334-5");
        cargarAportesIniciales(empresa);

        int option = 0;
        do {
            option = Integer.parseInt(JOptionPane.showInputDialog(
                    "MENU ECORUTA\n"
                            + "1. Listar aportantes\n"
                            + "2. Crear entrega\n"
                            + "3. Agregar lote\n"
                            + "4. Aceptar entrega\n"
                            + "5. Consultar comprobante\n"
                            + "6. Consultar etiquetas\n"
                            + "7. Consultar valor acumulado\n"
                            + "0. Salir del sistema"));

            switch (option) {
                case 1:
                    listarAportantes(empresa);
                    break;
                case 2:
                    crearEntrega(empresa);
                    break;
                case 3:
                    agregarLote(empresa);
                    break;
                case 4:
                    aceptarEntrega(empresa);
                    break;
                case 5:
                    consultarComprobante(empresa);
                    break;
                case 6:
                    consultarEtiquetas(empresa);
                    break;
                case 7:
                    consultarValorAcumulado(empresa);
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Hasta pronto");
                    break;
                default:
                    JOptionPane.showMessageDialog((Component) null, "Opcion no valida");
                    break;
            }
        } while (option != 0);
    }

    private static void cargarAportesIniciales(Empresa empresa) {
        empresa.registrarAportante(new Aportante("1001", "Ana Gomez", "ana@gmail.com"));
        empresa.registrarAportante(new Aportante("6789", "Exito", "exito@gmail.com"));
        empresa.registrarAportante(new Aportante("7898", "Universidad Gran Colombia", "granColombia@gmial.com"));
    }

    private static void listarAportantes(Empresa empresa) {
        System.out.println("Aportantes registrados");
        for (Aportante aportante : empresa.getAportantes()) {
            System.out.println(aportante);
        }
    }

    private static void crearEntrega(Empresa empresa) {
        String codigo = JOptionPane.showInputDialog("Ingrese el codigo de la entrega: ");
        String identificacion = JOptionPane.showInputDialog("Ingrese la identificacion del aportante: ");

        if (empresa.crearEntrega(codigo, identificacion, LocalDate.now())) {
            System.out.println("Entrega " + codigo + " creada en estado ABIERTA.");
        } else if (codigo.isBlank()) {
            System.out.println("Entrega rechazada: el codigo no puede estar vacio.");
        } else if (empresa.buscarEntrega(codigo) != null) {
            System.out.println("Entrega rechazada: el codigo " + codigo + " ya existe.");
        } else {
            System.out.println("Entrega rechazada: no existe un aportante con identificacion " + identificacion + ".");
        }
    }

    private static void agregarLote(Empresa empresa) {
        String codigoEntrega = JOptionPane.showInputDialog("Codigo de la entrega: ");
        if (empresa.buscarEntrega(codigoEntrega) == null) {
            System.out.println("La entrega " + codigoEntrega + " no existe.");
            return;
        }

        int tipo = Integer.parseInt(JOptionPane.showInputDialog("Tipo de lote:\n1. Lote papel y carton\n2. Lote metal\n3. Lote residuo electronico\n\nSeleccione el tipo de lote: "));
        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo de lote no valido.");
            return;
        }

        // Datos comunes a los tres tipos de lote
        String codigoLote = JOptionPane.showInputDialog("Codigo del lote: ");
        String descripcion = JOptionPane.showInputDialog("Descripcion del lote: ");
        int pesoKg = Integer.parseInt(JOptionPane.showInputDialog("Peso en kg: "));

        Lote lote;
        if (tipo == 1) {
            int respuesta = JOptionPane.showConfirmDialog(null, "El lote esta clasificado?",
                    "Papel y carton", JOptionPane.YES_NO_OPTION);
            boolean clasificado = (respuesta == JOptionPane.YES_OPTION);
            lote = new LotePapelYCarton(codigoLote, descripcion, pesoKg, clasificado);
        } else if (tipo == 2) {
            int opcionMetal = Integer.parseInt(JOptionPane.showInputDialog("Tipo de metal:\n1. ALUMINIO\n2. ACERO\n\nSeleccione el metal: "));
            if (opcionMetal != 1 && opcionMetal != 2) {
                System.out.println("Tipo de metal no valido.");
                return;
            }
            TipoMetal tipoMetal = (opcionMetal == 1) ? TipoMetal.ALUMINIO : TipoMetal.ACERO;
            lote = new LoteMetal(codigoLote, descripcion, pesoKg, tipoMetal);
        } else {
            int equipos = Integer.parseInt(JOptionPane.showInputDialog("Equipos completos (0 o mas): "));
            if (equipos < 0) {
                System.out.println("Lote rechazado: los equipos completos deben ser un numero de 0 o mas.");
                return;
            }
            lote = new LoteResiduoElectronico(codigoLote, descripcion, pesoKg, equipos);
        }

        if (empresa.agregarLote(codigoEntrega, lote)) {
            System.out.println("Lote " + codigoLote + " agregado. Valor reconocido: $" + lote.calcularValor());
        } else {
            System.out.println("Lote rechazado: la entrega debe estar ABIERTA, el peso debe ser mayor a 0 y el codigo no puede repetirse.");
        }
    }

    private static void aceptarEntrega(Empresa empresa) {
        String codigo = JOptionPane.showInputDialog("Codigo de la entrega: ");

        if (empresa.aceptarEntrega(codigo)) {
            System.out.println("Entrega " + codigo + " ACEPTADA.");
            System.out.println(empresa.consultarComprobanteEntrega(codigo));
        } else {
            System.out.println("Aceptacion rechazada: la entrega no existe, esta vacia o ya fue aceptada.");
        }
    }

    private static void consultarComprobante(Empresa empresa) {
        String codigo = JOptionPane.showInputDialog("Codigo de la entrega: ");
        ComprobanteEntrega comprobante = empresa.consultarComprobanteEntrega(codigo);

        if (comprobante == null) {
            System.out.println("No hay comprobante: la entrega no existe o aun no ha sido aceptada.");
        } else {
            System.out.println("Comprobante");
            System.out.println("Entrega: " + comprobante.codigoEntrega());
            System.out.println("Aportante: " + comprobante.identificacionAportante());
            System.out.println("Peso total: " + comprobante.pesoTotalKg() + " kg");
            System.out.println("Valor total: $" + comprobante.valorTotal());
        }
    }

    private static void consultarEtiquetas(Empresa empresa) {
        String codigo = JOptionPane.showInputDialog("Codigo de la entrega: ");
        List<String> etiquetas = empresa.consultarEtiquetas(codigo);

        if (etiquetas.isEmpty()) {
            System.out.println("No hay etiquetas: la entrega no existe o no tiene lotes de metal ni de residuos electronicos.");
        } else {
            System.out.println("Etiquetas de la entrega " + codigo);
            for (String etiqueta : etiquetas) {
                System.out.println(etiqueta);
            }
        }
    }

    private static void consultarValorAcumulado(Empresa empresa) {
        String identificacion = JOptionPane.showInputDialog("Identificacion del aportante: ");
        double total = empresa.calcularValorAcumulado(identificacion);
        System.out.println("Valor acumulado de entregas aceptadas del aportante " + identificacion + ": $" + total);
    }

}
