package com.mycompany.sistemaescolar.Screens.aluno;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.Screens.components.Components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class DashboardAluno extends JPanel {

    private final FachadaEscolar  fachada;
    private final Aluno           aluno;
    private final Consumer<String> navegarPara;

    public DashboardAluno(FachadaEscolar fachada, Pessoa pessoa, Consumer<String> navegarPara) {
        this.fachada     = fachada;
        this.aluno       = (Aluno) pessoa;
        this.navegarPara = navegarPara;
        setLayout(new BorderLayout());
        setBackground(C_BG);
    }

    public void refresh() {
        removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Dashboard do Aluno", "Seu resumo acadêmico"));
        content.add(Box.createVerticalStrut(16));

        // Métricas
        JPanel metricas = new JPanel(new GridLayout(1, 4, 10, 0));
        metricas.setOpaque(false);
        metricas.add(metricCard("Média",  String.format("%.1f", aluno.getNotaMedia()), null));
        metricas.add(metricCard("Faltas", String.valueOf(aluno.getFaltas()), null));
        metricas.add(metricCard("Meta",   String.format("%.0f%%", aluno.calcularMeta()), null));
        metricas.add(metricCard("Turmas", String.valueOf(aluno.getTurmas().size()), null));
        metricas.setMaximumSize(new Dimension(9999, 90));
        content.add(metricas);
        content.add(Box.createVerticalStrut(20));

        // Atalhos
        JPanel atalhos = new JPanel(new GridLayout(1, 2, 10, 0));
        atalhos.setOpaque(false);
        atalhos.add(cartaoAtalho("Meu desempenho", "Veja suas notas e metas", () -> navegarPara.accept("desempenho")));
        atalhos.add(cartaoAtalho("Minhas faltas",  "Consulte e justifique suas faltas", () -> navegarPara.accept("faltas")));
        atalhos.setMaximumSize(new Dimension(9999, 110));
        content.add(atalhos);
        content.add(Box.createVerticalStrut(20));

        // Tabela de turmas
        content.add(secao("Minhas turmas"));
        content.add(Box.createVerticalStrut(6));
        List<Turma> turmas = aluno.getTurmas();
        if (turmas.isEmpty()) {
            JLabel vz = new JLabel("Você ainda não está matriculado em nenhuma turma.");
            vz.setFont(F_BODY); vz.setForeground(C_MUTED);
            content.add(vz);
        } else {
            String[] cols = {"Código", "Disciplina", "Horário", "Professor"};
            String[][] data = new String[turmas.size()][4];
            for (int i = 0; i < turmas.size(); i++) {
                Turma t = turmas.get(i);
                String prof = t.getProfessor() != null ? t.getProfessor().getNome() : "— sem professor —";
                data[i] = new String[]{t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(), prof};
            }
            JTable tabela = criarTabela(cols, data);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 180));
            content.add(sp);
        }

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}