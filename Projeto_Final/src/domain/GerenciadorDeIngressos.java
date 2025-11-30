package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class GerenciadorDeIngressos {
    private Map<String, Ingresso> ingressos = new HashMap<>();

    private static final Duration PRAZO_CANCELAMENTO = Duration.ofMinutes(30);

    public Ingresso vender(String id, Cliente cliente, Sessao sessao, String codigoAssento, LocalDateTime momentoVenda) {
        Assento assento = sessao.getSala().buscarAssentoPorCodigo(codigoAssento);
        if (assento == null) throw new IllegalArgumentException("Assento não encontrado.");
        if (assento.isOcupado()) throw new IllegalArgumentException("Assento já ocupado.");
        if (cliente.getIdade() < sessao.getFilme().getClassificacaoEtaria()) {
            throw new IllegalArgumentException("Cliente não tem idade suficiente para este filme.");
        }
        assento.ocupar();
        Ingresso ingresso = new Ingresso(id, cliente, sessao, assento, sessao.getPreco(), momentoVenda);
        ingressos.put(id, ingresso);
        cliente.registrarFilmeAssistido(sessao.getFilme().getId());
        return ingresso;
    }

    public void cancelar(String ingressoId, LocalDateTime momentoCancelamento) {
        Ingresso ing = ingressos.get(ingressoId);
        if (ing == null) throw new IllegalArgumentException("Ingresso não encontrado.");
        if (ing.isCancelado()) throw new IllegalArgumentException("Ingresso já cancelado.");
        LocalDateTime inicioSessao = ing.getSessao().getInicio();
        if (Duration.between(momentoCancelamento, inicioSessao).toMinutes() < PRAZO_CANCELAMENTO.toMinutes()) {
            throw new IllegalArgumentException("Prazo de cancelamento expirado.");
        }
        ing.cancelar(momentoCancelamento);
    }

    public Ingresso buscarPorId(String id) {
        return ingressos.get(id);
    }

    public Collection<Ingresso> listarTodos() {
        return ingressos.values();
    }

    public List<Ingresso> listarPorSessao(String sessaoId) {
        List<Ingresso> res = new ArrayList<>();
        for (Ingresso i : ingressos.values()) {
            if (i.getSessao() != null && i.getSessao().getId().equals(sessaoId)) res.add(i);
        }
        return res;
    }

    public void remover(String id) {
        ingressos.remove(id);
    }

   
    public void adicionarDireto(Ingresso ingresso) {
        ingressos.put(ingresso.getId(), ingresso);
    }

    public void removerTodos() {
        ingressos.clear();
    }
}
