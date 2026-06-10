/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.services;

import com.mycompany.sistemaescolar.models.Aluno;
import com.mycompany.sistemaescolar.models.Pessoa;
import com.mycompany.sistemaescolar.models.Professor;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;

import java.util.ArrayList;

public class FachadaEscolar {
    private ArrayList<Pessoa> listaPessoas;

    public FachadaEscolar() {
        this.listaPessoas = new ArrayList<>();
    }

    public void cadastrarAluno(String nome, String matricula, double notaMedia) {
        Pessoa aluno = new Aluno(nome, matricula, notaMedia);
        listaPessoas.add(aluno);
    }

    public void cadastrarProfessor(String nome, String matricula, int quantidadeTurmas) {
        Pessoa professor = new Professor(nome, matricula, quantidadeTurmas);
        listaPessoas.add(professor);
    }

    public String listarAlunos() throws UserNotFoundException {
        String tabelaDados = "Matricula\tNome\t\tNota Media\n";
        tabelaDados += "--------------------------------------------------\n";
        boolean encontrouRegistros = false;

        for (Pessoa pessoa : listaPessoas) {
            if (pessoa instanceof Aluno) {
                tabelaDados += pessoa.obterDetalhes() + "\n";
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

        for (Pessoa pessoa : listaPessoas) {
            if (pessoa instanceof Professor) {
                tabelaDados += pessoa.obterDetalhes() + "\n";
                encontrouRegistros = true;
            }
        }

        if (!encontrouRegistros) {
            throw new UserNotFoundException("Nenhum professor cadastrado no sistema.");
        }

        return tabelaDados;
    }
}

