/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

/**
 *
 * @author rafae
 */
public class Aluno extends Pessoa{
    private double notaMedia;
    
    public Aluno(String nome, String matricula, double notaMedia) {
        super(nome, matricula);
        this.notaMedia = notaMedia;
    }
    
    @Override
    public double calcularMeta() {
        return notaMedia;
    }
    
    @Override
    public String obterDetalhes() {
        return matricula + "\t" + nome + "\t\t" + notaMedia;
    }
}
