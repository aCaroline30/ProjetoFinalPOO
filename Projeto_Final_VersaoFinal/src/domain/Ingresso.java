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
    private boolean cancelado = false;
    private LocalDateTime canceladoEm;

    public Ingresso() {}

    public Ingresso(String id, Cliente cliente, Sessao sessao, Assento assento, double valorPago, LocalDateTime vendidoEm) {
        this.id = id; this.cliente = cliente; this.sessao = sessao; this.assento = assento; this.valorPago = valorPago; this.vendidoEm = vendidoEm;
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
        if (cancelado) throw new IllegalStateException("Ingresso já cancelado");
        this.cancelado = true;
        this.canceladoEm = momento;
        if (assento != null) assento.liberar();
    }

    @Override
    public String toString() {
        return id + " - Cliente: " + (cliente==null? "?" : cliente.getNome()) + " - Sessao: " + (sessao==null? "?" : sessao.getId()) +
               " - Assento: " + (assento==null? "?" : assento.getCodigo()) + " - R$ " + String.format("%.2f", valorPago) + (cancelado? " (CANCELADO)" : "");
    }
}

