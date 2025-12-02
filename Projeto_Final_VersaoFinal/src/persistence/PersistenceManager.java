package persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;

public class PersistenceManager {
    private final Gson gson;

    public PersistenceManager() { this.gson = JsonAdapters.createGson(); }

  
    public static class DataSnapshot {
        public List<Filme> filmes = new ArrayList<>();
        public List<Sala> salas = new ArrayList<>();
        public List<SessaoDTO> sessoes = new ArrayList<>();
        public List<Cliente> clientes = new ArrayList<>();
        public List<IngressoDTO> ingressos = new ArrayList<>();
        public List<UsuarioDTO> usuarios = new ArrayList<>();
    }

    public static class SessaoDTO {
        public String id; public String filmeId; public String salaId; public String inicio; public double preco;
    }
    public static class IngressoDTO {
        public String id; public String clienteId; public String sessaoId; public String salaId; public String assentoCodigo;
        public double valorPago; public String vendidoEm; public boolean cancelado; public String canceladoEm;
    }
    public static class UsuarioDTO { public String id; public String username; public String senha; public String role; }

    public void save(String path, GerenciadorDeFilmes gm, GerenciadorDeSalas gs, GerenciadorDeSessoes gss, GerenciadorDeIngressos gi, Collection<Cliente> clientes, Collection<Usuario> usuarios) throws IOException {
        DataSnapshot snap = new DataSnapshot();
        snap.filmes.addAll(gm.listarTodos());
        snap.salas.addAll(gs.listarTodos());

        for (Sessao s : gss.listarTodos()) {
            SessaoDTO sd = new SessaoDTO();
            sd.id = s.getId(); sd.filmeId = s.getFilme().getId(); sd.salaId = s.getSala().getId();
            sd.inicio = s.getInicio().toString(); sd.preco = s.getPreco();
            snap.sessoes.add(sd);
        }

        snap.clientes.addAll(clientes);

        for (Ingresso i : gi.listarTodos()) {
            IngressoDTO idto = new IngressoDTO();
            idto.id = i.getId();
            idto.clienteId = i.getCliente()==null? null : i.getCliente().getId();
            idto.sessaoId = i.getSessao()==null? null : i.getSessao().getId();
            idto.salaId = i.getSessao()==null? null : i.getSessao().getSala().getId();
            idto.assentoCodigo = i.getAssento()==null? null : i.getAssento().getCodigo();
            idto.valorPago = i.getValorPago();
            idto.vendidoEm = i.getVendidoEm()==null? null : i.getVendidoEm().toString();
            idto.cancelado = i.isCancelado();
            idto.canceladoEm = i.getCanceladoEm()==null? null : i.getCanceladoEm().toString();
            snap.ingressos.add(idto);
        }

        for (Usuario u : usuarios) {
            UsuarioDTO ud = new UsuarioDTO();
            ud.id = u.getId(); ud.username = u.getUsername(); ud.senha = u.getSenha(); ud.role = u.getRole().name();
            snap.usuarios.add(ud);
        }

        try (Writer w = new FileWriter(path)) {
            gson.toJson(snap, w);
        }
    }

    public void load(String path, GerenciadorDeFilmes gm, GerenciadorDeSalas gs, GerenciadorDeSessoes gss, GerenciadorDeIngressos gi, Map<String, Cliente> clientesMap, AuthManager auth) throws IOException {
        try (Reader r = new FileReader(path)) {
            DataSnapshot snap = gson.fromJson(r, DataSnapshot.class);
      
            if (snap.filmes!=null) for (Filme f : snap.filmes) gm.adicionar(f);
           
            if (snap.salas!=null) for (Sala s : snap.salas) gs.adicionar(s);
       
            Map<String,Sessao> sessaoMap = new HashMap<>();
            if (snap.sessoes!=null) {
                for (SessaoDTO sd : snap.sessoes) {
                    Filme f = gm.buscarPorId(sd.filmeId);
                    Sala s = gs.buscarPorId(sd.salaId);
                    java.time.LocalDateTime inicio = java.time.LocalDateTime.parse(sd.inicio);
                    Sessao sessao = new Sessao(sd.id, f, s, inicio, sd.preco);
                    gss.adicionar(sessao);
                    sessaoMap.put(sessao.getId(), sessao);
                }
            }
          
            if (snap.clientes!=null) for (Cliente c : snap.clientes) clientesMap.put(c.getId(), c);
           
            if (snap.ingressos!=null) {
                for (IngressoDTO idto : snap.ingressos) {
                    Cliente c = idto.clienteId==null? null : clientesMap.get(idto.clienteId);
                    Sessao sess = idto.sessaoId==null? null : sessaoMap.get(idto.sessaoId);
                    Assento ass = null;
                    if (sess != null && idto.assentoCodigo!=null) {
                        ass = sess.getSala().buscarAssentoPorCodigo(idto.assentoCodigo);
                    }
                    Ingresso ing = new Ingresso(idto.id, c, sess, ass, idto.valorPago, idto.vendidoEm==null? null : java.time.LocalDateTime.parse(idto.vendidoEm));
                    if (idto.cancelado) {
                        ing.cancelar(idto.canceladoEm==null? java.time.LocalDateTime.now() : java.time.LocalDateTime.parse(idto.canceladoEm));
                    } else {
                        if (ass != null) ass.ocupar();
                    }
                    gi.listarTodos().add(ing); 
                 
                }
            }
         
            if (snap.usuarios!=null) {
                for (UsuarioDTO ud : snap.usuarios) {
                    Usuario u = new Usuario(ud.id, ud.username, ud.senha, Usuario.Role.valueOf(ud.role));
                    auth.adicionarUsuario(u);
                }
            }
        } catch (com.google.gson.JsonSyntaxException ex) {
            throw new IOException("Formato JSON inválido: " + ex.getMessage(), ex);
        }
    }
}

