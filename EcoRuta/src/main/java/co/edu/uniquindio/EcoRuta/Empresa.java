package co.edu.uniquindio.EcoRuta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Empresa {


    private String nombre;
    private String nit;
    private ArrayList<Entrega> entregas;
    private ArrayList<Aportante> aportantes;
    private ComprobanteEntrega comprobanteEntrega;

    public  Empresa(String nombre, String nit) {
        this.nombre = nombre;
        this.nit = nit;
        this.entregas = new ArrayList<>();
        this.aportantes = new ArrayList<>();

    }

    public boolean registrarAportante(Aportante aportante) {

        if (aportante == null) {
            return false;
        }

        if (buscarAportante(aportante.identificacion()) != null) {
            return false;
        }

        aportantes.add(aportante);
        return true;
    }


    public Aportante buscarAportante(String identificacion) {
        for (Aportante aportante : aportantes) {
            if (aportante.identificacion().equals(identificacion)){
                return aportante;
            }

        }
        return null;
    }

    public boolean crearEntrega(String codigoEntrega, String identificacion, LocalDate fecha) {

        if (codigoEntrega == null || codigoEntrega.isBlank()) {
            return false;
        }

        if (buscarEntrega(codigoEntrega) != null) {
            return false;
        }

        Aportante aportante = buscarAportante(identificacion);

        if (aportante == null) {
            return false;
        }

        Entrega nuevaEntrega = new Entrega(codigoEntrega, fecha, aportante);

        entregas.add(nuevaEntrega);

        return true;
    }


    public Entrega buscarEntrega(String codigoEntrega){
        for (Entrega entrega : entregas) {
            if (entrega.getCodigoEntrega().equals(codigoEntrega)){
                return entrega;
            }
        }
        return null;
    }
    public boolean agregarLote(String codigoEntrega, Lote lote) {
        if (codigoEntrega == null || codigoEntrega.isBlank() || lote == null) {
            return false;
        }
        if(buscarEntrega(codigoEntrega) == null) {
            return false;
        }
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return false;
        }
       return entrega.agregarLote(lote);

    }

    public boolean aceptarEntrega(String codigoEntrega){
       Entrega entrega = buscarEntrega(codigoEntrega);
       if(entrega == null){
           return false;
       }
       return  entrega.aceptar();
    }

    public ComprobanteEntrega consultarComprobanteEntrega(String codigoEntrega){
        if (codigoEntrega == null || codigoEntrega.isBlank()) {
            return null;
        }
        if (buscarEntrega(codigoEntrega) == null) {
            return null;
        }
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return null;
        }
        return  entrega.getComprobanteEntrega();

    }

    public List<String> consultarEtiquetas(String codigoEntrega) {

        if (codigoEntrega == null || codigoEntrega.isBlank()) {
            return new ArrayList<>();
        }

        Entrega entrega = buscarEntrega(codigoEntrega);

        if (entrega == null) {
            return new ArrayList<>();
        }

        return entrega.consultarEtiquetas();
    }
    public double calcularValorAcumulado(String identificacion){
        double total = 0;
        for (Entrega entrega : entregas) {
            ComprobanteEntrega comprobante = entrega.getComprobanteEntrega();
            if (comprobante != null &&
                    comprobante.identificacionAportante().equals(identificacion))  {
                total +=comprobante.valorTotal();
            }
        }
        return total;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public ArrayList<Entrega> getEntregas() {
        return entregas;
    }

    public void setEntregas(ArrayList<Entrega> entregas) {
        this.entregas = entregas;
    }

    public ArrayList<Aportante> getAportantes() {
        return aportantes;
    }

    public void setAportantes(ArrayList<Aportante> aportantes) {
        this.aportantes = aportantes;
    }

    public ComprobanteEntrega getComprobanteEntrega() {
        return comprobanteEntrega;
    }

    public void setComprobanteEntrega(ComprobanteEntrega comprobanteEntrega) {
        this.comprobanteEntrega = comprobanteEntrega;
    }
}


