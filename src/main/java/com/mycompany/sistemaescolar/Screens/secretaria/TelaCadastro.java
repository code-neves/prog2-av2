package com.mycompany.sistemaescolar.Screens.secretaria;

import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.services.Validador;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaCadastro extends JPanel {

    private final FachadaEscolar fachada;
    private int contadorMatricula = 1;

    public TelaCadastro(FachadaEscolar fachada) {
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

        content.add(cabecalho("Cadastro de Pessoa", "Cadastre alunos e professores no sistema"));
        content.add(Box.createVerticalStrut(20));

        // Toggle aluno / professor
        JToggleButton togAluno = toggle("Aluno");
        JToggleButton togProf  = toggle("Professor");
        togAluno.setSelected(true);
        ButtonGroup bg = new ButtonGroup(); bg.add(togAluno); bg.add(togProf);
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleRow.setOpaque(false); toggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleRow.setMaximumSize(new Dimension(9999, 40));
        toggleRow.add(togAluno); toggleRow.add(togProf);
        content.add(toggleRow); content.add(Box.createVerticalStrut(16));

        // Campos comuns
        JTextField fNome  = campo(content, "Nome completo", "Ex: Maria Souza");
        JTextField fCpf   = campo(content, "CPF (000.000.000-00)", "000.000.000-00");
        JTextField fEmail = campo(content, "E-mail", "aluno@escola.edu.br");
        JTextField fTel   = campo(content, "Telefone", "(81) 99999-0000");

        // Extra Aluno — matrícula automática
        JPanel extraAluno = new JPanel();
        extraAluno.setLayout(new BoxLayout(extraAluno, BoxLayout.Y_AXIS));
        extraAluno.setOpaque(false); extraAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMat = new JLabel("Matrícula (gerada automaticamente)");
        lblMat.setFont(F_BOLD); lblMat.setForeground(C_MUTED); lblMat.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField fMatDisplay = new JTextField();
        fMatDisplay.setEditable(false); fMatDisplay.setBackground(C_SIDEBAR); fMatDisplay.setForeground(C_MUTED);
        fMatDisplay.setFont(F_BODY);
        fMatDisplay.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(5, 9, 5, 9)));
        fMatDisplay.setMaximumSize(new Dimension(9999, 36)); fMatDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        extraAluno.add(lblMat); extraAluno.add(Box.createVerticalStrut(3));
        extraAluno.add(fMatDisplay); extraAluno.add(Box.createVerticalStrut(10));
        content.add(extraAluno);

        // Extra Professor
        JPanel extraProf = new JPanel();
        extraProf.setLayout(new BoxLayout(extraProf, BoxLayout.Y_AXIS));
        extraProf.setOpaque(false); extraProf.setVisible(false); extraProf.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField fEsp = campo(extraProf, "Especialidade", "Ex: Matemática");
        JTextField fMax = campo(extraProf, "Máx. de Turmas", "3");
        content.add(extraProf);

        fMatDisplay.setText(previewMatricula());

        togAluno.addActionListener(e -> { extraAluno.setVisible(true); extraProf.setVisible(false); fMatDisplay.setText(previewMatricula()); content.revalidate(); });
        togProf.addActionListener(e ->  { extraAluno.setVisible(false); extraProf.setVisible(true); content.revalidate(); });

        content.add(Box.createVerticalStrut(12));
        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(F_SMALL); lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatus); content.add(Box.createVerticalStrut(6));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnSalvar = botaoPrimario("Salvar");
        JButton btnLimpar = botao("Limpar");
        btnRow.add(btnSalvar); btnRow.add(Box.createHorizontalStrut(8)); btnRow.add(btnLimpar);
        content.add(btnRow);

        btnSalvar.addActionListener(e -> {
            String nome  = fNome.getText().trim();
            String cpf   = fCpf.getText().trim();
            String email = fEmail.getText().trim();
            String tel   = fTel.getText().trim();

            String err = Validador.primeiro(Validador.nome(nome), Validador.cpf(cpf), Validador.email(email), Validador.telefone(tel));

            if (err == null && togProf.isSelected())
                err = Validador.primeiro(Validador.campoVazio(fEsp.getText().trim(), "Especialidade"),
                    Validador.inteiroPositivo(fMax.getText().trim(), "Máx. de Turmas"));

            if (err != null) { statusErro(lblStatus, err); return; }

            String res;
            if (togAluno.isSelected()) {
                String mat = gerarMatricula();
                res = fachada.cadastrarAluno(nome, cpf, email, tel, mat, 0.0);
                if (res == null) { statusSucesso(lblStatus, "Aluno \"" + nome + "\" cadastrado. Matrícula: " + mat); fMatDisplay.setText(previewMatricula()); }
            } else {
                res = fachada.cadastrarProfessor(nome, cpf, email, tel, fEsp.getText().trim(), Integer.parseInt(fMax.getText().trim()));
                if (res == null) statusSucesso(lblStatus, "Professor \"" + nome + "\" cadastrado com sucesso.");
            }
            if (res != null) { statusErro(lblStatus, res); return; }
            limparCampos(fNome, fCpf, fEmail, fTel, fEsp, fMax);
        });

        btnLimpar.addActionListener(e -> { limparCampos(fNome, fCpf, fEmail, fTel, fEsp, fMax); lblStatus.setText(" "); });

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
    }

    private String gerarMatricula() {
        int ano = java.time.Year.now().getValue();
        return String.format("%d%03d", ano, contadorMatricula++);
    }

    private String previewMatricula() {
        int ano = java.time.Year.now().getValue();
        return String.format("%d%03d", ano, contadorMatricula);
    }
}