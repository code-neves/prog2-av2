/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

/**
 *
 * @author Augusto
 */

import java.io.Serializable;

public class Disciplina implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String areaConhecimento; // Componentes da BNCC 
    public Disciplina(String nome, String areaConhecimento) {
        this.nome = nome;
        this.areaConhecimento = areaConhecimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAreaConhecimento() {
        return areaConhecimento;
    }

    public void setAreaConhecimento(String areaConhecimento) {
        this.areaConhecimento = areaConhecimento;
    }
}