package domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sessao {
    private String id;
    private Filme filme;
    private Sala sala;
    private LocalDateTime inicio;
    private double preco;

    public Sessao() {}

    public Sessao(String id, Filme filme, Sala sala, LocalDateTime inicio, double preco) {
        this.id = id;
        this.filme = filme;
        this.sala = sala;
        this.inicio = inicio;
        this.preco = preco;
    }

    public String getId() { return id; }
    public Filme getFilme() { return filme; }
    public Sala getSala() { return sala; }
    public LocalDateTime getInicio() { return inicio; }
    public double getPreco() { return preco; }

    public LocalDateTime getFim() {
        if (filme == null || filme.getDuracao() == null) return inicio;
        return inicio.plus(filme.getDuracao());
    }

    public boolean conflitaCom(Sessao outra) {
        if (!this.sala.equals(outra.sala)) return false;
        LocalDateTime inicioA = this.inicio;
        LocalDateTime fimA = this.getFim();
        LocalDateTime inicioB = outra.inicio;
        LocalDateTime fimB = outra.getFim();
        return inicioA.isBefore(fimB) && inicioB.isBefore(fimA);
    }

   
    public String toString() {
        return String.format("%s - %s - Sala:%s - %s - R$ %.2f", id, filme == null ? "??" : filme.getTitulo(), sala == null ? "??" : sala.getNome(), inicio.toString(), preco);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sessao)) return false;
        Sessao sessao = (Sessao) o;
        return Objects.equals(id, sessao.id);
    }

   
    public int hashCode() {
        return Objects.hash(id);
    }
}
