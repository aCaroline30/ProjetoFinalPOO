package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private List<String> preferenciaGeneros; // ex: ["Ação", "Drama"]
    private List<String> historicoFilmesAssistidosIds; // ids de filmes assistidos

    public Cliente() {}

    public Cliente(String id, String nome, String email, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.preferenciaGeneros = new ArrayList<>();
        this.historicoFilmesAssistidosIds = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public int getIdade() {
        if (dataNascimento == null) return 0;
        return java.time.Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public List<String> getPreferenciaGeneros() {
        if (preferenciaGeneros == null) preferenciaGeneros = new ArrayList<>();
        return preferenciaGeneros;
    }
    public List<String> getHistoricoFilmesAssistidosIds() {
        if (historicoFilmesAssistidosIds == null) historicoFilmesAssistidosIds = new ArrayList<>();
        return historicoFilmesAssistidosIds;
    }

    public void adicionarGeneroPreferido(String genero) {
        if (!getPreferenciaGeneros().contains(genero)) getPreferenciaGeneros().add(genero);
    }

    public void registrarFilmeAssistido(String filmeId) {
        getHistoricoFilmesAssistidosIds().add(filmeId);
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + getIdade() + " anos)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
