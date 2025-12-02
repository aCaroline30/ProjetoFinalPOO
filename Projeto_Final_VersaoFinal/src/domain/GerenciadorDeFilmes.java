package domain;

import java.util.*;

public class GerenciadorDeFilmes {
    private Map<String, Filme> filmes = new LinkedHashMap<>();
    public void adicionar(Filme f) { filmes.put(f.getId(), f); }
    public Filme buscarPorId(String id) { return filmes.get(id); }
    public void remover(String id) { filmes.remove(id); }
    public List<Filme> listarTodos() { return new ArrayList<>(filmes.values()); }
}

