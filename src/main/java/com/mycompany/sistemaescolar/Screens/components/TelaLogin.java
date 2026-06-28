package com.mycompany.sistemaescolar.Screens.components;

import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.services.LoginResult;
import com.mycompany.sistemaescolar.models.Pessoa;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaLogin extends JFrame {

    private final FachadaEscolar fachada;

    private JToggleButton[] perfilBtns;
    private JTextField      campoId;
    private JPasswordField  campoSenha;
    private JLabel          lblErro;

    private static final String[] PERFIS    = {"Aluno", "Professor", "Coordenação"};
    private static final String[] PERFIL_ID = {"aluno", "professor", "coord"};

    public TelaLogin(FachadaEscolar fachada) {
        this.fachada = fachada;
        configurar();
        construir();
    }

    private void configurar() {
        setTitle("EduSystem — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_BG);
    }

    private void construir() {
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(C_BG);
        centro.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Logo
        JLabel logo = new JLabel("EduSystem");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(C_TEXT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Plataforma de gestão escolar");
        sub.setFont(F_SMALL); sub.setForeground(C_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(logo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(sub);
        centro.add(Box.createVerticalStrut(28));

        // Seletor de perfil
        JLabel lblPerfil = new JLabel("Perfil de acesso");
        lblPerfil.setFont(F_BOLD); lblPerfil.setForeground(C_MUTED);
        lblPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(lblPerfil);
        centro.add(Box.createVerticalStrut(8));

        JPanel perfilPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        perfilPanel.setBackground(C_BG);
        perfilPanel.setMaximumSize(new Dimension(9999, 50));
        ButtonGroup bg = new ButtonGroup();
        perfilBtns = new JToggleButton[3];
        for (int i = 0; i < 3; i++) {
            perfilBtns[i] = toggle(PERFIS[i]);
            bg.add(perfilBtns[i]);
            perfilPanel.add(perfilBtns[i]);
        }
        perfilBtns[0].setSelected(true);
        centro.add(perfilPanel);
        centro.add(Box.createVerticalStrut(20));

        // Campos
        JLabel lblId = new JLabel("CPF / \"root\" para coordenação");
        lblId.setFont(F_BOLD); lblId.setForeground(C_MUTED);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoId = estilizarCampo(new JTextField());
        campoId.setMaximumSize(new Dimension(9999, 36));
        campoId.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSenha = new JLabel("Senha (aluno: matrícula · professor: CPF)");
        lblSenha.setFont(F_BOLD); lblSenha.setForeground(C_MUTED);
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoSenha = estilizarCampo(new JPasswordField());
        campoSenha.setMaximumSize(new Dimension(9999, 36));
        campoSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblErro = new JLabel(" ");
        lblErro.setFont(F_SMALL); lblErro.setForeground(C_DANGER);
        lblErro.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(lblId);   centro.add(Box.createVerticalStrut(4));
        centro.add(campoId); centro.add(Box.createVerticalStrut(12));
        centro.add(lblSenha);   centro.add(Box.createVerticalStrut(4));
        centro.add(campoSenha); centro.add(Box.createVerticalStrut(6));
        centro.add(lblErro);    centro.add(Box.createVerticalStrut(12));

        // Botão entrar
        JButton btnEntrar = botaoPrimario("Entrar");
        btnEntrar.setMaximumSize(new Dimension(9999, 40));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(btnEntrar);

        // Preenche root ao selecionar coordenação
        perfilBtns[2].addActionListener(e -> { campoId.setText("root"); campoSenha.setText("root"); });
        perfilBtns[0].addActionListener(e -> { campoId.setText(""); campoSenha.setText(""); });
        perfilBtns[1].addActionListener(e -> { campoId.setText(""); campoSenha.setText(""); });

        ActionListener loginAction = e -> tentarLogin();
        btnEntrar.addActionListener(loginAction);
        campoSenha.addActionListener(loginAction);

        add(centro);
    }

    private void tentarLogin() {
        String id    = campoId.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (id.isEmpty() || senha.isEmpty()) {
            lblErro.setText("Preencha todos os campos.");
            return;
        }

        String perfilSelecionado = null;
        for (int i = 0; i < perfilBtns.length; i++)
            if (perfilBtns[i].isSelected()) { perfilSelecionado = PERFIL_ID[i]; break; }

        LoginResult resultado = fachada.autenticar(id, senha);
        if (resultado == null) {
            lblErro.setText("Credenciais inválidas. Tente novamente.");
            campoSenha.setText("");
            return;
        }

        String papelEncontrado = switch (resultado.getPapel()) {
            case ALUNO      -> "aluno";
            case PROFESSOR  -> "professor";
            case COORDENACAO -> "coord";
        };

        if (!papelEncontrado.equals(perfilSelecionado)) {
            lblErro.setText("Perfil selecionado não corresponde às credenciais.");
            campoSenha.setText("");
            return;
        }

        Pessoa pessoa = resultado.getPessoa();
        String nome   = pessoa != null ? pessoa.getNome() : "Coordenação";

        dispose();
        SwingUtilities.invokeLater(() -> {
            telaPrincipal tp = new telaPrincipal(fachada, papelEncontrado, nome, id, pessoa);
            tp.setVisible(true);
        });
    }
}