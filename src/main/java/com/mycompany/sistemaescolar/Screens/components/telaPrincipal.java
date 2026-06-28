package com.mycompany.sistemaescolar.Screens.components;

import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.Screens.aluno.*;
import com.mycompany.sistemaescolar.Screens.professor.*;
import com.mycompany.sistemaescolar.Screens.secretaria.*;
import com.mycompany.sistemaescolar.models.Pessoa;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

/**
 * Janela principal do sistema.
 * Gerencia a sidebar de navegação e o CardLayout central.
 * Recebe perfil e usuário do login e monta as telas corretas.
 */
public class telaPrincipal extends JFrame {

    private static final String ARQUIVO = "banco_dados_escolar.dat";

    private final FachadaEscolar fachada;
    private final String         perfil;
    private final String         nomeUsuario;
    private final String         idUsuario;
    private final Pessoa         pessoaLogada;

    private JPanel    painelConteudo;
    private CardLayout cardLayout;

    // Referências às telas para permitir refresh
    private DashboardAluno      dashAluno;
    private DashboardProfessor  dashProfessor;
    private DashboardSecretaria dashSecretaria;
    private TelaMinhasFaltas    telaMinhasFaltas;
    private TelaDesempenhoAluno telaDesempenhoAluno;
    private TelaCalendarioFaltas telaCalendarioFaltas;
    private TelaDesempenho      telaDesempenho;
    private TelaTurmas          telaTurmas;
    private TelaRelatorios      telaRelatorios;
    private TelaCadastro        telaCadastro;

    public telaPrincipal(FachadaEscolar fachada, String perfil,
                         String nomeUsuario, String idUsuario, Pessoa pessoaLogada) {
        this.fachada      = fachada;
        this.perfil       = perfil;
        this.nomeUsuario  = nomeUsuario;
        this.idUsuario    = idUsuario;
        this.pessoaLogada = pessoaLogada;

        configurarJanela();
        construirTelas();
        construirLayout();
        mostrarTela("dashboard");
    }

