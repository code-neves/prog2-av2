/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

import java.util.List;


public class Turma {
    protected Professor professor;
    protected String codigoTurma;
    protected String disciplina;
    protected String horario;
    private List<Aluno> alunos;
    
    public Turma(
            Professor professor, String codigoTurma, 
            String disciplina, String horario 
    ) {
        this.professor = professor;
        this.codigoTurma = codigoTurma;
        this.disciplina = disciplina;
        this.horario = horario;
    }
    
    public boolean adicionarAluno(Aluno novoAluno) {
        for (Aluno aluno : this.alunos){
            if (aluno.getMatricula().equals(novoAluno.getMatricula())) {
                return false;
            }
            this.alunos.add(novoAluno);
            return true;
        }
        return false;
    }
    
    public boolean removerAluno(Aluno aluno) {
        for (Aluno a : this.alunos){
            if (a.getMatricula().equals(aluno.getMatricula())) {
                return false;
            }
            this.alunos.remove(aluno);
            return true;
        }
        return false;
    }
    

    
}
