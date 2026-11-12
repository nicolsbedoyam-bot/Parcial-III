package co.edu.uniquindio.EcoRuta;

public class LotePapelYCarton extends  Lote {

    private boolean clasificado;

    //Se reconocen $500 por kilogramo, y $100 por kilogramo si está clasificado
    private static final int VALOR_KG= 500;
    private static final int CLASIFICADO_KG= 100;

    public LotePapelYCarton(String codigo, String descripcion,int pesoKg, boolean clasificado){
        super(codigo, descripcion, pesoKg);
        this.clasificado= clasificado;
    }
    @Override
    public String toString(){
        return "LotePapelYCarton:" + this.codigo + "-" + this.descripcion + "-" + this.pesoKg;
    }

    @Override
    public int calcularValor(){
        //Se reconocen $500 por kilogramo, más $100 por kilogramo si está clasificado
        int valorPorKg= VALOR_KG;
        if(clasificado){
            valorPorKg+= CLASIFICADO_KG;
        }
        return valorPorKg * pesoKg;
    }

    public boolean isClasificado() {
        return clasificado;
    }

    public void setClasificado(boolean clasificado) {
        this.clasificado = clasificado;
    }
}
