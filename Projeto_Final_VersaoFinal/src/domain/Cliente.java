package domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Cliente {
    private String id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private List<String> preferenciaGeneros = new ArrayList<>();
    private List<String> historicoFilmesIds = new ArrayList<>();

    public Cliente() {}
    public Cliente(String id, String nome, String email, LocalDate dataNascimento) {
        this.id = id; this.nome = nome; this.email = email; this.dataNascimento = dataNascimento;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate d) { this.dataNascimento = d; }
    public List<String> getPreferenciaGeneros() { return preferenciaGeneros; }
    public List<String> getHistoricoFilmesIds() { return historicoFilmesIds; }

    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public void adicionarAoHistorico(String filmeId) {
        if (filmeId != null) historicoFilmesIds.add(filmeId);
    }

    @Override
    public String toString() {
        return id + " - " + nome + " - " + email + " - " + (dataNascimento==null? "n/d" : dataNascimento.toString());
    }
}


