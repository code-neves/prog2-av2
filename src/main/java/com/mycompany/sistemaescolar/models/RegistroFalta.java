package com.mycompany.sistemaescolar.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa UMA falta individual de um aluno em uma data específica.
 * Permite que o professor lance faltas via calendário (uma data por vez)
 * e que o aluno solicite justificativa (atestado em texto) para cada uma.
 */
public class RegistroFalta implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusJustificativa { SEM_JUSTIFICATIVA, PENDENTE, APROVADA, REJEITADA }

    private LocalDate data;
    private String codigoTurma;        // qual turma gerou a falta (pode ser null se geral)
    private String justificativaTexto; // texto enviado pelo aluno (sem upload de arquivo)
    private StatusJustificativa status;

    public RegistroFalta(LocalDate data, String codigoTurma) {
        this.data = data;
        this.codigoTurma = codigoTurma;
        this.justificativaTexto = null;
        this.status = StatusJustificativa.SEM_JUSTIFICATIVA;
    }

    public LocalDate getData()                 { return data; }
    public String getCodigoTurma()             { return codigoTurma; }
    public String getJustificativaTexto()       { return justificativaTexto; }
    public StatusJustificativa getStatus()      { return status; }

    /** Aluno envia a justificativa (texto). Fica PENDENTE até a coordenação/professor avaliar. */
    public void enviarJustificativa(String texto) {
        this.justificativaTexto = texto;
        this.status = StatusJustificativa.PENDENTE;
    }

    public void aprovarJustificativa() { this.status = StatusJustificativa.APROVADA; }
    public void rejeitarJustificativa() { this.status = StatusJustificativa.REJEITADA; }

    public boolean temJustificativaPendente() {
        return status == StatusJustificativa.PENDENTE;
    }

    @Override
    public String toString() {
        String turmaInfo = (codigoTurma != null) ? " | Turma: " + codigoTurma : "";
        return data + turmaInfo + " | Status: " + status +
               (justificativaTexto != null ? " | Justificativa: " + justificativaTexto : "");
    }
}
