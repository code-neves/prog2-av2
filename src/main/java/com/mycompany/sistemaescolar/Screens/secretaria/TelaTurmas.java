package com.mycompany.sistemaescolar.Screens.secretaria;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.services.Validador;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaTurmas extends JPanel {

    private final FachadaEscolar fachada;
    private JPanel turmasList;

    public TelaTurmas(FachadaEscolar fachada) {
        this.fachada = fachada;
        setLayout(new BorderLayout());
        setBackground(C_BG);
        construir();
    }

    private void construir() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Gestão de Turmas", "Crie, aloque professores e matricule alunos"));
        content.add(Box.createVerticalStrut(20));
        content.add(secao("Nova turma")); content.add(Box.createVerticalStrut(10));

        JTextField tCod = campo(content, "Código da turma (ex: 7ANO-B)", "MAT-2024-A");
        JLabel lDisc = new JLabel("Disciplina (BNCC)"); lDisc.setFont(F_BOLD); lDisc.setForeground(C_MUTED); lDisc.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] discNomes = {"Língua Portuguesa","Matemática","Ciências","História","Geografia","Arte","Educação Física","Língua Inglesa"};
        JComboBox<String> tDisc = new JComboBox<>(discNomes);
        tDisc.setFont(F_BODY); tDisc.setMaximumSize(new Dimension(9999, 36)); tDisc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lDisc); content.add(Box.createVerticalStrut(3)); content.add(tDisc); content.add(Box.createVerticalStrut(10));

        JTextField tHor = campo(content, "Horário", "Seg/Qua 07:00–08:40");
        JTextField tCap = campo(content, "Limite de alunos", "30");

        JLabel lblStatus = new JLabel(" "); lblStatus.setFont(F_SMALL); lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatus); content.add(Box.createVerticalStrut(4));

        JButton btnCriar = botaoPrimario("Criar turma"); btnCriar.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(btnCriar); content.add(Box.createVerticalStrut(24));

        btnCriar.addActionListener(e -> {
            String cod = tCod.getText().trim(), hor = tHor.getText().trim(), cap = tCap.getText().trim();
            String err = Validador.primeiro(Validador.codigoTurma(cod), Validador.campoVazio(hor, "Horário"), Validador.inteiroPositivo(cap, "Capacidade"));
            if (err != null) { statusErro(lblStatus, err); return; }
            String res = fachada.criarTurma(cod, (String) tDisc.getSelectedItem(), hor, Integer.parseInt(cap));
            if (res != null) { statusErro(lblStatus, res); return; }
            statusSucesso(lblStatus, "Turma \"" + cod + "\" criada.");
            limparCampos(tCod, tHor, tCap); refresh();
        });

        content.add(secao("Ações")); content.add(Box.createVerticalStrut(8));
        JPanel acoesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoesRow.setOpaque(false); acoesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnAlocar = botao("Alocar professor");
        JButton btnMatric = botao("Matricular alunos");
        acoesRow.add(btnAlocar); acoesRow.add(btnMatric);
        content.add(acoesRow); content.add(Box.createVerticalStrut(24));

        btnAlocar.addActionListener(e -> dialogAlocar());
        btnMatric.addActionListener(e -> dialogMatricular());

        content.add(secao("Turmas cadastradas")); content.add(Box.createVerticalStrut(6));
        turmasList = new JPanel(); turmasList.setLayout(new BoxLayout(turmasList, BoxLayout.Y_AXIS));
        turmasList.setOpaque(false); turmasList.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(turmasList); content.add(Box.createVerticalGlue());

        add(scrollSemBorda(content), BorderLayout.CENTER);
    }

    public void refresh() {
        if (turmasList == null) return;
        turmasList.removeAll();
        List<Turma> turmas = fachada.getTurmas();
        if (turmas.isEmpty()) {
            turmasList.add(new JLabel("Nenhuma turma cadastrada ainda.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
        } else {
            String[] cols = {"Código","Disciplina","Horário","Professor","Alunos / Limite","Status"};
            String[][] data = new String[turmas.size()][6];
            for (int i = 0; i < turmas.size(); i++) {
                Turma t = turmas.get(i);
                int al = t.getAlunos().size(), cap = t.getCapacidadeMaxima();
                String prof   = t.getProfessor() != null ? t.getProfessor().getNome() : "— sem professor —";
                String status = t.estaCheia() ? "Cheia" : (al >= cap * 0.9 ? "Quase cheia" : "Aberta");
                data[i] = new String[]{t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(), prof, al + " / " + cap, status};
            }
            JTable tabela = criarTabela(cols, data);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1)); sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 260)); turmasList.add(sp);
        }
        turmasList.revalidate(); turmasList.repaint();
    }

    private void dialogAlocar() {
        List<Professor> profs  = fachada.getProfessores();
        List<Turma>     turmas = fachada.getTurmas();
        if (profs.isEmpty())  { JOptionPane.showMessageDialog(this, "Nenhum professor cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        if (turmas.isEmpty()) { JOptionPane.showMessageDialog(this, "Nenhuma turma cadastrada.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        JComboBox<String> cbP = new JComboBox<>(profs.stream().map(p -> p.getNome() + " — " + p.getCpf()).toArray(String[]::new));
        JComboBox<String> cbT = new JComboBox<>(turmas.stream().map(t -> t.getCodigoTurma() + " (" + t.getDisciplina().getNome() + ")").toArray(String[]::new));
        cbP.setFont(F_BODY); cbT.setFont(F_BODY);

        JPanel p = new JPanel(new GridLayout(4, 1, 6, 6)); p.setBorder(new EmptyBorder(12,12,12,12));
        p.add(new JLabel("Professor:") {{ setFont(F_BOLD); }}); p.add(cbP);
        p.add(new JLabel("Turma:") {{ setFont(F_BOLD); }}); p.add(cbT);

        if (JOptionPane.showConfirmDialog(this, p, "Alocar professor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String res = fachada.alocarProfessorATurma(profs.get(cbP.getSelectedIndex()).getCpf(), turmas.get(cbT.getSelectedIndex()).getCodigoTurma());
        if (res != null) JOptionPane.showMessageDialog(this, res, "Erro", JOptionPane.ERROR_MESSAGE);
        else { JOptionPane.showMessageDialog(this, "Professor alocado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); refresh(); }
    }

    private void dialogMatricular() {
        List<Aluno> alunos = fachada.getAlunos();
        List<Turma> turmas = fachada.getTurmas();
        if (alunos.isEmpty()) { JOptionPane.showMessageDialog(this, "Nenhum aluno cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        if (turmas.isEmpty()) { JOptionPane.showMessageDialog(this, "Nenhuma turma cadastrada.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        JList<String> lista = new JList<>(alunos.stream().map(a -> a.getNome() + " — " + a.getMatricula()).toArray(String[]::new));
        lista.setFont(F_BODY); lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); lista.setVisibleRowCount(8);
        JComboBox<String> cbT = new JComboBox<>(turmas.stream().map(t -> t.getCodigoTurma() + " (" + t.getDisciplina().getNome() + ")").toArray(String[]::new));
        cbT.setFont(F_BODY);

        JPanel p = new JPanel(new BorderLayout(6, 10)); p.setBorder(new EmptyBorder(12,12,12,12));
        JPanel top = new JPanel(new GridLayout(2,1,4,4)); top.add(new JLabel("Turma:") {{ setFont(F_BOLD); }}); top.add(cbT);
        JPanel mid = new JPanel(new BorderLayout(0,4));
        mid.add(new JLabel("Alunos (Ctrl+clique para vários):") {{ setFont(F_BOLD); }}, BorderLayout.NORTH);
        mid.add(new JScrollPane(lista) {{ setPreferredSize(new Dimension(340, 180)); }}, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH); p.add(mid, BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(this, p, "Matricular alunos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        List<String> sels = lista.getSelectedValuesList();
        if (sels.isEmpty()) { JOptionPane.showMessageDialog(this, "Selecione ao menos um aluno.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        Turma turmaSel = turmas.get(cbT.getSelectedIndex());
        int ok = 0, erros = 0; StringBuilder sb = new StringBuilder();
        String[] nomesArr = alunos.stream().map(a -> a.getNome() + " — " + a.getMatricula()).toArray(String[]::new);
        for (String sel : sels) {
            int idx = java.util.Arrays.asList(nomesArr).indexOf(sel);
            String res = fachada.matricularAlunoEmTurma(alunos.get(idx).getMatricula(), turmaSel.getCodigoTurma());
            if (res != null) { erros++; sb.append("• ").append(alunos.get(idx).getNome()).append(": ").append(res).append("\n"); }
            else ok++;
        }
        String msg = ok + " aluno(s) matriculado(s)."; if (erros > 0) msg += "\nErros:\n" + sb;
        JOptionPane.showMessageDialog(this, msg, erros == 0 ? "Sucesso" : "Resultado parcial", erros == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        refresh();
    }
}