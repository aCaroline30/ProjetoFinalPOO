package domain;

import java.time.Duration;
import java.util.Objects;

public class Filme {
    private String id;
    private String titulo;
    private String genero;
    private Duration duracao;
    private int classificacaoEtaria;
    private String sinopse;
    private String urlTrailer;

    public Filme() {}

    public Filme(String id, String titulo, String genero, Duration duracao, int classificacaoEtaria, String sinopse, String urlTrailer) {
        this.id = id; this.titulo = titulo; this.genero = genero; this.duracao = duracao;
        this.classificacaoEtaria = classificacaoEtaria; this.sinopse = sinopse; this.urlTrailer = urlTrailer;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Duration getDuracao() { return duracao; }
    public void setDuracao(Duration duracao) { this.duracao = duracao; }
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
    public void setClassificacaoEtaria(int c) { this.classificacaoEtaria = c; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String s) { this.sinopse = s; }
    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String u) { this.urlTrailer = u; }

    @Override
    public String toString() {
        long min = duracao == null ? 0 : duracao.toMinutes();
        return id + " - " + titulo + " (" + genero + ") " + min + "min - Class: " + classificacaoEtaria;
    }

    @Override
    public boolean equals(Object o) { return (o instanceof Filme) && Objects.equals(((Filme)o).id, id); }
    @Override
    public int hashCode() { return Objects.hash(id); }
}

