package domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {
    private String id;
    private Cliente cliente;
    private Sessao sessao;
    private Assento assento;
    private double valorPago;
    private LocalDateTime vendidoEm;
    private boolean cancelado;
    private LocalDateTime canceladoEm;

    public Ingresso() {}

    public Ingresso(String id, Cliente cliente, Sessao sessao, Assento assento, double valorPago, LocalDateTime vendidoEm) {
        this.id = id;
        this.cliente = cliente;
        this.sessao = sessao;
        this.assento = assento;
        this.valorPago = valorPago;
        this.vendidoEm = vendidoEm;
        this.cancelado = false;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Sessao getSessao() { return sessao; }
    public Assento getAssento() { return assento; }
    public double getValorPago() { return valorPago; }
    public LocalDateTime getVendidoEm() { return vendidoEm; }
    public boolean isCancelado() { return cancelado; }
    public LocalDateTime getCanceladoEm() { return canceladoEm; }

    public void cancelar(LocalDateTime momento) {
        this.cancelado = true;
        this.canceladoEm = momento;
        if (assento != null) assento.liberar();
    }

    @Override
    public String toString() {
        return String.format("%s - Cliente:%s - Sessao:%s - Assento:%s - R$ %.2f - %s",
                id, cliente == null ? "??" : cliente.getNome(), sessao == null ? "??" : sessao.getId(), assento == null ? "??" : assento.getCodigo(), valorPago, cancelado ? "CANCELADO" : "VENDIDO");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingresso)) return false;
        Ingresso ingresso = (Ingresso) o;
        return Objects.equals(id, ingresso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
