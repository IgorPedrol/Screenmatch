package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodios")
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private Integer temporada;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer numeroEpisodio;

    private Double avaliacao;

    private LocalDate dataLancamento;

    @ManyToOne
    private Serie serie;

    public Episodio() {
    }

    public Episodio(Integer numeroTemporada, EpisodioDTO episodioDTO) {
        this.temporada = numeroTemporada;
        this.titulo = episodioDTO.titulo();
        this.numeroEpisodio = episodioDTO.numero();

        try {
            this.avaliacao = Double.valueOf(episodioDTO.avaliacao());
        } catch (NumberFormatException ex) {
            this.avaliacao = 0.0;
        }

        try {
            this.dataLancamento = LocalDate.parse(episodioDTO.dataLancamento());
        } catch (DateTimeParseException ex) {
            this.dataLancamento = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento ;
    }
}
