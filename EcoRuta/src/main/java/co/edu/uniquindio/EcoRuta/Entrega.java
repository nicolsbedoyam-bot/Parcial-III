package co.edu.uniquindio.EcoRuta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Entrega {

    private String codigoEntrega;
    private LocalDate fechaEntrega;
    private EstadoEntrega estadoEntrega;
    private ArrayList<Lote> lotes;
    private Aportante aportante;
    private ComprobanteEntrega comprobanteEntrega;


    public Entrega(String codigoEntrega, LocalDate fechaEntrega, Aportante aportante){
        this.codigoEntrega = codigoEntrega;
        this.fechaEntrega = fechaEntrega;
        this.estadoEntrega = EstadoEntrega.ABIERTA;
        this.aportante = aportante;
        this.lotes = new ArrayList<>();
    }

    public boolean agregarLote(Lote lote){
        if (estadoEntrega != EstadoEntrega.ABIERTA){
            return false;
        }
        if (lote.getPesoKg() <= 0){
            return false;
        }
        if (lote.getCodigo() == null || lote.getCodigo().isBlank() || existeLote(lote.getCodigo())){
            return false;
        }
        lotes.add(lote);
        return true;
    }

    public boolean existeLote(String codigo){
        for (Lote l : lotes) {
            if (l.getCodigo().equals(codigo)){
                return true;
            }
        }
        return false;
    }

    public int calcularValorTotal(){
        int total = 0;
        for (Lote l : lotes){
            total += l.calcularValor();
        }
        return total;
    }

    public int calucularPesoTotal(){
        int pesoTotal = 0;
        for (Lote l : lotes){
            pesoTotal += l.getPesoKg();
        }
        return pesoTotal;
    }

    public boolean aceptar(){
        if (estadoEntrega != EstadoEntrega.ABIERTA){
            return false;
        }
        if (lotes.isEmpty()){
            return false;
        }
        comprobanteEntrega = new ComprobanteEntrega(codigoEntrega, aportante.identificacion(),
                calucularPesoTotal(), calcularValorTotal());
        estadoEntrega = EstadoEntrega.ACEPTADA;
        return true;
    }

    public List<String> consultarEtiquetas() {
        List<String> etiquetas = new ArrayList<>();
        for (Lote lote : lotes) {
            // ¿Este lote firmó el contrato IEtiquetable? (papel y cartón no)
            if (lote instanceof IEtiquetable) {
                IEtiquetable etiqueta = (IEtiquetable) lote;
                etiquetas.add(etiqueta.generarEtiqueta());
            }
        }
        return etiquetas;
    }

    public ComprobanteEntrega getComprobanteEntrega() {
        return comprobanteEntrega;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoEntrega getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(EstadoEntrega estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getCodigoEntrega() {
        return codigoEntrega;
    }

    public void setCodigoEntrega(String codigoEntrega) {
        this.codigoEntrega = codigoEntrega;
    }

    public ArrayList<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(ArrayList<Lote> lotes) {
        this.lotes = lotes;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public void setAportante(Aportante aportante) {
        this.aportante = aportante;
    }
}