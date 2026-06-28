package com.mycompany.sistemaescolar.Screens.aluno;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaDesempenhoAluno extends JPanel {

    private final FachadaEscolar fachada;
    private final Aluno          aluno;

    public TelaDesempenhoAluno(FachadaEscolar fachada, Pessoa pessoa) {
        this.fachada = fachada;
        this.aluno   = (Aluno) pessoa;
        setLayout(new BorderLayout());
        setBackground(C_BG);
    }

    public void refresh() {
        removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Meu Desempenho", "Acompanhe sua média, faltas e meta"));
        content.add(Box.createVerticalStrut(20));

        JPanel metricas = new JPanel(new GridLayout(1, 3, 10, 0));
        metricas.setOpaque(false);
        metricas.add(metricCard("Nota Média", String.format("%.1f", aluno.getNotaMedia()), "de 10,0"));
        metricas.add(metricCard("Faltas",     String.valueOf(aluno.getFaltas()), "aulas"));
        metricas.add(metricCard("Meta",       String.format("%.0f%%", aluno.calcularMeta()), "de aprovação"));
        metricas.setMaximumSize(new Dimension(9999, 90));
        content.add(metricas);
        content.add(Box.createVerticalStrut(20));

        // Situação
        content.add(secao("Situação atual"));
        content.add(Box.createVerticalStrut(8));
        String situacao = aluno.getNotaMedia() >= 5.0 ? "✓ Aprovado na média" : "⚠ Abaixo da média mínima (5,0)";
        Color  corSit   = aluno.getNotaMedia() >= 5.0 ? C_SUCCESS : C_DANGER;
        JLabel lblSit = new JLabel(situacao);
        lblSit.setFont(F_BOLD); lblSit.setForeground(corSit);
        lblSit.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblSit);

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}