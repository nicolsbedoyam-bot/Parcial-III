package co.edu.uniquindio.EcoRuta;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmpresaTest {

    private Empresa empresa;
    private LocalDate hoy;
    private Aportante aportante;

    @BeforeEach
    void preparar() {
        // Empresa pide nombre y NIT en el constructor.
        empresa = new Empresa("EcoRuta", "900123456");
        hoy = LocalDate.now();

        aportante = new Aportante("1001", "Ana Gómez", "ana@correo.com");
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

        // crearEntrega devuelve true o false, no la entrega.
        boolean creada = empresa.crearEntrega("E01", "1001", hoy);
        boolean repetida = empresa.crearEntrega("E01", "1001", hoy);
        boolean desconocido = empresa.crearEntrega("E02", "9999", hoy);

        // La entrega se busca después de crearla.
        Entrega entrega = empresa.buscarEntrega("E01");

        registrar(
                "PRUEBA 1: Registro de una entrega",
                "Crear E01; repetir E01; crear E02 con aportante desconocido",
                "E01 ABIERTA, sin lotes y sin comprobante; las otras dos rechazadas",
                "E01 creada: " + creada
                        + ", E01 repetida: " + repetida
                        + ", E02 desconocido: " + desconocido
                        + ", estado: " + entrega.getEstadoEntrega()
                        + ", lotes: " + entrega.getLotes().size()
                        + ", comprobante: " + entrega.getComprobanteEntrega()
        );

        assertTrue(creada);
        assertNotNull(entrega);
        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        assertEquals(0, entrega.getLotes().size());
        assertNull(entrega.getComprobanteEntrega());

        assertFalse(repetida);
        assertFalse(desconocido);
        assertNull(empresa.buscarEntrega("E02"));
    }

    // PRUEBA 2: Validación de lotes

    @Test
    @DisplayName("Prueba 2: Validación de lotes")
    void pruebaValidacionLotes() {

        empresa.crearEntrega("E01", "1001", hoy);
        Entrega entrega = empresa.buscarEntrega("E01");

        // 1. Primer lote: debe quedar 1 lote.
        empresa.agregarLote("E01", new LotePapelYCarton("L01", "Cajas", 5, false));
        int lotesDespuesDelPrimero = entrega.getLotes().size();

        // 2. Código repetido: debe seguir con 1 lote.
        empresa.agregarLote("E01", new LotePapelYCarton("L01", "Otras cajas", 3, false));
        int lotesDespuesDelRepetido = entrega.getLotes().size();

        // 3. Lote con 0 kg: debe seguir con 1 lote.
        empresa.agregarLote("E01", new LotePapelYCarton("L02", "Sin peso", 0, false));
        int lotesDespuesDelSinPeso = entrega.getLotes().size();

        registrar(
                "PRUEBA 2: Validación de lotes",
                "L01 con 5 kg; repetir L01; crear L02 con 0 kg",
                "Solo se agrega el primer lote (1 lote al final)",
                "Lotes después de L01: " + lotesDespuesDelPrimero
                        + ", después del repetido: " + lotesDespuesDelRepetido
                        + ", después del de 0 kg: " + lotesDespuesDelSinPeso
        );

        assertEquals(1, lotesDespuesDelPrimero);
        assertEquals(1, lotesDespuesDelRepetido);
        assertEquals(1, lotesDespuesDelSinPeso);
    }
}