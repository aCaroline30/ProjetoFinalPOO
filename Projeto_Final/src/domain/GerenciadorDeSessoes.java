package domain;

import java.util.*;

public class GerenciadorDeSessoes {
    private Map<String, Sessao> sessoes = new HashMap<>();

    public void adicionar(Sessao s) throws IllegalArgumentException {
        for (Sessao existente : sessoes.values()) {
            if (s.conflitaCom(existente)) {
                throw new IllegalArgumentException("Conflito de sessão com " + existente.getId());
            }
        }
        sessoes.put(s.getId(), s);
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
