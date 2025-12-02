package domain;

public class Assento {
    private String codigo; // ex: A1, B10 or S1...
    private boolean ocupado;

    public Assento() {}

    public Assento(String codigo) {
        this.codigo = codigo;
        this.ocupado = false;
    }

    public String getCodigo() { return codigo; }
    public boolean isOcupado() { return ocupado; }
    public void ocupar() { this.ocupado = true; }
    public void liberar() { this.ocupado = false; }

    @Override
    public String toString() {
        return codigo + (ocupado ? " (X)" : " ( )");
    }
}

