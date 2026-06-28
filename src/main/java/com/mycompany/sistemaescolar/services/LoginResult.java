package com.mycompany.sistemaescolar.services;

import com.mycompany.sistemaescolar.models.Pessoa;

/**
 * Resultado de uma tentativa de login: quem é a pessoa (se houver)
 * e qual o papel dela no sistema (ALUNO, PROFESSOR ou COORDENACAO).
 * A Coordenação não tem um objeto Pessoa associado — é um papel fixo
 * de administração (login root/root).
 */
public class LoginResult {

    public enum Papel { ALUNO, PROFESSOR, COORDENACAO }

    private final Papel papel;
    private final Pessoa pessoa; // null quando papel == COORDENACAO

    public LoginResult(Papel papel, Pessoa pessoa) {
        this.papel = papel;
        this.pessoa = pessoa;
    }

    public Papel getPapel()   { return papel; }
    public Pessoa getPessoa() { return pessoa; }
}
