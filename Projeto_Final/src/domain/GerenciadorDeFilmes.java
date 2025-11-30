package domain;

import java.util.*;

public class GerenciadorDeFilmes {
    private Map<String, Filme> filmes = new HashMap<>();

    public void adicionar(Filme f) {
        filmes.put(f.getId(), f);
    }

    public Filme buscarPorId(String id) {
        return filmes.get(id);
    }

    public void remover(String id) {
        filmes.remove(id);
    }

    public Collection<Filme> listarTodos() {
        return filmes.values();
    }

    public void removerTodos() {
        filmes.clear();
    }
}
