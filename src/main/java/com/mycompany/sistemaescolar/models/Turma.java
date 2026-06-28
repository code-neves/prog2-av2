/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Turma implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Professor professor;
    protected String codigoTurma;
    protected Disciplina disciplina; // Associado ao objeto Disciplina estruturado
    protected String horario;
    private List<Aluno> alunos;
    private int capacidadeMaxima; // Limite de alunos permitidos na turma

    // Capacidade padrão usada quando nenhum valor é informado no construtor antigo
    public static final int CAPACIDADE_PADRAO = 30;

    // CORRIGIDO: Construtor com apenas 3 parâmetros (sem exigir Professor no momento da criação)
    // Mantido por compatibilidade: usa a capacidade padrão da turma.
    public Turma(String codigoTurma, Disciplina disciplina, String horario) {
        this(codigoTurma, disciplina, horario, CAPACIDADE_PADRAO);
    }

    // Novo construtor: permite definir o limite de alunos por turma
    public Turma(String codigoTurma, Disciplina disciplina, String horario, int capacidadeMaxima) {
        this.codigoTurma = codigoTurma;
        this.disciplina = disciplina;
        this.horario = horario;
        this.alunos = new ArrayList<>(); // Inicialização para evitar NullPointerException
        this.professor = null;           // Começa sem professor alocado
        this.capacidadeMaxima = (capacidadeMaxima > 0) ? capacidadeMaxima : CAPACIDADE_PADRAO;
    }

    public boolean adicionarAluno(Aluno novoAluno) {
        if (novoAluno == null) return false;

        if (estaCheia()) return false; // Respeita o limite de alunos por turma

        for (Aluno aluno : this.alunos){
            if (aluno.getMatricula().equals(novoAluno.getMatricula())) {
                return false; 
            }
        }
        this.alunos.add(novoAluno);
        return true;
    }

    // ----------------------------------------------------------------
    // Capacidade / limite de alunos por turma
    // ----------------------------------------------------------------
    public boolean estaCheia() {
        return this.alunos.size() >= this.capacidadeMaxima;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        if (capacidadeMaxima > 0) {
            this.capacidadeMaxima = capacidadeMaxima;
        }
    }

    public int getVagasDisponiveis() {
        return Math.max(0, capacidadeMaxima - alunos.size());
    }

    /**
     * Tratamento de compatibilidade: se o arquivo .dat foi salvo ANTES
     * de existir o campo capacidadeMaxima, ele chega aqui como 0 na
     * deserialização (Java não chama o construtor nesse processo).
     * Sem isso, turmas antigas ficariam "cheias" para sempre.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (capacidadeMaxima <= 0) {
            capacidadeMaxima = CAPACIDADE_PADRAO;
        }
    }
    
    public boolean removerAluno(Aluno aluno) {
        if (aluno == null) return false;
        
        for (Aluno a : this.alunos){
            if (a.getMatricula().equals(aluno.getMatricula())) {
                this.alunos.remove(a);
                return true;
            }
        }
        return false;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public void setCodigoTurma(String codigoTurma) {
        this.codigoTurma = codigoTurma;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }
}