package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sala {
    private String id;
    private String nome;
    private int capacidade;
    private List<Assento> assentos;

    public Sala() {}

    public Sala(String id, String nome, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.assentos = new ArrayList<>();
        gerarAssentosPadrao();
    }

    private void gerarAssentosPadrao() {
        for (int i = 1; i <= capacidade; i++) {
            assentos.add(new Assento("S" + i));
        }
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public List<Assento> getAssentos() {
        if (assentos == null) assentos = new ArrayList<>();
        return assentos;
    }

    public Assento buscarAssentoPorCodigo(String codigo) {
        return getAssentos().stream().filter(a -> a.getCodigo().equalsIgnoreCase(codigo)).findFirst().orElse(null);
    }

 
    public String toString() {
        return id + " - " + nome + " (capacidade=" + capacidade + ")";
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sala)) return false;
        Sala sala = (Sala) o;
        return Objects.equals(id, sala.id);
    }


    public int hashCode() {
        return Objects.hash(id);
    }
}
