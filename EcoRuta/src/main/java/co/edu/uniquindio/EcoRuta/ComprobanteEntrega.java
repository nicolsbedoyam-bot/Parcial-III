package co.edu.uniquindio.EcoRuta;

public record ComprobanteEntrega(String codigoEntrega, String identificacionAportante, int pesoTotalKg, double valorTotal) {

    @Override
    public String toString(){
        return "ComprobanteEntrega:" + codigoEntrega + "-" + identificacionAportante + "-" + pesoTotalKg + "-" + valorTotal;
    }

    @Override
    public String codigoEntrega() {
        return codigoEntrega;
    }

    @Override
    public String identificacionAportante() {
        return identificacionAportante;
    }

    @Override
    public int pesoTotalKg() {
        return pesoTotalKg;
    }

    @Override
    public double valorTotal() {
        return valorTotal;
    }
}
