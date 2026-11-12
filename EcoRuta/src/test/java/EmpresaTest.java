
import co.edu.uniquindio.EcoRuta.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas funcionales de EcoRuta.
// Cada prueba deja registrados: datos de entrada, resultado esperado y resultado obtenido.
public class EmpresaTest {

    private Empresa empresa;
    private LocalDate hoy;
    private Aportante aportante;

    @BeforeEach
    void preparar() {
        empresa = new Empresa();
        hoy = LocalDate.now();
        aportante = new Aportante("1001", "Ana Gómez", "ana@correo.com");
        empresa.registrarAportante(aportante);
    }

    // Muestra en consola los datos de cada prueba.
    private void registrar(String prueba, String entrada, String esperado, String obtenido) {
        System.out.println(prueba);
        System.out.println("  Entrada:  " + entrada);
        System.out.println("  Esperado: " + esperado);
        System.out.println("  Obtenido: " + obtenido);
        System.out.println();
    }


    @Test
    @DisplayName("Prueba 1: Registro de una entrega")
    void pruebaRegistroEntrega() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);

        // Que la entrega se haya creado.
        assertNotNull(entrega);
        // Que comience en estado ABIERTA.
        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        // Que comience sin lotes.
        assertEquals(0, entrega.getLotes().size());
        // Que todavía no tenga comprobante.
        assertNull(entrega.getComprobanteEntrega());

        // No debe permitir códigos de entrega repetidos.
        assertThrows(IllegalArgumentException.class,
                () -> empresa.crearEntrega("E01", "1001", hoy));

        // No debe permitir aportantes desconocidos.
        assertThrows(IllegalArgumentException.class,
                () -> empresa.crearEntrega("E02", "9999", hoy));

        // E02 no debe existir y E01 debe conservarse.
        assertNull(empresa.buscarEntrega("E02"));
        assertSame(entrega, empresa.buscarEntrega("E01"));

        registrar("PRUEBA 1: Registro de una entrega",
                "crear E01 (aportante 1001); crear E01 otra vez; crear E02 con aportante 9999",
                "E01 ABIERTA, 0 lotes, sin comprobante; las otras 2 operaciones rechazadas",
                "E01 " + entrega.getEstadoEntrega() + ", " + entrega.getLotes().size()
                        + " lotes, comprobante " + entrega.getComprobanteEntrega()
                        + "; E02 existe: " + (empresa.buscarEntrega("E02") != null));
    }


    @Test
    @DisplayName("Prueba 2: Validación de lotes")
    void pruebaValidacionLotes() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);

        // Agregar el primer lote L01 con 5 kg.
        assertTrue(empresa.agregarLote("E01", new LotePapelYCarton("L01", "Cajas", 5, false)));

        // No debe permitir códigos de lote repetidos.
        assertFalse(empresa.agregarLote("E01", new LotePapelYCarton("L01", "Otras cajas", 3, false)));

        // No debe permitir lotes con peso igual a cero.
        assertThrows(IllegalArgumentException.class,
                () -> empresa.agregarLote("E01", new LotePapelYCarton("L02", "Sin peso", 0, false)));

        // La entrega solo debe conservar el primer lote.
        assertEquals(1, entrega.getLotes().size());

        registrar("PRUEBA 2: Validación de lotes",
                "L01 con 5 kg; otro L01; L02 con 0 kg",
                "las 2 últimas rechazadas; E01 conserva solo 1 lote",
                "lotes en E01: " + entrega.getLotes().size());
    }

    /
    @Test
    @DisplayName("Prueba 3: Rechazo de una entrega vacía")
    void pruebaEntregaVacia() {

        Entrega entrega = empresa.crearEntrega("E03", "1001", hoy);

        // No debe aceptar una entrega sin lotes.
        assertThrows(RuntimeException.class, () -> empresa.aceptarEntrega("E03"));

        // Debe seguir ABIERTA y sin comprobante.
        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        assertNull(entrega.getComprobanteEntrega());

        registrar("PRUEBA 3: Rechazo de una entrega vacía",
                "crear E03 sin lotes y aceptarla",
                "rechazada, E03 ABIERTA y sin comprobante",
                "estado " + entrega.getEstadoEntrega() + "; comprobante " + entrega.getComprobanteEntrega());
    }


    @Test
    @DisplayName("Prueba 4: Aceptación y cálculo con los tres tipos de lote")
    void pruebaAceptacionYCalculo() {

        Entrega entrega = empresa.crearEntrega("E04", "1001", hoy);

        Lote papel = new LotePapelYCarton("P01", "Papel clasificado", 10, true);
        Lote metal = new LoteMetal("M01", "Latas", 5, TipoMetal.ALUMINIO);
        Lote electronico = new LoteResiduoElectronico("R01", "Computadores", 4, 2);

        assertTrue(empresa.agregarLote("E04", papel));
        assertTrue(empresa.agregarLote("E04", metal));
        assertTrue(empresa.agregarLote("E04", electronico));

        empresa.aceptarEntrega("E04");

        // Debe pasar a ACEPTADA y tener comprobante.
        assertEquals(EstadoEntrega.ACEPTADA, entrega.getEstadoEntrega());
        ComprobanteEntrega comprobante = empresa.consultarComprobanteEntrega("E04");
        assertNotNull(comprobante);

        // Valores por lote.
        assertEquals(6000, papel.calcularValor(), 0.001);
        assertEquals(11000, metal.calcularValor(), 0.001);
        assertEquals(7200, electronico.calcularValor(), 0.001);

        // Totales del comprobante.
        assertEquals(19, comprobante.pesoTotalKg(), 0.001);
        assertEquals(24200, comprobante.valorTotal(), 0.001);

        registrar("PRUEBA 4: Aceptación y cálculo con los tres tipos de lote",
                "E04 con 10 kg papel clasificado, 5 kg aluminio, 4 kg electrónicos (2 equipos)",
                "ACEPTADA, 19 kg, $24200 (lotes: $6000, $11000, $7200)",
                entrega.getEstadoEntrega() + ", " + comprobante.pesoTotalKg() + " kg, $"
                        + comprobante.valorTotal() + " (lotes: $" + papel.calcularValor() + ", $"
                        + metal.calcularValor() + ", $" + electronico.calcularValor() + ")");
    }


    @Test
    @DisplayName("Prueba 5: Consulta de etiquetas")
    void pruebaConsultaEtiquetas() {

        // Se prepara la misma entrega aceptada de la prueba 4.
        empresa.crearEntrega("E04", "1001", hoy);
        empresa.agregarLote("E04", new LotePapelYCarton("P01", "Papel clasificado", 10, true));
        empresa.agregarLote("E04", new LoteMetal("M01", "Latas", 5, TipoMetal.ALUMINIO));
        empresa.agregarLote("E04", new LoteResiduoElectronico("R01", "Computadores", 4, 2));
        empresa.aceptarEntrega("E04");

        List<String> etiquetas = empresa.consultarEtiquetas("E04");

        // Deben ser exactamente dos etiquetas.
        assertEquals(2, etiquetas.size());

        // Etiqueta del metal: código M01 y tipo ALUMINIO.
        assertTrue(etiquetas.stream().anyMatch(e -> e.contains("M01") && e.contains("ALUMINIO")));

        // Etiqueta del electrónico: código R01 y 2 equipos completos.
        assertTrue(etiquetas.stream().anyMatch(e -> e.contains("R01") && e.contains("2")));

        registrar("PRUEBA 5: Consulta de etiquetas",
                "consultar etiquetas de E04",
                "exactamente 2 (M01 con ALUMINIO, R01 con 2 equipos)",
                etiquetas.size() + " etiquetas -> " + etiquetas);
    }
}