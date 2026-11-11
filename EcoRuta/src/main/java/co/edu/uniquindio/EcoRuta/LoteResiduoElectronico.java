package co.edu.uniquindio.Parcial3;

public class LoteResiduoElectronico extends  Lote implements IEtiquetable{

    private int equiposCompletos;

    //Se reconocen $1.400 por kilogramo, $800 por cada equipo completo
    private static final int VALOR_KG= 1400;
    private static final int VALOR_EQUIPO_COMPLETO=800;

    public LoteResiduoElectronico(String codigo, String descripcion,int pesoKg, int equiposCompletos){
        super(codigo, descripcion, pesoKg);
        this.equiposCompletos= equiposCompletos;
    }

    @Override
    public String toString(){
        return "LoteResiduoElectronico:" + this.codigo + "-" + this.descripcion + "-" + this.pesoKg;
    }

    //Se reconocen $1.400 por kilogramo, más $800 por cada equipo completo
    @Override
    public int calcularValor(){
        return VALOR_KG * pesoKg + VALOR_EQUIPO_COMPLETO * equiposCompletos;
    }

    //Los lotes de metales y de residuos electrónicos deben generar una etiqueta
    @Override
    public String generarEtiqueta(){
        return "Lote:" + codigo + "| EquiposCompletos:" + equiposCompletos;
    }

    public int getEquiposCompletos() {
        return equiposCompletos;
    }

    public void setEquiposCompletos(int equiposCompletos) {
        this.equiposCompletos = equiposCompletos;
    }
}
