package domain;

import java.util.*;

public class GerenciadorDeSalas {
    private Map<String, Sala> salas = new LinkedHashMap<>();
    public void adicionar(Sala s) { salas.put(s.getId(), s); }
    public Sala buscarPorId(String id) { return salas.get(id); }
    public void remover(String id) { salas.remove(id); }
    public List<Sala> listarTodos() { return new ArrayList<>(salas.values()); }
}

