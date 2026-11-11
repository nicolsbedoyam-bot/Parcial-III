
import co.edu.uniquindio.EcoRuta.Aportante;
import co.edu.uniquindio.EcoRuta.Empresa;
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

    private Lote papel;
    private Lote metal;
    private Lote electronico;

    @BeforeEach
    void preparar() {

        empresa = new Empresa();

        hoy = LocalDate.now();

        aportante = new Aportante("1001", "Ana Gómez", "ana@correo.com");

        empresa.registrarAportante(aportante);

        papel = new LotePapelYCarton("P01", "Papel reciclable", 10, true);

        metal = new LoteMetal("M01", "Aluminio reciclable", 5, TipoMetal.ALUMINIO);

        electronico = new LoteResiduoElectronico("R01", "Equipos electrónicos", 4, 2);
    }

    @Test
    @DisplayName("Prueba 1: Registro de entregas")
    void pruebaRegistroEntrega() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);
//Que la entrega se haya creado
        assertNotNull(entrega);
        //Que comience en estado ABIERTA
        assertEquals(EstadoEntrega.ABIERTA, entrega.getEstadoEntrega());
        //Que comience sin lotes.
        assertEquals(0, entrega.getLotes().size());
        //	Que todavía no tenga comprobante.
        assertNull(entrega.getComprobanteEntrega());

        // No debe permitir códigos de entrega repetidos.
        assertThrows(IllegalArgumentException.class, () -> empresa.crearEntrega("E01", "1001", hoy));

        // No debe permitir identificaciones desconocidas.
        assertThrows(IllegalArgumentException.class, () -> empresa.crearEntrega("E02", "9999", hoy));

        assertNull(empresa.buscarEntrega("E02"));
    }

    @Test
    @DisplayName("Prueba 2: Validación de lotes")
    void pruebaValidacionLotes() {

        Entrega entrega = empresa.crearEntrega("E01", "1001", hoy);

        // Agregar el primer lote.
        assertTrue(empresa.agregarLote("E01", papel));
        // No debe permitir códigos de lote repetidos.
        assertFalse(empresa.agregarLote("E01", papel));
        // No debe permitir lotes con peso igual a cero.
        assertThrows(IllegalArgumentException.class, () -> new LotePapelYCarton("P02", "Papel", 0, true));
        assertEquals(1, entrega.getLotes().size());
    }
    //

}
