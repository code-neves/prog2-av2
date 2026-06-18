/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.models;

/**
 *
 * @author rafae
 */
public class Aluno extends Pessoa implements Desempenho{
    private static final long serialVersionUID = 1L;
    private double notaMedia;
    private String matricula;
    private int faltas;
    
    public Aluno(
            String nome, String cpf, String email, 
            String telefone, String matricula, double notaMedia
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
    
    public String consultarDesempenho() {
        return "Matrícula: " + matricula + " | Nome: " + nome + 
               " | Média: " + notaMedia + " | Faltas: " + faltas + 
               " | Meta: " + String.format("%.1f", calcularMeta()) + "%";
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
    
  @Override
    public double calcularMeta() {
        // Se a nota for maior ou igual à média para aprovação (7.0), atingiu 100% da meta
        if (this.notaMedia >= 7.0) {
            return 100.0;
        } else {
            // Calcula a porcentagem de aproximação do objetivo
            return (this.notaMedia / 7.0) * 100.0;
        }
    }  

}
