package co.edu.uniquindio.EcoRuta;

public class LoteMetal extends Lote implements IEtiquetable{

    private TipoMetal tipoMetal;

    //Se reconocen $2.200 por kilogramo de aluminio y $900 por kilogramo de acero
    private static final int VALOR_KG_ALUMINIO= 2200;
    private static final int VALOR_KG_ACERO= 900;

    public LoteMetal(String codigo, String descripcion,int pesoKg, TipoMetal tipoMetal){
        super(codigo, descripcion, pesoKg);
        this.tipoMetal= tipoMetal;
    }

    @Override
    public String toString(){
        return "LoteMetal:" + this.codigo + "-" + this.descripcion + "-" + this.pesoKg;
    }

    @Override
    public int calcularValor(){
        int valorPorKg;
        if(tipoMetal == TipoMetal.ALUMINIO){
            valorPorKg= VALOR_KG_ALUMINIO;
        }else{
            valorPorKg= VALOR_KG_ACERO;
        }
        return valorPorKg* pesoKg;
    }

    //Los lotes de metales y de residuos electrónicos deben generar una etiqueta
    @Override
    public String generarEtiqueta(){
        return "Lote:" + codigo + "| Metal:" + tipoMetal;
    }

    public TipoMetal getTipoMetal() {
        return tipoMetal;
    }

    public void setTipoMetal(TipoMetal tipoMetal) {
        this.tipoMetal = tipoMetal;
    }
}
