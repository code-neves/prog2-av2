package com.mycompany.sistemaescolar.Screens.professor;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class DashboardProfessor extends JPanel {

    private final FachadaEscolar   fachada;
    private final Professor        professor;
    private final Consumer<String> navegarPara;

    public DashboardProfessor(FachadaEscolar fachada, Pessoa pessoa, Consumer<String> navegarPara) {
        this.fachada     = fachada;
        this.professor   = (Professor) pessoa;
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

        content.add(cabecalho("Dashboard do Professor", "Suas turmas alocadas"));
        content.add(Box.createVerticalStrut(16));

        int totalAlunos = professor.getTurmas().stream().mapToInt(t -> t.getAlunos().size()).sum();
        JPanel metricas = new JPanel(new GridLayout(1, 2, 10, 0));
        metricas.setOpaque(false);
        metricas.add(metricCard("Turmas", professor.getQtdTurmasAlocadas() + " / " + professor.getMaxTurmas(), null));
        metricas.add(metricCard("Alunos", String.valueOf(totalAlunos), "no total"));
        metricas.setMaximumSize(new Dimension(9999, 90));
        content.add(metricas);
        content.add(Box.createVerticalStrut(20));

        JPanel atalhos = new JPanel(new GridLayout(1, 2, 10, 0));
        atalhos.setOpaque(false);
        atalhos.add(cartaoAtalho("Lançar nota / falta", "Registre desempenho dos alunos", () -> navegarPara.accept("desempenho")));
        atalhos.add(cartaoAtalho("Calendário de faltas", "Marque faltas por data", () -> navegarPara.accept("calendario")));
        atalhos.setMaximumSize(new Dimension(9999, 110));
        content.add(atalhos);
        content.add(Box.createVerticalStrut(20));

        content.add(secao("Minhas turmas"));
        content.add(Box.createVerticalStrut(6));

        List<Turma> turmas = professor.getTurmas();
        if (turmas.isEmpty()) {
            JLabel vz = new JLabel("Nenhuma turma alocada ainda.");
            vz.setFont(F_BODY); vz.setForeground(C_MUTED);
            content.add(vz);
        } else {
            String[] cols = {"Código", "Disciplina", "Horário", "Alunos"};
            String[][] data = new String[turmas.size()][4];
            for (int i = 0; i < turmas.size(); i++) {
                Turma t = turmas.get(i);
                data[i] = new String[]{t.getCodigoTurma(), t.getDisciplina().getNome(),
                    t.getHorario(), String.valueOf(t.getAlunos().size())};
            }
            JTable tabela = criarTabela(cols, data);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 200));
            content.add(sp);
        }

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}