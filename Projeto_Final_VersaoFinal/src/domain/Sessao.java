package domain;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;

public class Sessao {
    private String id;
    private Filme filme;
    private Sala sala;
    private LocalDateTime inicio;
    private double preco;

    public Sessao() {}

    public Sessao(String id, Filme filme, Sala sala, LocalDateTime inicio, double preco) {
        this.id = id; this.filme = filme; this.sala = sala; this.inicio = inicio; this.preco = preco;
    }

    public String getId() { return id; }
    public Filme getFilme() { return filme; }
    public void setFilme(Filme f) { this.filme = f; }
    public Sala getSala() { return sala; }
    public void setSala(Sala s) { this.sala = s; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime d) { this.inicio = d; }
    public double getPreco() { return preco; }
    public void setPreco(double p) { this.preco = p; }

    public LocalDateTime getFim() {
        Duration dur = filme == null ? Duration.ZERO : filme.getDuracao();
        return inicio.plus(dur == null ? Duration.ZERO : dur);
    }

    public boolean conflitaCom(Sessao outra) {
        if (outra == null) return false;
        if (!this.sala.getId().equals(outra.sala.getId())) return false;
        LocalDateTime aInicio = this.inicio;
        LocalDateTime aFim = this.getFim();
        LocalDateTime bInicio = outra.inicio;
        LocalDateTime bFim = outra.getFim();
        return aInicio.isBefore(bFim) && bInicio.isBefore(aFim);
    }

    @Override
    public String toString() {
        return id + " - " + (filme==null? "?" : filme.getTitulo()) + " - " + inicio + " - Sala: " + (sala==null? "?" : sala.getNome()) + " - R$ " + String.format("%.2f", preco);
    }

    @Override
    public boolean equals(Object o) { return (o instanceof Sessao) && Objects.equals(((Sessao)o).id, id); }
    @Override
    public int hashCode() { return Objects.hash(id); }
}

