package domain;

import java.util.*;

public class Sala {
    private String id;
    private String nome;
    private int capacidade;
    private List<Assento> assentos = new ArrayList<>();

    public Sala() {}

    public Sala(String id, String nome, int capacidade) {
        this.id = id;
        this.nome = nome;
        setCapacidade(capacidade);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCapacidade() { return capacidade; }

    public void setCapacidade(int novaCapacidade) {
        if (novaCapacidade < 0) throw new IllegalArgumentException("Capacidade negativa");
        if (novaCapacidade == this.capacidade) return;
        if (novaCapacidade > this.capacidade) {
            for (int i = this.capacidade + 1; i <= novaCapacidade; i++)
                assentos.add(new Assento("S" + i));
        } else {
           
            for (int i = novaCapacidade; i < this.capacidade; i++) {
                if (assentos.get(i).isOcupado())
                    throw new IllegalStateException("Não é possível reduzir capacidade: assento " + assentos.get(i).getCodigo() + " ocupado.");
            }
            assentos = new ArrayList<>(assentos.subList(0, novaCapacidade));
        }
        this.capacidade = novaCapacidade;
    }

    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); }

    public Assento buscarAssentoPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (Assento a : assentos) if (codigo.equalsIgnoreCase(a.getCodigo())) return a;
        return null;
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (capacidade: " + capacidade + ")";
    }
}

