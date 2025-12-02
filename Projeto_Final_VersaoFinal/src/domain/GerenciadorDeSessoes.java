package domain;

import java.util.*;

public class GerenciadorDeSessoes {
    private Map<String, Sessao> sessoes = new HashMap<>();

    public void adicionar(Sessao s) throws IllegalArgumentException {
        // valida RN01: não permitir sessões sobrepostas na mesma sala
        for (Sessao existente : sessoes.values()) {
            if (s.conflitaCom(existente)) {
                throw new IllegalArgumentException("Conflito de sessão com " + existente.getId());
            }
        }
        sessoes.put(s.getId(), s);
    }

    /**
     * Edita uma sessão existente.
     * Lança IllegalArgumentException em caso de conflito ou se não existir.
     */
    public void editar(String id, Filme novoFilme, Sala novaSala, java.time.LocalDateTime novoInicio, double novoPreco) {
        Sessao atual = sessoes.get(id);
        if (atual == null) throw new IllegalArgumentException("Sessão não encontrada: " + id);

        // construir sessão temporária para validação de conflito (mesmo id)
        Sessao tentativa = new Sessao(id, novoFilme, novaSala, novoInicio, novoPreco);
        for (Sessao existente : sessoes.values()) {
            if (existente.getId().equals(id)) continue; // ignorar a própria sessão
            if (tentativa.conflitaCom(existente)) {
                throw new IllegalArgumentException("Edição causaria conflito com sessão " + existente.getId());
            }
        }

        // se ok, aplicar alterações
        atual = new Sessao(id, novoFilme, novaSala, novoInicio, novoPreco);
        sessoes.put(id, atual);
    }

    public Sessao buscarPorId(String id) {
        return sessoes.get(id);
    }

    public void remover(String id) {
        sessoes.remove(id);
    }

    public Collection<Sessao> listarTodos() {
        return sessoes.values();
    }

    public List<Sessao> listarPorFilme(String filmeId) {
        List<Sessao> resultado = new ArrayList<>();
        for (Sessao s : sessoes.values()) {
            if (s.getFilme() != null && s.getFilme().getId().equals(filmeId)) resultado.add(s);
        }
        return resultado;
    }

    public void removerTodos() {
        sessoes.clear();
    }
}
