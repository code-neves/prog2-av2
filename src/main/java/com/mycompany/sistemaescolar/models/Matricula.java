package com.mycompany.sistemaescolar.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { ATIVA, TRANCADA, CONCLUIDA }

    private String codigo;
    private Aluno aluno;
    private Turma turma;
    private String dataMatricula;
    private Status status;

    public Matricula(Aluno aluno, Turma turma) {
        this.aluno = aluno;
        this.turma = turma;
        this.dataMatricula = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.status = Status.ATIVA;
        // Código gerado a partir da matrícula do aluno + código da turma
        this.codigo = aluno.getMatricula() + "-" + turma.getCodigoTurma();
    }

    public String getCodigo()            { return codigo; }
    public Aluno getAluno()              { return aluno; }
    public Turma getTurma()              { return turma; }
    public String getDataMatricula()     { return dataMatricula; }
    public Status getStatus()            { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return codigo + " | " + aluno.getNome() +
               " | Turma: " + turma.getCodigoTurma() +
               " | " + turma.getDisciplina().getNome() +
               " | Data: " + dataMatricula +
               " | Status: " + status;
    }
}
