package co.edu.uniquindio.EcoRuta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmpresaTest {

    private Empresa empresa;
    private LocalDate hoy;
    private Aportante aportante;

    @BeforeEach
    void preparar() {
        empresa = new Empresa();
        hoy = LocalDate.now();

        aportante = new Aportante(
                "1001",
                "Ana Gómez",
                "ana@correo.com"
        );

        empresa.registrarAportante(aportante);
    }

    private void registrar(String prueba, String entrada,
                           String esperado, String obtenido) {

        System.out.println(prueba);
        System.out.println("  Entrada:  " + entrada);
        System.out.println("  Esperado: " + esperado);
        System.out.println("  Obtenido: " + obtenido);
        System.out.println();
    }

    // PRUEBA 1: Registro de una entrega

    @Test
    @DisplayName("Prueba 1: Registro de una entrega")
    void pruebaRegistroEntrega() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);

        assertNotNull(entrega);
        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        assertEquals(0, entrega.getLotes().size());
        assertNull(entrega.getComprobanteEntrega());

        // No permite códigos de entrega repetidos.
        assertThrows(IllegalArgumentException.class,
                () -> empresa.crearEntrega("E01", "1001", hoy));

        // No permite identificaciones desconocidas.
        assertThrows(IllegalArgumentException.class,
                () -> empresa.crearEntrega("E02", "9999", hoy));

        assertNull(empresa.buscarEntrega("E02"));
        assertSame(entrega, empresa.buscarEntrega("E01"));

        registrar(
                "PRUEBA 1: Registro de una entrega",
                "Crear E01; repetir E01; crear E02 con aportante desconocido",
                "E01 creada y las otras dos operaciones rechazadas",
                "Estado: " + entrega.getEstadoEntrega()
                        + ", lotes: " + entrega.getLotes().size()
                        + ", comprobante: " + entrega.getComprobanteEntrega()
        );
    }

    // PRUEBA 2: Validación de lotes

    @Test
    @DisplayName("Prueba 2: Validación de lotes")
    void pruebaValidacionLotes() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);

        // Agregar primer lote.
        assertTrue(empresa.agregarLote(
                "E01",
                new LotePapelYCarton("L01", "Cajas", 5, false)
        ));

        // Rechazar código de lote repetido.
        assertFalse(empresa.agregarLote(
                "E01",
                new LotePapelYCarton("L01", "Otras cajas", 3, false)
        ));

        // Rechazar peso igual a cero.
        assertThrows(IllegalArgumentException.class,
                () -> new LotePapelYCarton("L02", "Sin peso", 0, false));

        assertEquals(1, entrega.getLotes().size());

        registrar(
                "PRUEBA 2: Validación de lotes",
                "L01 con 5 kg; repetir L01; crear L02 con 0 kg",
                "Solo se agrega el primer lote",
                "Cantidad de lotes: " + entrega.getLotes().size()
        );
    }

    // PRUEBA 3: Rechazo de una entrega vacía

    @Test
    @DisplayName("Prueba 3: Rechazo de una entrega vacía")
    void pruebaEntregaVacia() {

        Entrega entrega = empresa.crearEntrega("E03", "1001", hoy);

        // aceptar() devuelve false cuando la entrega está vacía.
        assertFalse(empresa.aceptarEntrega("E03"));

        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        assertNull(entrega.getComprobanteEntrega());

        registrar(
                "PRUEBA 3: Rechazo de una entrega vacía",
                "Crear E03 sin lotes e intentar aceptarla",
                "Debe permanecer ABIERTA y sin comprobante",
                "Estado: " + entrega.getEstadoEntrega()
                        + ", comprobante: " + entrega.getComprobanteEntrega()
        );
    }

    // PRUEBA 4: Aceptación y cálculo de los tres tipos de lote

    @Test
    @DisplayName("Prueba 4: Aceptación y cálculo de los lotes")
    void pruebaAceptacionYCalculo() {

        Entrega entrega = empresa.crearEntrega("E04", "1001", hoy);

        LotePapelYCarton papel = new LotePapelYCarton(
                "P01", "Papel clasificado", 10, true
        );

        LoteMetal metal = new LoteMetal(
                "M01", "Latas", 5, TipoMetal.ALUMINIO
        );

        LoteResiduoElectronico electronico = new LoteResiduoElectronico(
                "R01", "Computadores", 4, 2
        );

        assertTrue(empresa.agregarLote("E04", papel));
        assertTrue(empresa.agregarLote("E04", metal));
        assertTrue(empresa.agregarLote("E04", electronico));

        assertTrue(empresa.aceptarEntrega("E04"));

        assertEquals(EstadoEntrega.ACEPTADA, entrega.getEstadoEntrega());

        ComprobanteEntrega comprobante =
                empresa.consultarComprobanteEntrega("E04");

        assertNotNull(comprobante);

        // Valores individuales de los lotes.
        assertEquals(6000, papel.calcularValor());
        assertEquals(11000, metal.calcularValor());
        assertEquals(7200, electronico.calcularValor());

        // Valores totales del comprobante.
        assertEquals(19, comprobante.pesoTotalKg());
        assertEquals(24200, comprobante.valorTotal(), 0.001);

        registrar(
                "PRUEBA 4: Aceptación y cálculo de los lotes",
                "10 kg papel clasificado, 5 kg aluminio y 4 kg electrónicos",
                "ACEPTADA, 19 kg y $24200",
                "Estado: " + entrega.getEstadoEntrega()
                        + ", peso: " + comprobante.pesoTotalKg()
                        + " kg, valor: $" + comprobante.valorTotal()
        );
    }

    // PRUEBA 5: Consulta de etiquetas

    @Test
    @DisplayName("Prueba 5: Consulta de etiquetas")
    void pruebaConsultaEtiquetas() {

        empresa.crearEntrega("E05", "1001", hoy);

        empresa.agregarLote(
                "E05",
                new LotePapelYCarton("P01", "Papel clasificado", 10, true)
        );

        empresa.agregarLote(
                "E05",
                new LoteMetal("M01", "Latas", 5, TipoMetal.ALUMINIO)
        );

        empresa.agregarLote(
                "E05",
                new LoteResiduoElectronico("R01", "Computadores", 4, 2)
        );

        assertTrue(empresa.aceptarEntrega("E05"));

        List<String> etiquetas = empresa.consultarEtiquetas("E05");

        assertNotNull(etiquetas);
        assertEquals(2, etiquetas.size());

        // Etiqueta del metal.
        assertTrue(etiquetas.stream().anyMatch(
                e -> e.contains("M01") && e.contains("ALUMINIO")
        ));

        // Etiqueta del residuo electrónico.
        assertTrue(etiquetas.stream().anyMatch(
                e -> e.contains("R01") && e.contains("2")
        ));

        registrar(
                "PRUEBA 5: Consulta de etiquetas",
                "Consultar las etiquetas de E05",
                "Dos etiquetas: M01 con ALUMINIO y R01 con 2 equipos",
                etiquetas.size() + " etiquetas: " + etiquetas
        );
    }
}