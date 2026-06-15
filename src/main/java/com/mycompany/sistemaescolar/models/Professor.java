/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

public class Professor extends Pessoa {
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
    
    public boolean atribuirNota(String matricula, double nota ) {
        if ("1".equals(matricula)) {
            //atribuir a notra
            return true;
        } else {
            return false;
        }
    }
    
    public void registrarFrequencia() {
        
    }
    
}
