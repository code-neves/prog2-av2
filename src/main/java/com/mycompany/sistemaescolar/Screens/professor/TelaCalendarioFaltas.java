package com.mycompany.sistemaescolar.Screens.professor;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaCalendarioFaltas extends JPanel {

    private final FachadaEscolar fachada;
    private final Professor      professor;

    public TelaCalendarioFaltas(FachadaEscolar fachada, Pessoa pessoa) {
        this.fachada   = fachada;
        this.professor = (Professor) pessoa;
        setLayout(new BorderLayout());
        setBackground(C_BG);
    }

    public void refresh() {
        removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Calendário de Faltas", "Selecione a turma, a data e marque os alunos ausentes"));
        content.add(Box.createVerticalStrut(20));

        List<Turma> turmas = professor.getTurmas();
        if (turmas.isEmpty()) {
            JLabel vz = new JLabel("Você ainda não tem turmas alocadas.");
            vz.setFont(F_BODY); vz.setForeground(C_MUTED);
            content.add(vz);
            content.add(Box.createVerticalGlue());
            add(content, BorderLayout.CENTER);
            revalidate(); repaint();
            return;
        }

        // Combo de turma
        JLabel lTurma = new JLabel("Turma"); lTurma.setFont(F_BOLD); lTurma.setForeground(C_MUTED);
        lTurma.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] codTurmas = turmas.stream()
            .map(t -> t.getCodigoTurma() + " — " + t.getDisciplina().getNome())
            .toArray(String[]::new);
        JComboBox<String> cbTurma = new JComboBox<>(codTurmas);
        cbTurma.setFont(F_BODY); cbTurma.setMaximumSize(new Dimension(9999, 36));
        cbTurma.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lTurma); content.add(Box.createVerticalStrut(3));
        content.add(cbTurma); content.add(Box.createVerticalStrut(14));

        // Spinner de data
        JLabel lData = new JLabel("Data da aula"); lData.setFont(F_BOLD); lData.setForeground(C_MUTED);
        lData.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSpinner spinnerData = new JSpinner(new SpinnerDateModel());
        spinnerData.setEditor(new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy"));
        spinnerData.setMaximumSize(new Dimension(180, 36));
        spinnerData.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lData); content.add(Box.createVerticalStrut(3));
        content.add(spinnerData); content.add(Box.createVerticalStrut(16));

        // Lista de alunos com checkbox
        content.add(secao("Alunos da turma")); content.add(Box.createVerticalStrut(8));
        JPanel listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);
        JScrollPane scrollAlunos = new JScrollPane(listaPanel);
        scrollAlunos.setBorder(new LineBorder(C_BORDER));
        scrollAlunos.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollAlunos.setMaximumSize(new Dimension(9999, 240));
        content.add(scrollAlunos); content.add(Box.createVerticalStrut(10));

        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(F_SMALL); lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatus); content.add(Box.createVerticalStrut(6));

        JButton btnLancar = botaoPrimario("Lançar faltas selecionadas");
        btnLancar.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(btnLancar);

        List<JCheckBox> checks = new ArrayList<>();
        List<Aluno> alunosDaTurma = new ArrayList<>();

        Runnable popularLista = () -> {
            listaPanel.removeAll(); checks.clear(); alunosDaTurma.clear();
            Turma t = turmas.get(cbTurma.getSelectedIndex());
            alunosDaTurma.addAll(t.getAlunos());
            if (alunosDaTurma.isEmpty()) {
                listaPanel.add(new JLabel("Nenhum aluno matriculado.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            } else {
                for (Aluno al : alunosDaTurma) {
                    JCheckBox chk = new JCheckBox(al.getNome() + " — " + al.getMatricula());
                    chk.setFont(F_BODY); chk.setOpaque(false);
                    chk.setAlignmentX(Component.LEFT_ALIGNMENT);
                    checks.add(chk); listaPanel.add(chk);
                }
            }
            listaPanel.revalidate(); listaPanel.repaint();
        };
        popularLista.run();
        cbTurma.addActionListener(e -> popularLista.run());

        btnLancar.addActionListener(e -> {
            Turma turmaSel = turmas.get(cbTurma.getSelectedIndex());
            java.util.Date dataSel = (java.util.Date) spinnerData.getValue();
            LocalDate data = dataSel.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            int ok = 0, jaExistia = 0;
            for (int i = 0; i < checks.size(); i++) {
                if (!checks.get(i).isSelected()) continue;
                String res = fachada.registrarFaltaPorData(alunosDaTurma.get(i).getMatricula(),
                    turmaSel.getCodigoTurma(), data);
                if (res == null) ok++; else jaExistia++;
            }
            if (ok == 0 && jaExistia == 0) statusErro(lblStatus, "Selecione ao menos um aluno.");
            else {
                String msg = ok + " falta(s) lançada(s) para " +
                    data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".";
                if (jaExistia > 0) msg += " (" + jaExistia + " já existia(m))";
                statusSucesso(lblStatus, msg);
            }
            checks.forEach(c -> c.setSelected(false));
        });

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}