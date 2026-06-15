/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.services;

import com.mycompany.sistemaescolar.models.Aluno;
import com.mycompany.sistemaescolar.models.Professor;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;

import java.util.ArrayList;

public class FachadaEscolar {
    private ArrayList<Professor> listProfessores;
    private ArrayList<Aluno> listAlunos;
    
    public FachadaEscolar() {
        this.listProfessores = new ArrayList<>();
        this.listAlunos = new ArrayList<>();
    }

    public void cadastrarAluno(String nome, String cpf, String email, 
            String telefone,String matricula, double notaMedia) {
        Aluno aluno = new Aluno(nome,cpf, notaMedia, email, telefone, matricula);
        listAlunos.add(aluno);
    }

    public void cadastrarProfessor(String nome, String cpf, String email, 
            String telefone, String especialidade , int qtdTurmas
    ) {
        Professor professor = new Professor(nome, cpf, email,telefone,
                especialidade, qtdTurmas);
        listProfessores.add(professor);
    }

    public String listarAlunos() throws UserNotFoundException {
        String tabelaDados = "Matricula\tNome\t\tNota Media\n";
        tabelaDados += "--------------------------------------------------\n";
        boolean encontrouRegistros = false;

        for (Aluno pessoa : listAlunos) {
            if (pessoa instanceof Aluno) {
                encontrouRegistros = true;
            }
        }

        if (!encontrouRegistros) {
            throw new UserNotFoundException("Nenhum aluno cadastrado no sistema.");
        }

        return tabelaDados;
    }

    public String listarProfessores() throws UserNotFoundException {
        String tabelaDados = "Matricula\tNome\t\tTurmas\n";
        tabelaDados += "--------------------------------------------------\n";
        boolean encontrouRegistros = false;

        for (Professor pessoa : listProfessores) {
            if (pessoa instanceof Professor) {
                encontrouRegistros = true;
            }
        }

        if (!encontrouRegistros) {
            throw new UserNotFoundException("Nenhum professor cadastrado no sistema.");
        }

        return tabelaDados;
    }
}

