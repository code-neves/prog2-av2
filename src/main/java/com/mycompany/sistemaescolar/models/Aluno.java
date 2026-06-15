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
    private String matricula;
    private int faltas;
    
    public Aluno(
            String nome, String cpf, double notaMedia, 
            String matricula, String email, String telefone
    ) {
        super(nome, cpf, email ,telefone);
        this.matricula = matricula;
        this.notaMedia = notaMedia;
        this.faltas = 0;        
    }
    
    public double calcularMedia() {
        return notaMedia;
    }
    
    public boolean registrarFalta(int faltas) {
        if (this.faltas < faltas) {
            return false;
        } else {
            this.faltas += faltas;
            return true;
        }
    }
    
    public void consultarDesempenho () {
    
    }  

    public double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }
}
