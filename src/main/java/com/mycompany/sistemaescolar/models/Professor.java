/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

public class Professor extends Pessoa {
    private int quantidadeDeTurmas;
    
    public Professor(String nome, String matricula, int quantidadeTurmas) {
        super(nome, matricula);
        this.quantidadeDeTurmas = quantidadeTurmas;
    }

    @Override
    public double calcularMeta() {
        return quantidadeDeTurmas * 100.0;
    }

    @Override
    public String obterDetalhes() {
        return matricula + "\t" + nome + "\t\t" + quantidadeDeTurmas;
    }
    
}
