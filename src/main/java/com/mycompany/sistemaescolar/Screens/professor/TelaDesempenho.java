package com.mycompany.sistemaescolar.Screens.professor;

import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;
import com.mycompany.sistemaescolar.models.Pessoa;
import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.services.Validador;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaDesempenho extends JPanel {

    private final FachadaEscolar fachada;

    public TelaDesempenho(FachadaEscolar fachada, Pessoa pessoa) {
        this.fachada = fachada;
        setLayout(new BorderLayout());
        setBackground(C_BG);
    }

    public void refresh() {
        removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Lançar Desempenho", "Registre notas e faltas por aluno"));
        content.add(Box.createVerticalStrut(16));

        JPanel cardForm = card();
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));

        JTextField fMat    = campo(cardForm, "Matrícula do aluno", "2024001");
        JTextField fNota   = campo(cardForm, "Nota (0,0 – 10,0)", "7.5");
        JTextField fFaltas = campo(cardForm, "Faltas (quantidade)", "0");

        JLabel lObs = new JLabel("Observação (opcional)");
        lObs.setFont(F_BOLD); lObs.setForeground(C_MUTED);
        lObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea fObs = new JTextArea(2, 20);
        fObs.setFont(F_BODY); fObs.setLineWrap(true);
        fObs.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane scrollObs = new JScrollPane(fObs);
        scrollObs.setBorder(null);
        scrollObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollObs.setMaximumSize(new Dimension(9999, 60));
        cardForm.add(lObs); cardForm.add(Box.createVerticalStrut(4));
        cardForm.add(scrollObs); cardForm.add(Box.createVerticalStrut(12));

        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(F_SMALL); lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardForm.add(lblStatus); cardForm.add(Box.createVerticalStrut(6));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        JButton btnSalvar = botaoPrimario("Salvar lançamento");
        JButton btnLimpar = botao("Limpar");
        btnRow.add(btnSalvar); btnRow.add(Box.createHorizontalStrut(8)); btnRow.add(btnLimpar);
        cardForm.add(btnRow);

        btnSalvar.addActionListener(e -> {
            String mat      = fMat.getText().trim();
            String nota     = fNota.getText().trim();
            String faltaStr = fFaltas.getText().trim();
            String err = Validador.primeiro(
                Validador.matricula(mat),
                Validador.nota(nota),
                Validador.inteiroPositivo(faltaStr.isEmpty() ? "1" : faltaStr, "Faltas")
            );
            if (err != null) { statusErro(lblStatus, err); return; }
            int qtd = faltaStr.isEmpty() ? 0 : Integer.parseInt(faltaStr);
            if (qtd > 0) {
                String res = fachada.registrarFalta(mat, qtd);
                if (res != null) { statusErro(lblStatus, res); return; }
            }
            statusSucesso(lblStatus, "Desempenho lançado para matrícula " + mat + ".");
            limparCampos(fMat, fNota, fFaltas); fObs.setText("");
        });

        btnLimpar.addActionListener(e -> {
            limparCampos(fMat, fNota, fFaltas); fObs.setText(""); lblStatus.setText(" ");
        });

        content.add(cardForm);
        content.add(Box.createVerticalStrut(20));
        content.add(secao("Desempenho dos alunos"));
        content.add(Box.createVerticalStrut(6));

        try {
            String rel = fachada.listarDesempenhoAlunos();
            String[] cols = {"Matrícula", "Nome", "Média", "Faltas", "Meta (%)"};
            JTable tabela = criarTabelaDeTexto(rel, cols);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 200));
            content.add(sp);
        } catch (UserNotFoundException ex) {
            JLabel vz = new JLabel("Nenhum aluno cadastrado ainda.");
            vz.setFont(F_BODY); vz.setForeground(C_MUTED);
            content.add(vz);
        }

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}