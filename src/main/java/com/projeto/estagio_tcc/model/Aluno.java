package com.projeto.estagio_tcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Aluno {
    @Id
    private String ra;

    private int ano;
    private String nome;
    private String equipe;
    private String orientador;
    private String tema;

    @Column(length = 500)
    private String observacoes;

    @Column
    private String disciplina;

    public Aluno(String ra, int ano, String nome, String equipe, String orientador, String tema, String observacoes, String disciplina) {
        this.ra = ra;
        this.ano = ano;
        this.nome = nome;
        this.equipe = equipe;
        this.orientador = orientador;
        this.tema = tema;
        this.observacoes = observacoes;
    }

    public Aluno() {}

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public String getOrientador() {
        return orientador;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return getAno() == aluno.getAno() && Objects.equals(getRa(), aluno.getRa()) && Objects.equals(getNome(), aluno.getNome()) && Objects.equals(getEquipe(), aluno.getEquipe()) && Objects.equals(getOrientador(), aluno.getOrientador()) && Objects.equals(getTema(), aluno.getTema()) && Objects.equals(getObservacoes(), aluno.getObservacoes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRa(), getAno(), getNome(), getEquipe(), getOrientador(), getTema(), getObservacoes());
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "ra='" + ra + '\'' +
                ", ano=" + ano +
                ", nome='" + nome + '\'' +
                ", equipe='" + equipe + '\'' +
                ", orientador='" + orientador + '\'' +
                ", tema='" + tema + '\'' +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
