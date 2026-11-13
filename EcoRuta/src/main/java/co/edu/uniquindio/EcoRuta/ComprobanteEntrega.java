package co.edu.uniquindio.EcoRuta;

public record ComprobanteEntrega(String codigoEntrega, String identificacionAportante, int pesoTotalKg, int valorTotal) {

    @Override
    public String toString(){
        return "ComprobanteEntrega:" + codigoEntrega + "-" + identificacionAportante + "-" + pesoTotalKg + "-" + valorTotal;
    }


}
