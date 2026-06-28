package com.mycompany.sistemaescolar.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aluno herda de Pessoa (herança) e implementa Desempenho (interface).
 * Obrigado a implementar exibirResumo() de Pessoa e calcularMeta() de Desempenho.
 */
public class Aluno extends Pessoa implements Desempenho {
    private static final long serialVersionUID = 1L;

    private double notaMedia;
    private String matricula;
    private int    faltas;
    private List<Turma> turmas;
    private List<RegistroFalta> listaFaltas; // faltas individuais, com data e justificativa

    public Aluno(String nome, String cpf, String email,
                 String telefone, String matricula, double notaMedia) {
        super(nome, cpf, email, telefone);
        this.matricula   = matricula;
        this.notaMedia   = notaMedia;
        this.faltas      = 0;
        this.turmas      = new ArrayList<>();
        this.listaFaltas = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Implementação do método abstrato de Pessoa — POLIMORFISMO
    // Quando iteramos List<Pessoa> e chamamos exibirResumo(),
    // esta versão é executada para objetos Aluno.
    // ----------------------------------------------------------------
    @Override
    public String exibirResumo() {
        return String.format("[ALUNO]  %-20s | Matrícula: %-12s | Média: %4.1f | Faltas: %d | Meta: %.1f%%",
                nome, matricula, notaMedia, faltas, calcularMeta());
    }

    // ----------------------------------------------------------------
    // Implementação do segundo método abstrato de Pessoa
    // ----------------------------------------------------------------
    @Override
    public String getTipo() {
        return "Aluno";
    }

    // ----------------------------------------------------------------
    // Implementação do método abstrato de senha — Aluno usa a matrícula
    // ----------------------------------------------------------------
    @Override
    public String getSenha() {
        return matricula;
    }

    // ----------------------------------------------------------------
    // Implementação da interface Desempenho — polimorfismo de interface
    // ----------------------------------------------------------------
    @Override
    public double calcularMeta() {
        if (this.notaMedia >= 7.0) return 100.0;
        return (this.notaMedia / 7.0) * 100.0;
    }

    // ----------------------------------------------------------------
    // Gerenciamento de turmas
    // ----------------------------------------------------------------
    public boolean adicionarTurma(Turma turma) {
        if (turma == null) return false;
        for (Turma t : turmas)
            if (t.getCodigoTurma().equals(turma.getCodigoTurma())) return false;
        turmas.add(turma);
        return true;
    }

    public boolean removerTurma(String codigoTurma) {
        return turmas.removeIf(t -> t.getCodigoTurma().equals(codigoTurma));
    }

    public List<Turma> getTurmas() { return turmas; }

    // ----------------------------------------------------------------
    // Faltas
    // ----------------------------------------------------------------

    /** Forma simples antiga: soma uma quantidade de faltas sem data específica. */
    public boolean registrarFalta(int quantidade) {
        if (quantidade <= 0) return false;
        this.faltas += quantidade;
        return true;
    }

    /**
     * Registra UMA falta em uma data específica (usada pelo calendário do professor).
     * Evita duplicar a mesma falta na mesma data e turma.
     */
    public boolean registrarFalta(LocalDate data, String codigoTurma) {
        if (data == null) return false;
        for (RegistroFalta r : listaFaltas) {
            if (r.getData().equals(data) &&
                ((codigoTurma == null && r.getCodigoTurma() == null) ||
                 (codigoTurma != null && codigoTurma.equals(r.getCodigoTurma())))) {
                return false; // já existe falta nessa data/turma
            }
        }
        listaFaltas.add(new RegistroFalta(data, codigoTurma));
        this.faltas++;
        return true;
    }

    public List<RegistroFalta> getListaFaltas() { return listaFaltas; }

    /** Aluno envia justificativa (texto) para uma falta específica, pela data. */
    public boolean justificarFalta(LocalDate data, String textoJustificativa) {
        if (data == null || textoJustificativa == null || textoJustificativa.isBlank()) return false;
        for (RegistroFalta r : listaFaltas) {
            if (r.getData().equals(data)) {
                r.enviarJustificativa(textoJustificativa);
                return true;
            }
        }
        return false;
    }

    public String consultarDesempenho() {
        return "Matrícula: " + matricula +
               " | Nome: "   + nome +
               " | Média: "  + notaMedia +
               " | Faltas: " + faltas +
               " | Meta: "   + String.format("%.1f", calcularMeta()) + "%";
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------
    public double getNotaMedia()             { return notaMedia; }
    public void   setNotaMedia(double nota)  { this.notaMedia = nota; }
    public String getMatricula()             { return matricula; }
    public void   setMatricula(String m)     { this.matricula = m; }
    public int    getFaltas()                { return faltas; }
    public void   setFaltas(int f)           { this.faltas = f; }

    /**
     * Compatibilidade: bancos de dados salvos antes de existir listaFaltas
     * trazem esse campo como null na deserialização.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (listaFaltas == null) {
            listaFaltas = new ArrayList<>();
        }
    }
}
