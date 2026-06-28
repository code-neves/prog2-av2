package com.mycompany.sistemaescolar.models;

import java.io.Serializable;

/**
 * Classe base abstrata para todas as pessoas do sistema.
 * O método abstrato exibirResumo() garante que TODA subclasse
 * implemente sua própria apresentação — isso é polimorfismo real.
 */
public abstract class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String cpf;
    protected String email;
    protected String telefone;

    public Pessoa(String nome, String cpf, String email, String telefone) {
        this.nome     = nome;
        this.cpf      = cpf;
        this.email    = email;
        this.telefone = telefone;
    }

    /**
     * Método abstrato: cada subclasse OBRIGATORIAMENTE define
     * como exibe seu próprio resumo. É por aqui que o polimorfismo
     * acontece quando iteramos List<Pessoa>.
     */
    public abstract String exibirResumo();

    /**
     * Método abstrato: cada subclasse define seu próprio "tipo" textual
     * (ex: "Aluno", "Professor"). Junto com exibirResumo(), reforça o
     * contrato abstrato de Pessoa — toda subclasse é OBRIGADA a
     * implementar os dois métodos, e cada uma o faz de forma diferente.
     */
    public abstract String getTipo();

    /**
     * Método CONCRETO (não abstrato) definido na própria classe abstrata.
     * Mostra que uma classe abstrata também pode oferecer comportamento
     * pronto e compartilhado entre todas as subclasses, sem precisar
     * ser reescrito em Aluno ou Professor.
     */
    public String exibirContato() {
        return String.format("%s (%s) | E-mail: %s | Telefone: %s",
                nome, getTipo(), email, telefone);
    }

    /**
     * Método abstrato: cada subclasse define sua própria regra de senha
     * (Aluno usa a matrícula, Professor usa o CPF). O login do sistema
     * chama pessoa.getSenha() de forma polimórfica, sem precisar saber
     * qual subclasse está por trás da referência Pessoa.
     */
    public abstract String getSenha();

    // ----------------------------------------------------------------
    // Getters / Setters comuns
    // ----------------------------------------------------------------
    public String getNome()              { return nome; }
    public void   setNome(String nome)   { this.nome = nome; }
    public String getCpf()               { return cpf; }
    public void   setCpf(String cpf)     { this.cpf = cpf; }
    public String getEmail()             { return email; }
    public void   setEmail(String email) { this.email = email; }
    public String getTelefone()          { return telefone; }
    public void   setTelefone(String t)  { this.telefone = t; }
}
