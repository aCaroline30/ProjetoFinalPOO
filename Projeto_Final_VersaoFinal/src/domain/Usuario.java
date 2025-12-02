package domain;

public class Usuario {
    public enum Role { ADMIN, OPERADOR }
    private String id;
    private String username;
    private String senha;
    private Role role;

    public Usuario() {}
    public Usuario(String id, String username, String senha, Role role) { this.id = id; this.username = username; this.senha = senha; this.role = role; }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getSenha() { return senha; }
    public Role getRole() { return role; }
}

