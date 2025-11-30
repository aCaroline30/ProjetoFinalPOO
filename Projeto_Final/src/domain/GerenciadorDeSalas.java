package domain;

import java.util.*;

public class GerenciadorDeSalas {
    private Map<String, Sala> salas = new HashMap<>();

    public void adicionar(Sala s) {
        salas.put(s.getId(), s);
    }

    public Sala buscarPorId(String id) {
        return salas.get(id);
    }

    public void remover(String id) {
        salas.remove(id);
    }

    public Collection<Sala> listarTodos() {
        return salas.values();
    }

    public void removerTodos() {
        salas.clear();
    }
}
