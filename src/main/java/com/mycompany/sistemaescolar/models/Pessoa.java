/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

/**
 *
 * @author rafae
 */
public abstract class Pessoa implements Desempenho {
    protected String nome;
    protected String matricula;
    
    public Pessoa(String nome, String matricula) {
        this.nome = nome;
        this.matricula = matricula;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getMatricula() {
        return matricula;
    };
    
    public abstract String obterDetalhes();
}
