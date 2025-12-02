package domain;

import java.util.*;
import java.util.stream.Collectors;

public class SistemaDeRecomendacao {
    private GerenciadorDeFilmes gerenciadorDeFilmes;

    public SistemaDeRecomendacao(GerenciadorDeFilmes gerenciador) {
        this.gerenciadorDeFilmes = gerenciador;
    }

    public List<Filme> recomendarPara(Cliente cliente) {
        if (cliente.getHistoricoFilmesAssistidosIds().size() < 3 && cliente.getPreferenciaGeneros().isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> generos = new HashSet<>(cliente.getPreferenciaGeneros());
        for (String filmeId : cliente.getHistoricoFilmesAssistidosIds()) {
            Filme f = gerenciadorDeFilmes.buscarPorId(filmeId);
            if (f != null && f.getGenero() != null) generos.add(f.getGenero());
        }

        return gerenciadorDeFilmes.listarTodos().stream()
                .filter(f -> f.getGenero() != null && generos.contains(f.getGenero()) && !cliente.getHistoricoFilmesAssistidosIds().contains(f.getId()))
                .collect(Collectors.toList());
    }
}
