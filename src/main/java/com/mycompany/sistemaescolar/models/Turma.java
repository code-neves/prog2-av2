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
    
    // CORRIGIDO: Construtor com apenas 3 parâmetros (sem exigir Professor no momento da criação)
    public Turma(String codigoTurma, Disciplina disciplina, String horario) {
        this.codigoTurma = codigoTurma;
        this.disciplina = disciplina;
        this.horario = horario;
        this.alunos = new ArrayList<>(); // Inicialização para evitar NullPointerException
        this.professor = null;           // Começa sem professor alocado
    }
    
    public boolean adicionarAluno(Aluno novoAluno) {
        if (novoAluno == null) return false;
        
        for (Aluno aluno : this.alunos){
            if (aluno.getMatricula().equals(novoAluno.getMatricula())) {
                return false; 
            }
        }
        this.alunos.add(novoAluno);
        return true;
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