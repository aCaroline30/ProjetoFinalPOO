package domain;

import java.time.LocalDateTime;
import java.util.*;

public class GerenciadorDeIngressos {
    private Map<String, Ingresso> ingressos = new LinkedHashMap<>();

    public void vender(String id, Cliente cliente, Sessao sessao, String codigoAssento, LocalDateTime momento) {
        if (cliente == null) throw new IllegalArgumentException("Cliente nulo");
        if (sessao == null) throw new IllegalArgumentException("Sessao nula");
        Assento ass = sessao.getSala().buscarAssentoPorCodigo(codigoAssento);
        if (ass == null) throw new IllegalArgumentException("Assento não existe");
        if (ass.isOcupado()) throw new IllegalArgumentException("Assento ocupado");
    
        if (cliente.getIdade() < sessao.getFilme().getClassificacaoEtaria())
            throw new IllegalArgumentException("Cliente não tem idade para este filme");

        ass.ocupar();
        Ingresso ing = new Ingresso(id, cliente, sessao, ass, sessao.getPreco(), momento);
        ingressos.put(id, ing);
        cliente.adicionarAoHistorico(sessao.getFilme().getId());
    }

    public void cancelar(String id, LocalDateTime momento) {
        Ingresso ing = ingressos.get(id);
        if (ing == null) throw new IllegalArgumentException("Ingresso não encontrado");
        if (ing.isCancelado()) throw new IllegalStateException("Ingresso já cancelado");
       
        LocalDateTime limite = ing.getSessao().getInicio().minusMinutes(30);
        if (momento.isAfter(limite)) throw new IllegalArgumentException("Prazo de cancelamento expirado");
        ing.cancelar(momento);
    }

    public Ingresso buscarPorId(String id) { return ingressos.get(id); }
    public void remover(String id) { ingressos.remove(id); }
    public List<Ingresso> listarTodos() { return new ArrayList<>(ingressos.values()); }
}

