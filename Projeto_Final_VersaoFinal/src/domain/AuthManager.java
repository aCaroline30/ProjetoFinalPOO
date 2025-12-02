package domain;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Usuario usuarioLogado = null;

    public AuthManager() {}

    // adicionar usuario (usado no seed)
    public void adicionarUsuario(Usuario u) {
        usuarios.put(u.getUsername(), u);
    }

    // remover
    public void removerUsuario(String username) {
        usuarios.remove(username);
    }

    // login retorna true se ok
    public boolean login(String username, String senha) {
        Usuario u = usuarios.get(username);
        if (u != null && u.getSenha().equals(senha)) {
            usuarioLogado = u;
            return true;
        }
        return false;
    }

    public void logout() {
        usuarioLogado = null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean isLogado() { return usuarioLogado != null; }

    public boolean possuiRole(Usuario.Role role) {
        return usuarioLogado != null && usuarioLogado.getRole() == role;
    }
}
