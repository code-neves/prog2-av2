package com.mycompany.sistemaescolar.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Professor herda de Pessoa (herança).
 * Obrigado a implementar exibirResumo() definido como abstrato em Pessoa.
 */
public class Professor extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String especialidade;
    private int    maxTurmas;
    private List<Turma> turmas;

    public Professor(String nome, String cpf, String email,
                     String telefone, String especialidade, int maxTurmas) {
        super(nome, cpf, email, telefone);
        this.especialidade = especialidade;
        this.maxTurmas     = maxTurmas;
        this.turmas        = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Implementação do método abstrato de Pessoa — POLIMORFISMO
    // Quando iteramos List<Pessoa> e chamamos exibirResumo(),
    // esta versão é executada para objetos Professor.
    // ----------------------------------------------------------------
    @Override
    public String exibirResumo() {
        return String.format("[PROF.]  %-20s | CPF: %-14s | Especialidade: %-18s | Turmas: %d/%d",
                nome, cpf, especialidade, turmas.size(), maxTurmas);
    }

    // ----------------------------------------------------------------
    // Implementação do segundo método abstrato de Pessoa
    // ----------------------------------------------------------------
    @Override
    public String getTipo() {
        return "Professor";
    }

    // ----------------------------------------------------------------
    // Implementação do método abstrato de senha — Professor usa o CPF
    // ----------------------------------------------------------------
    @Override
    public String getSenha() {
        return cpf;
    }

    // ----------------------------------------------------------------
    // Gerenciamento de turmas
    // ----------------------------------------------------------------
    public boolean adicionarTurma(Turma turma) {
        if (turma == null) return false;
        if (turmas.size() >= maxTurmas) return false;
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
    // Ações pedagógicas (usadas pela fachada)
    // ----------------------------------------------------------------
    public boolean atribuirNota(Aluno aluno, double nota) {
        if (aluno == null || nota < 0.0 || nota > 10.0) return false;
        aluno.setNotaMedia(nota);
        return true;
    }

    public boolean registrarFrequencia(Aluno aluno, int qtdFaltas) {
        if (aluno == null) return false;
        return aluno.registrarFalta(qtdFaltas);
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------
    public String getEspecialidade()         { return especialidade; }
    public void   setEspecialidade(String e) { this.especialidade = e; }
    public int    getMaxTurmas()             { return maxTurmas; }
    public void   setMaxTurmas(int max)      { this.maxTurmas = max; }
    public int    getQtdTurmasAlocadas()     { return turmas.size(); }
}
