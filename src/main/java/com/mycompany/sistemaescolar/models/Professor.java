/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

public class Professor extends Pessoa {
    private static final long serialVersionUID = 1L;
    private int qtdTurmas;
    protected String especialidade;
    
    
    public Professor(
            String nome, String cpf, String email, 
            String telefone, String especialidade, int qtdTurmas
    ) {
        super(nome, cpf, email, telefone);
        this.qtdTurmas = qtdTurmas;
        this.especialidade = especialidade;
    }
    
    public boolean atribuirNota(Aluno aluno, double nota) {
        if (aluno != null && nota >= 0.0 && nota <= 10.0) {
            aluno.setNotaMedia(nota);
            return true;
        }
        return false;
    }
   public void registrarFrequencia(Aluno aluno, int quantidadeFaltas) {
        if (aluno != null) {
            aluno.registrarFalta(quantidadeFaltas);
        }
    }

    public int getQtdTurmas() {
        return qtdTurmas;
    }

    public void setQtdTurmas(int qtdTurmas) {
        this.qtdTurmas = qtdTurmas;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
    