    // ----------------------------------------------------------------
    // Configuração da janela
    // ----------------------------------------------------------------
    private void configurarJanela() {
        setTitle("EduSystem");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { salvarESair(); }
        });
        setSize(1250, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
    }

    // ----------------------------------------------------------------
    // Instancia as telas conforme o perfil
    // ----------------------------------------------------------------
    private void construirTelas() {
        switch (perfil) {
            case "aluno" -> {
                dashAluno          = new DashboardAluno(fachada, pessoaLogada, this::mostrarTela);
                telaMinhasFaltas   = new TelaMinhasFaltas(fachada, pessoaLogada);
                telaDesempenhoAluno= new TelaDesempenhoAluno(fachada, pessoaLogada);
            }
            case "professor" -> {
                dashProfessor        = new DashboardProfessor(fachada, pessoaLogada, this::mostrarTela);
                telaDesempenho       = new TelaDesempenho(fachada, pessoaLogada);
                telaCalendarioFaltas = new TelaCalendarioFaltas(fachada, pessoaLogada);
            }
            default -> { // coord / secretaria
                dashSecretaria = new DashboardSecretaria(fachada, this::mostrarTela);
                telaCadastro   = new TelaCadastro(fachada);
                telaTurmas     = new TelaTurmas(fachada);
                telaDesempenho = new TelaDesempenho(fachada, null);
                telaRelatorios = new TelaRelatorios(fachada);
            }
        }
    }

    // ----------------------------------------------------------------
    // Monta sidebar + conteúdo
    // ----------------------------------------------------------------
    private void construirLayout() {
        add(construirSidebar(), BorderLayout.WEST);

        cardLayout    = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(C_BG);

        switch (perfil) {
            case "aluno" -> {
                painelConteudo.add(dashAluno,           "dashboard");
                painelConteudo.add(telaMinhasFaltas,    "faltas");
                painelConteudo.add(telaDesempenhoAluno, "desempenho");
            }
            case "professor" -> {
                painelConteudo.add(dashProfessor,        "dashboard");
                painelConteudo.add(telaDesempenho,       "desempenho");
                painelConteudo.add(telaCalendarioFaltas, "calendario");
            }
            default -> {
                painelConteudo.add(dashSecretaria, "dashboard");
                painelConteudo.add(telaCadastro,   "cadastro");
                painelConteudo.add(telaTurmas,     "turmas");
                painelConteudo.add(telaDesempenho, "desempenho");
                painelConteudo.add(telaRelatorios, "relatorios");
            }
        }

        add(painelConteudo, BorderLayout.CENTER);
    }

    // ----------------------------------------------------------------
    // Sidebar
    // ----------------------------------------------------------------
    private JPanel construirSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(C_SIDEBAR);
        sb.setPreferredSize(new Dimension(200, 0));
        sb.setBorder(new MatteBorder(0, 0, 0, 1, C_BORDER));

        // Logo
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        logoArea.setBackground(C_SIDEBAR);
        logoArea.setBorder(new MatteBorder(0, 0, 1, 0, C_BORDER));
        JLabel logoLabel = new JLabel("EduSystem");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoLabel.setForeground(C_TEXT);
        logoArea.add(logoLabel);
        logoArea.setMaximumSize(new Dimension(9999, 52));
        sb.add(logoArea);
        sb.add(Box.createVerticalStrut(8));

        // Itens de navegação
        for (String[] item : navItens()) sb.add(navItem(item[0], item[1]));
        sb.add(Box.createVerticalGlue());

        // Rodapé
        JPanel foot = new JPanel();
        foot.setLayout(new BoxLayout(foot, BoxLayout.Y_AXIS));
        foot.setBackground(C_SIDEBAR);
        foot.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDER),
            new EmptyBorder(10, 12, 10, 12)
        ));
        JLabel nomeLabel = new JLabel(nomeUsuario);
        nomeLabel.setFont(F_BOLD); nomeLabel.setForeground(C_TEXT);
        JLabel perfilLabel = new JLabel(labelPerfil());
        perfilLabel.setFont(F_SMALL); perfilLabel.setForeground(C_MUTED);
        JButton sair = new JButton("Sair");
        sair.setFont(F_SMALL); sair.setForeground(C_MUTED);
        sair.setBorderPainted(false); sair.setContentAreaFilled(false);
        sair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sair.setAlignmentX(Component.LEFT_ALIGNMENT);
        sair.addActionListener(e -> salvarESair());
        foot.add(nomeLabel); foot.add(perfilLabel);
        foot.add(Box.createVerticalStrut(6)); foot.add(sair);
        foot.setMaximumSize(new Dimension(9999, 100));
        sb.add(foot);
        return sb;
    }

    private String[][] navItens() {
        return switch (perfil) {
            case "aluno"     -> new String[][]{
                {"dashboard",  "Dashboard"},
                {"desempenho", "Meu desempenho"},
                {"faltas",     "Minhas faltas"}
            };
            case "professor" -> new String[][]{
                {"dashboard",  "Dashboard"},
                {"desempenho", "Lançar desempenho"},
                {"calendario", "Calendário de faltas"}
            };
            default          -> new String[][]{
                {"dashboard",  "Dashboard"},
                {"cadastro",   "Cadastro de pessoa"},
                {"turmas",     "Gestão de turmas"},
                {"desempenho", "Lançar desempenho"},
                {"relatorios", "Relatórios"}
            };
        };
    }

    private String labelPerfil() {
        return switch (perfil) {
            case "aluno"     -> "Aluno · " + idUsuario;
            case "professor" -> "Professor";
            default          -> "Coordenação";
        };
    }

    private JButton navItem(String tela, String rotulo) {
        JButton btn = new JButton(rotulo);
        btn.setFont(F_BODY); btn.setForeground(C_MUTED); btn.setBackground(C_SIDEBAR);
        btn.setBorderPainted(false); btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 14, 9, 14));
        btn.setMaximumSize(new Dimension(9999, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(C_BORDER); btn.setForeground(C_TEXT); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(C_SIDEBAR); btn.setForeground(C_MUTED); }
        });
        btn.addActionListener(e -> mostrarTela(tela));
        return btn;
    }

    // ----------------------------------------------------------------
    // Navegação
    // ----------------------------------------------------------------
    public void mostrarTela(String tela) {
        cardLayout.show(painelConteudo, tela);
        // Dispara refresh na tela ativada
        switch (tela) {
            case "dashboard"  -> refreshDashboard();
            case "faltas"     -> { if (telaMinhasFaltas    != null) telaMinhasFaltas.refresh(); }
            case "desempenho" -> { if (telaDesempenho      != null) telaDesempenho.refresh();
                                   if (telaDesempenhoAluno != null) telaDesempenhoAluno.refresh(); }
            case "turmas"     -> { if (telaTurmas          != null) telaTurmas.refresh(); }
            case "relatorios" -> { if (telaRelatorios      != null) telaRelatorios.refresh(); }
            case "calendario" -> { if (telaCalendarioFaltas!= null) telaCalendarioFaltas.refresh(); }
        }
    }

    private void refreshDashboard() {
        if (dashAluno      != null) dashAluno.refresh();
        if (dashProfessor  != null) dashProfessor.refresh();
        if (dashSecretaria != null) dashSecretaria.refresh();
    }

    // ----------------------------------------------------------------
    // Salvar e sair
    // ----------------------------------------------------------------
    private void salvarESair() {
        try { fachada.salvarDados(ARQUIVO); }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }
}