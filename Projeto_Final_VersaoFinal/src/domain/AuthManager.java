package domain;

import java.util.*;

public class AuthManager {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Usuario logado = null;

    public void adicionarUsuario(Usuario u) { usuarios.put(u.getUsername(), u); }

    public boolean login(String username, String senha) {
        Usuario u = usuarios.get(username);
        if (u != null && u.getSenha().equals(senha)) { logado = u; return true; }
        return false;
    }

    public void logout() { logado = null; }
    public boolean isLogado() { return logado != null; }
    public Usuario getUsuarioLogado() { return logado; }
    public boolean possuiRole(Usuario.Role role) { return logado != null && logado.getRole() == role; }
}

