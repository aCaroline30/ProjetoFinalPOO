package persistence;

public class SessaoDTO {
    public String id;
    public String filmeId;
    public String salaId;
    public String inicioIso; 
    public double preco;

    public SessaoDTO() {}
    public SessaoDTO(String id, String filmeId, String salaId, String inicioIso, double preco) {
        this.id = id; this.filmeId = filmeId; this.salaId = salaId; this.inicioIso = inicioIso; this.preco = preco;
    }
}
