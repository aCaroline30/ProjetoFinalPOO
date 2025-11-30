package persistence;

import com.google.gson.Gson;
import domain.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PersistenceManager {
    private Gson gson;

    public PersistenceManager() {
        this.gson = JsonAdapters.createGson();
    }

    public void save(String path,
                     GerenciadorDeFilmes gm,
                     GerenciadorDeSalas gs,
                     GerenciadorDeSessoes gss,
                     GerenciadorDeIngressos gi,
                     Collection<Cliente> clientesCollection) throws IOException {

        DataSnapshot snap = new DataSnapshot();
        snap.filmes = new ArrayList<>(gm.listarTodos());
        snap.salas = new ArrayList<>(gs.listarTodos());
        snap.clientes = new ArrayList<>(clientesCollection);

        snap.sessoes = gss.listarTodos().stream().map(s -> {
            SessaoDTO d = new SessaoDTO();
            d.id = s.getId();
            d.filmeId = s.getFilme().getId();
            d.salaId = s.getSala().getId();
            d.inicioIso = s.getInicio().toString();
            d.preco = s.getPreco();
            return d;
        }).collect(Collectors.toList());

        snap.ingressos = gi.listarTodos().stream().map(i -> {
            IngressoDTO dto = new IngressoDTO();
            dto.id = i.getId();
            dto.clienteId = i.getCliente().getId();
            dto.sessaoId = i.getSessao().getId();
            dto.assentoCodigo = i.getAssento().getCodigo();
            dto.valorPago = i.getValorPago();
            dto.vendidoEmIso = i.getVendidoEm().toString();
            dto.cancelado = i.isCancelado();
            dto.canceladoEmIso = i.getCanceladoEm() == null ? null : i.getCanceladoEm().toString();
            return dto;
        }).collect(Collectors.toList());

        String json = gson.toJson(snap);
        Files.write(Paths.get(path), json.getBytes());
    }

    public void load(String path,
                     GerenciadorDeFilmes gm,
                     GerenciadorDeSalas gs,
                     GerenciadorDeSessoes gss,
                     GerenciadorDeIngressos gi,
                     Map<String, Cliente> clientesMap) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(path)));
        DataSnapshot snap = gson.fromJson(content, DataSnapshot.class);

        // limpar gerenciadores
        gm.removerTodos();
        gs.removerTodos();
        gss.removerTodos();
        gi.removerTodos();
        if (clientesMap != null) clientesMap.clear();

        // filmes e salas
        if (snap.filmes != null) {
            for (Filme f : snap.filmes) gm.adicionar(f);
        }
        if (snap.salas != null) {
            for (Sala s : snap.salas) gs.adicionar(s);
        }

        // clientes
        if (snap.clientes != null) {
            for (Cliente c : snap.clientes) {
                clientesMap.put(c.getId(), c);
            }
        }

        // sessoes
        if (snap.sessoes != null) {
            for (SessaoDTO dto : snap.sessoes) {
                Filme f = gm.buscarPorId(dto.filmeId);
                Sala s = gs.buscarPorId(dto.salaId);
                if (f == null || s == null) {
                    System.out.println("Aviso: sessão " + dto.id + " ignorada (filme/sala não encontrados).");
                    continue;
                }
                java.time.LocalDateTime inicio = java.time.LocalDateTime.parse(dto.inicioIso);
                Sessao sessao = new Sessao(dto.id, f, s, inicio, dto.preco);
                try {
                    gss.adicionar(sessao);
                } catch (Exception e) {
                    System.out.println("Erro adicionando sessão " + dto.id + ": " + e.getMessage());
                }
            }
        }

        // ingressos
        if (snap.ingressos != null) {
            for (IngressoDTO dto : snap.ingressos) {
                Cliente c = clientesMap.get(dto.clienteId);
                Sessao s = gss.buscarPorId(dto.sessaoId);
                if (c == null || s == null) {
                    System.out.println("Aviso: ingresso " + dto.id + " ignorado (cliente/sessão não encontrados).");
                    continue;
                }
                Assento assento = s.getSala().buscarAssentoPorCodigo(dto.assentoCodigo);
                if (assento == null) {
                    System.out.println("Aviso: assento " + dto.assentoCodigo + " não encontrado na sala da sessão " + s.getId());
                    continue;
                }
                LocalDateTime vendidoEm = LocalDateTime.parse(dto.vendidoEmIso);
                Ingresso ing = new Ingresso(dto.id, c, s, assento, dto.valorPago, vendidoEm);
                if (dto.cancelado && dto.canceladoEmIso != null) {
                    ing.cancelar(LocalDateTime.parse(dto.canceladoEmIso));
                } else {
                    assento.ocupar();
                }
                gi.adicionarDireto(ing);
            }
        }
    }
}
