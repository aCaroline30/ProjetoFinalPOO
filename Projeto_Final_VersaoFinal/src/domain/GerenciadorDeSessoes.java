package domain;

import java.util.*;

public class GerenciadorDeSessoes {
    private Map<String, Sessao> sessoes = new LinkedHashMap<>();
    public void adicionar(Sessao s) {
      
        for (Sessao e : sessoes.values()) {
            if (s.conflitaCom(e)) throw new IllegalArgumentException("Conflito de horário com sessão: " + e.getId());
        }
        sessoes.put(s.getId(), s);
    }
    public Sessao buscarPorId(String id) { return sessoes.get(id); }
    public void editar(String id, Filme f, Sala sala, java.time.LocalDateTime inicio, double preco) {
        Sessao s = buscarPorId(id);
        if (s == null) throw new IllegalArgumentException("Sessão não encontrada");
        Sessao temp = new Sessao(id, f, sala, inicio, preco);
        for (Sessao e : sessoes.values()) {
            if (!e.getId().equals(id) && temp.conflitaCom(e)) throw new IllegalArgumentException("Conflito de horário");
        }
        s.setFilme(f); s.setSala(sala); s.setInicio(inicio); s.setPreco(preco);
    }
    public void remover(String id) { sessoes.remove(id); }
    public List<Sessao> listarTodos() { return new ArrayList<>(sessoes.values()); }
    public List<Sessao> listarPorFilme(String filmeId) {
        List<Sessao> res = new ArrayList<>();
        for (Sessao s : sessoes.values()) if (s.getFilme()!=null && filmeId.equals(s.getFilme().getId())) res.add(s);
        return res;
    }
}

