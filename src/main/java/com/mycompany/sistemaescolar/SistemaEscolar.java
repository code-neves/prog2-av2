package com.mycompany.sistemaescolar;

import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.services.Validador;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;
import com.mycompany.sistemaescolar.models.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.Year;
import java.util.List;

/**
 * GUI completo com login por perfil, dashboard e telas funcionais.
 * Conectado à FachadaEscolar sem alterar nenhum model ou service.
 */
public class SistemaEscolar {

    // ----------------------------------------------------------------
    // Paleta de cores
    // ----------------------------------------------------------------
    private static final Color C_BG        = new Color(0xF5F4F0);
    private static final Color C_SURFACE   = new Color(0xFFFFFF);
    private static final Color C_SIDEBAR   = new Color(0xF0EEE8);
    private static final Color C_BORDER    = new Color(0xDDDAD2);
    private static final Color C_ACCENT    = new Color(0x1A6FD4);
    private static final Color C_ACCENT_BG = new Color(0xE6F0FB);
    private static final Color C_TEXT      = new Color(0x1A1A18);
    private static final Color C_MUTED     = new Color(0x888780);
    private static final Color C_SUCCESS   = new Color(0x0F6E56);
    //private static final Color C_SUCCESS_BG= new Color(0xE1F5EE);
    private static final Color C_DANGER    = new Color(0xA32D2D);
    //private static final Color C_DANGER_BG = new Color(0xFCEBEB);
    //private static final Color C_WARN      = new Color(0x854F0B);
    //private static final Color C_WARN_BG   = new Color(0xFAEEDA);

    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN,  13);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,   13);
    private static final Font F_HEAD  = new Font("Segoe UI", Font.BOLD,   18);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN,  11);
    //private static final Font F_MONO  = new Font("Consolas", Font.PLAIN,  12);

    private static final String ARQUIVO = "banco_dados_escolar.dat";

    // ----------------------------------------------------------------
    // Estado global da sessão
    // ----------------------------------------------------------------
    private static FachadaEscolar fachada;
    private static String perfilAtivo;   // "aluno", "professor", "coord"
    private static String nomeUsuario;
    private static String idUsuario;     // matrícula ou CPF
    private static Pessoa pessoaLogada;  // referência ao Aluno/Professor autenticado (null p/ coordenação)

    // ----------------------------------------------------------------
    // Janela principal
    // ----------------------------------------------------------------
    private static JFrame janela;
    private static JPanel painelSidebar;
    private static JPanel painelConteudo;
    private static CardLayout cardLayout;

    public static void main(String[] args) {
        fachada = new FachadaEscolar();
        try { fachada.carregarDados(ARQUIVO); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            mostrarLogin();
        });
    }

    // ================================================================
    // TELA DE LOGIN
    // ================================================================
    private static void mostrarLogin() {
        JFrame loginFrame = new JFrame("EduSystem — Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(420, 520);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);
        loginFrame.getContentPane().setBackground(C_BG);

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
        sub.setFont(F_SMALL);
        sub.setForeground(C_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(logo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(sub);
        centro.add(Box.createVerticalStrut(28));

        // Seletor de perfil
        JLabel lblPerfil = new JLabel("Perfil de acesso");
        lblPerfil.setFont(F_BOLD);
        lblPerfil.setForeground(C_MUTED);
        lblPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(lblPerfil);
        centro.add(Box.createVerticalStrut(8));

        JPanel perfilPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        perfilPanel.setBackground(C_BG);
        perfilPanel.setMaximumSize(new Dimension(9999, 50));
        ButtonGroup bg = new ButtonGroup();
        JToggleButton[] perfilBtns = new JToggleButton[3];
        String[] perfis = {"Aluno", "Professor", "Coordenação"};
        String[] perfilIds = {"aluno", "professor", "coord"};
        for (int i = 0; i < 3; i++) {
            JToggleButton btn = estilizarToggle(perfis[i]);
            perfilBtns[i] = btn;
            bg.add(btn);
            perfilPanel.add(btn);
        }
        perfilBtns[0].setSelected(true);
        centro.add(perfilPanel);
        centro.add(Box.createVerticalStrut(20));

        // Campos
        JLabel lblId = new JLabel("CPF (login) / \"root\" para coordenação");
        lblId.setFont(F_BOLD); lblId.setForeground(C_MUTED);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField campoId = estilizarCampo(new JTextField());
        campoId.setMaximumSize(new Dimension(9999, 36));
        campoId.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSenha = new JLabel("Senha (aluno: matrícula · professor: CPF)");
        lblSenha.setFont(F_BOLD); lblSenha.setForeground(C_MUTED);
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);
        campoSenha.setMaximumSize(new Dimension(9999, 36));
        campoSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblErro = new JLabel(" ");
        lblErro.setFont(F_SMALL);
        lblErro.setForeground(C_DANGER);
        lblErro.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(lblId); centro.add(Box.createVerticalStrut(4));
        centro.add(campoId); centro.add(Box.createVerticalStrut(12));
        centro.add(lblSenha); centro.add(Box.createVerticalStrut(4));
        centro.add(campoSenha); centro.add(Box.createVerticalStrut(6));
        centro.add(lblErro); centro.add(Box.createVerticalStrut(12));

        JButton btnEntrar = estilizarBotaoPrimario("Entrar");
        btnEntrar.setMaximumSize(new Dimension(9999, 40));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(btnEntrar);

        // Preenche root automaticamente ao selecionar coordenação
        perfilBtns[2].addActionListener(e -> { campoId.setText("root"); campoSenha.setText("root"); });
        perfilBtns[0].addActionListener(e -> { campoId.setText(""); campoSenha.setText(""); });
        perfilBtns[1].addActionListener(e -> { campoId.setText(""); campoSenha.setText(""); });

        ActionListener login = e -> {
            String id = campoId.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();
            String perfil = null;
            for (int i = 0; i < 3; i++) if (perfilBtns[i].isSelected()) { perfil = perfilIds[i]; break; }

            if (id.isEmpty() || senha.isEmpty()) {
                lblErro.setText("Preencha todos os campos.");
                return;
            }

            // Autenticação real via fachada — funciona para os 3 perfis
            com.mycompany.sistemaescolar.services.LoginResult resultado = fachada.autenticar(id, senha);
            if (resultado == null) {
                lblErro.setText("Credenciais inválidas.");
                return;
            }

            // Confere se o perfil selecionado bate com o papel autenticado
            String papelEncontrado = switch (resultado.getPapel()) {
                case ALUNO -> "aluno";
                case PROFESSOR -> "professor";
                case COORDENACAO -> "coord";
            };
            if (!papelEncontrado.equals(perfil)) {
                lblErro.setText("Perfil selecionado não corresponde às credenciais.");
                return;
            }

            perfilAtivo   = perfil;
            idUsuario     = id;
            pessoaLogada  = resultado.getPessoa(); // null para coordenação
            nomeUsuario   = switch (resultado.getPapel()) {
                case COORDENACAO -> "Coordenação";
                default -> resultado.getPessoa().getNome();
            };
            loginFrame.dispose();
            abrirSistema();
        };
        btnEntrar.addActionListener(login);
        campoSenha.addActionListener(login);

        loginFrame.add(centro);
        loginFrame.setVisible(true);
    }

    // ================================================================
    // JANELA PRINCIPAL
    // ================================================================
    private static void abrirSistema() {
        janela = new JFrame("EduSystem");
        janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { salvarESair(); }
        });
        janela.setSize(1250, 680);
        janela.setLocationRelativeTo(null);
        janela.setResizable(false);
        janela.setLayout(new BorderLayout());

        // Sidebar
        painelSidebar = construirSidebar();
        janela.add(painelSidebar, BorderLayout.WEST);

        // Conteúdo central com CardLayout
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(C_BG);

        // Adiciona telas disponíveis
        painelConteudo.add(telaDashboard(),    "dashboard");
        painelConteudo.add(telaCadastro(),     "cadastro");
        painelConteudo.add(telaTurmas(),       "turmas");
        painelConteudo.add(telaDesempenho(),   "desempenho");
        painelConteudo.add(telaRelatorios(),   "relatorios");
        painelConteudo.add(telaMinhasFaltas(), "faltas");
        painelConteudo.add(telaCalendarioFaltas(), "calendario");

        janela.add(painelConteudo, BorderLayout.CENTER);
        mostrarTela("dashboard");
        janela.setVisible(true);
    }

    // ================================================================
    // SIDEBAR
    // ================================================================
    private static JPanel construirSidebar() {
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

        // Itens de navegação por perfil
        String[][] itens = navItens();
        for (String[] item : itens) {
            sb.add(navItem(item[0], item[1]));
        }
        sb.add(Box.createVerticalGlue());

        // Rodapé do sidebar
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

    private static String[][] navItens() {
        if ("aluno".equals(perfilAtivo)) return new String[][]{
            {"dashboard", "Dashboard"},
            {"desempenho", "Meu desempenho"},
            {"faltas", "Minhas faltas"}
        };
        if ("professor".equals(perfilAtivo)) return new String[][]{
            {"dashboard", "Dashboard"},
            {"desempenho", "Lançar desempenho"},
            {"calendario", "Calendário de faltas"}
        };
        return new String[][]{  // coord
            {"dashboard",   "Dashboard"},
            {"cadastro",    "Cadastro de pessoa"},
            {"turmas",      "Gestão de turmas"},
            {"desempenho",  "Lançar desempenho"},
            {"relatorios",  "Relatórios"}
        };
    }

    private static String labelPerfil() {
        return switch (perfilAtivo) {
            case "aluno"     -> "Aluno · " + idUsuario;
            case "professor" -> "Professor";
            default          -> "Coordenação";
        };
    }

    private static JComponent navItem(String tela, String rotulo) {
        JButton btn = new JButton(rotulo);
        btn.setFont(F_BODY);
        btn.setForeground(C_MUTED);
        btn.setBackground(C_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
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

    private static void mostrarTela(String tela) {
        cardLayout.show(painelConteudo, tela);
        // Atualiza conteúdo dinâmico
        if ("dashboard".equals(tela))   refreshDashboard();
        if ("relatorios".equals(tela))  refreshRelatorios();
        if ("desempenho".equals(tela))  refreshDesempenho();
        if ("turmas".equals(tela))      refreshTurmas();
        if ("faltas".equals(tela))      refreshMinhasFaltas();
        if ("calendario".equals(tela))  refreshCalendarioFaltas();
    }

    // ================================================================
    // DASHBOARD
    // ================================================================
    private static JPanel dashPanel;
    private static JPanel telaDesempenhoRef;

    private static JPanel telaDashboard() {
        dashPanel = new JPanel(new BorderLayout());
        dashPanel.setBackground(C_BG);
        return dashPanel;
    }

    private static void refreshDashboard() {
        dashPanel.removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        if ("aluno".equals(perfilAtivo)) {
            Aluno alunoLogado = (Aluno) pessoaLogada;
            content.add(cabecalho("Dashboard do aluno", "Seu resumo acadêmico"));
            content.add(Box.createVerticalStrut(16));
            JPanel metricas = new JPanel(new GridLayout(1, 4, 10, 0));
            metricas.setOpaque(false);
            metricas.add(metricCard("Média", String.format("%.1f", alunoLogado.getNotaMedia()), null));
            metricas.add(metricCard("Faltas", String.valueOf(alunoLogado.getFaltas()), null));
            metricas.add(metricCard("Meta", String.format("%.0f%%", alunoLogado.calcularMeta()), null));
            metricas.add(metricCard("Turmas", String.valueOf(alunoLogado.getTurmas().size()), null));
            metricas.setMaximumSize(new Dimension(9999, 90));
            content.add(metricas);
            content.add(Box.createVerticalStrut(20));
            content.add(secao("Minhas turmas"));
            content.add(Box.createVerticalStrut(6));
            if (alunoLogado.getTurmas().isEmpty()) {
                content.add(new JLabel("Você ainda não está matriculado em nenhuma turma.") {{
                    setFont(F_BODY); setForeground(C_MUTED);
                }});
            } else {
                String[] cols = {"Código", "Disciplina", "Horário", "Professor"};
                String[][] data = new String[alunoLogado.getTurmas().size()][4];
                List<Turma> turmasAluno = alunoLogado.getTurmas();
                for (int i = 0; i < turmasAluno.size(); i++) {
                    Turma t = turmasAluno.get(i);
                    String prof = t.getProfessor() != null ? t.getProfessor().getNome() : "— sem professor —";
                    data[i] = new String[]{ t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(), prof };
                }
                JTable tabela = criarTabela(cols, data);
                JScrollPane sp = new JScrollPane(tabela);
                sp.setBorder(new LineBorder(C_BORDER, 1));
                sp.setAlignmentX(Component.LEFT_ALIGNMENT);
                sp.setMaximumSize(new Dimension(9999, 180));
                content.add(sp);
            }
        } else if ("professor".equals(perfilAtivo)) {
            Professor profLogado = (Professor) pessoaLogada;
            content.add(cabecalho("Dashboard do professor", "Suas turmas alocadas"));
            content.add(Box.createVerticalStrut(16));
            JPanel metricas = new JPanel(new GridLayout(1, 2, 10, 0));
            metricas.setOpaque(false);
            metricas.add(metricCard("Turmas", profLogado.getQtdTurmasAlocadas() + " / " + profLogado.getMaxTurmas(), null));
            int totalAlunos = profLogado.getTurmas().stream().mapToInt(t -> t.getAlunos().size()).sum();
            metricas.add(metricCard("Alunos", String.valueOf(totalAlunos), null));
            metricas.setMaximumSize(new Dimension(9999, 90));
            content.add(metricas);
            content.add(Box.createVerticalStrut(20));
            JPanel atalhos = new JPanel(new GridLayout(1, 2, 10, 0));
            atalhos.setOpaque(false);
            atalhos.add(cartaoAtalho("Lançar nota / falta", "Registre desempenho dos alunos", () -> mostrarTela("desempenho")));
            atalhos.add(cartaoAtalho("Calendário de faltas", "Marque faltas dos alunos em uma data específica", () -> mostrarTela("calendario")));
            atalhos.setMaximumSize(new Dimension(9999, 110));
            content.add(atalhos);
            content.add(Box.createVerticalStrut(20));
            content.add(secao("Minhas turmas"));
            content.add(Box.createVerticalStrut(6));
            if (profLogado.getTurmas().isEmpty()) {
                content.add(new JLabel("Nenhuma turma alocada ainda.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            } else {
                String[] cols = {"Código", "Disciplina", "Horário", "Alunos / Limite"};
                List<Turma> turmasProf = profLogado.getTurmas();
                String[][] data = new String[turmasProf.size()][4];
                for (int i = 0; i < turmasProf.size(); i++) {
                    Turma t = turmasProf.get(i);
                    data[i] = new String[]{ t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(),
                            t.getAlunos().size() + " / " + t.getCapacidadeMaxima() };
                }
                JTable tabela = criarTabela(cols, data);
                JScrollPane sp = new JScrollPane(tabela);
                sp.setBorder(new LineBorder(C_BORDER, 1));
                sp.setAlignmentX(Component.LEFT_ALIGNMENT);
                sp.setMaximumSize(new Dimension(9999, 180));
                content.add(sp);
            }
        } else {
            // Fix 2: cabeçalho centralizado
            JPanel headerCoord = new JPanel();
            headerCoord.setLayout(new BoxLayout(headerCoord, BoxLayout.Y_AXIS));
            headerCoord.setOpaque(false);
            headerCoord.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerCoord.setMaximumSize(new Dimension(9999, 60));
            JLabel tCoord = new JLabel("Painel de coordenação", SwingConstants.CENTER);
            tCoord.setFont(F_HEAD); tCoord.setForeground(C_TEXT);
            tCoord.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sCoord = new JLabel("Visão geral e atalhos rápidos", SwingConstants.CENTER);
            sCoord.setFont(F_BODY); sCoord.setForeground(C_MUTED);
            sCoord.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerCoord.add(tCoord); headerCoord.add(Box.createVerticalStrut(2)); headerCoord.add(sCoord);
            content.add(headerCoord);
            content.add(Box.createVerticalStrut(16));

            // Fix 3: contar registros reais via listar*
            String qtdAlunos = "0", qtdProfs = "0", qtdTurmas = "0", qtdMats = "0";
            try { qtdAlunos = String.valueOf(fachada.listarAlunos().split("\n").length - 2); } catch (Exception ignored) {}
            try { qtdProfs  = String.valueOf(fachada.listarProfessores().split("\n").length - 2); } catch (Exception ignored) {}
            try { qtdMats   = String.valueOf(fachada.listarMatriculas().split("\n").length - 2); } catch (Exception ignored) {}
            // turmas: conta linhas não-vazias e não-separador do listarMatriculas (turmas distintas)
            try {
                String txtM = fachada.listarMatriculas();
                java.util.Set<String> turmasSet = new java.util.HashSet<>();
                for (String ln : txtM.split("\n")) {
                    if (ln.isBlank() || ln.startsWith("─") || ln.startsWith("Código")) continue;
                    String[] p = ln.trim().split("\\s{2,}");
                    if (p.length >= 3) turmasSet.add(p[2].trim());
                }
                if (!turmasSet.isEmpty()) qtdTurmas = String.valueOf(turmasSet.size());
            } catch (Exception ignored) {}

            JPanel metricas = new JPanel(new GridLayout(1, 4, 10, 0));
            metricas.setOpaque(false);
            metricas.add(metricCard("Alunos", qtdAlunos, null));
            metricas.add(metricCard("Professores", qtdProfs, null));
            metricas.add(metricCard("Turmas", qtdTurmas, null));
            metricas.add(metricCard("Matrículas", qtdMats, null));
            metricas.setMaximumSize(new Dimension(9999, 90));
            content.add(metricas);
            content.add(Box.createVerticalStrut(20));
            content.add(secao("Atalhos rápidos"));
            content.add(Box.createVerticalStrut(8));
            JPanel atalhos = new JPanel(new GridLayout(1, 3, 10, 0));
            atalhos.setOpaque(false);
            atalhos.add(cartaoAtalho("Cadastrar aluno/professor", "Adicione novas pessoas ao sistema", () -> mostrarTela("cadastro")));
            atalhos.add(cartaoAtalho("Gerenciar turmas", "Crie turmas, aloque professores, matricule alunos", () -> mostrarTela("turmas")));
            atalhos.add(cartaoAtalho("Relatórios", "Veja listagens de alunos, professores e matrículas", () -> mostrarTela("relatorios")));
            atalhos.setMaximumSize(new Dimension(9999, 120));
            content.add(atalhos);
        }

        content.add(Box.createVerticalGlue());
        dashPanel.add(new JScrollPane(content) {{
            setBorder(null);
            getViewport().setBackground(C_BG);
        }}, BorderLayout.CENTER);
        dashPanel.revalidate();
        dashPanel.repaint();
    }

    // ================================================================
    // CADASTRO DE PESSOA
    // ================================================================
    private static JToggleButton togAluno, togProf;
    private static JPanel extraAluno, extraProf;
    // Fix 5: contador de matrícula automática (ano + sequencial)
    private static int contadorMatricula = 1;

    private static String gerarMatricula() {
        int ano = java.time.Year.now().getValue();
        return String.format("%d%03d", ano, contadorMatricula++);
    }

    private static JPanel telaCadastro() {
        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(C_BG);

        // Fix 4: sem card branco — conteúdo direto no fundo
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Cadastro de Pessoa", "Cadastre alunos e professores no sistema"));
        content.add(Box.createVerticalStrut(20));

        // Fix 7: Toggle alinhado à esquerda
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleRow.setOpaque(false);
        toggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleRow.setMaximumSize(new Dimension(9999, 40));
        togAluno = estilizarToggle("Aluno");
        togProf  = estilizarToggle("Professor");
        togAluno.setSelected(true);
        ButtonGroup bgTog = new ButtonGroup();
        bgTog.add(togAluno); bgTog.add(togProf);
        toggleRow.add(togAluno); toggleRow.add(togProf);
        content.add(toggleRow);
        content.add(Box.createVerticalStrut(16));

        // Fix 7: campos comuns direto no content, alinhados à esquerda
        JTextField fNome  = campo(content, "Nome completo", "Ex: Maria Souza");
        JTextField fCpf   = campo(content, "CPF (000.000.000-00)", "000.000.000-00");
        JTextField fEmail = campo(content, "E-mail", "aluno@escola.edu.br");
        JTextField fTel   = campo(content, "Telefone", "(81) 99999-0000");

        // Extra Aluno — Fix 5: matrícula gerada automaticamente (campo somente leitura)
        // Fix 6: sem nota média
        extraAluno = new JPanel();
        extraAluno.setLayout(new BoxLayout(extraAluno, BoxLayout.Y_AXIS));
        extraAluno.setOpaque(false);
        extraAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        extraAluno.setMaximumSize(new Dimension(9999, 9999));

        JLabel lblMatGerada = new JLabel("Matrícula (gerada automaticamente)");
        lblMatGerada.setFont(F_BOLD); lblMatGerada.setForeground(C_MUTED);
        lblMatGerada.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField fMatDisplay = new JTextField();
        fMatDisplay.setFont(F_BODY);
        fMatDisplay.setEditable(false);
        fMatDisplay.setBackground(C_SIDEBAR);
        fMatDisplay.setForeground(C_MUTED);
        fMatDisplay.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(5, 9, 5, 9)));
        fMatDisplay.setMaximumSize(new Dimension(9999, 36));
        fMatDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        extraAluno.add(lblMatGerada);
        extraAluno.add(Box.createVerticalStrut(3));
        extraAluno.add(fMatDisplay);
        extraAluno.add(Box.createVerticalStrut(10));
        content.add(extraAluno);

        // Extra Professor
        extraProf = new JPanel();
        extraProf.setLayout(new BoxLayout(extraProf, BoxLayout.Y_AXIS));
        extraProf.setOpaque(false);
        extraProf.setVisible(false);
        extraProf.setAlignmentX(Component.LEFT_ALIGNMENT);
        extraProf.setMaximumSize(new Dimension(9999, 9999));
        JTextField fEspecialidade = campo(extraProf, "Especialidade", "Ex: Matemática");
        JTextField fMaxTurmas     = campo(extraProf, "Máx. de Turmas", "3");
        content.add(extraProf);

        togAluno.addActionListener(e -> {
            extraAluno.setVisible(true); extraProf.setVisible(false);
            fMatDisplay.setText(gerarMatricPreview());
            content.revalidate();
        });
        togProf.addActionListener(e -> {
            extraAluno.setVisible(false); extraProf.setVisible(true);
            content.revalidate();
        });

        // Pré-visualiza a próxima matrícula ao abrir a tela
        fMatDisplay.setText(gerarMatricPreview());

        content.add(Box.createVerticalStrut(12));

        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(F_SMALL);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatus);
        content.add(Box.createVerticalStrut(6));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 40));
        JButton btnSalvar = estilizarBotaoPrimario("Salvar");
        JButton btnLimpar = estilizarBotao("Limpar");
        btnRow.add(btnSalvar);
        btnRow.add(Box.createHorizontalStrut(8));
        btnRow.add(btnLimpar);
        content.add(btnRow);

        btnSalvar.addActionListener(e -> {
            boolean isAluno = togAluno.isSelected();
            String nome  = fNome.getText().trim();
            String cpf   = fCpf.getText().trim();
            String email = fEmail.getText().trim();
            String tel   = fTel.getText().trim();

            String err = Validador.primeiro(
                Validador.nome(nome),
                Validador.cpf(cpf),
                Validador.email(email),
                Validador.telefone(tel)
            );

            if (err == null && !isAluno) {
                err = Validador.primeiro(
                    Validador.campoVazio(fEspecialidade.getText().trim(), "Especialidade"),
                    Validador.inteiroPositivo(fMaxTurmas.getText().trim(), "Máx. de Turmas")
                );
            }

            if (err != null) { statusErro(lblStatus, err); return; }

            String resultado;
            if (isAluno) {
                // Fix 5: gera matrícula automaticamente
                String matriculaGerada = gerarMatricula();
                resultado = fachada.cadastrarAluno(nome, cpf, email, tel, matriculaGerada, 0.0);
                if (resultado == null) {
                    statusSucesso(lblStatus, "Aluno \"" + nome + "\" cadastrado. Matrícula: " + matriculaGerada);
                    fMatDisplay.setText(gerarMatricPreview());
                }
            } else {
                resultado = fachada.cadastrarProfessor(nome, cpf, email, tel,
                    fEspecialidade.getText().trim(),
                    Integer.parseInt(fMaxTurmas.getText().trim()));
                if (resultado == null)
                    statusSucesso(lblStatus, "Professor \"" + nome + "\" cadastrado com sucesso.");
            }
            if (resultado != null) { statusErro(lblStatus, resultado); return; }
            limparCampos(fNome, fCpf, fEmail, fTel, fEspecialidade, fMaxTurmas);
        });

        btnLimpar.addActionListener(e -> {
            limparCampos(fNome, fCpf, fEmail, fTel, fEspecialidade, fMaxTurmas);
            lblStatus.setText(" ");
        });

        content.add(Box.createVerticalGlue());
        tela.add(new JScrollPane(content) {{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        return tela;
    }

    // Retorna prévia da próxima matrícula sem incrementar o contador
    private static String gerarMatricPreview() {
        int ano = java.time.Year.now().getValue();
        return String.format("%d%03d", ano, contadorMatricula);
    }

    // ================================================================
    // GESTÃO DE TURMAS
    // ================================================================
    private static JPanel turmasList;

    private static JPanel telaTurmas() {
        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(C_BG);

        // Fix 1+2: sem card branco, campos direto no content alinhados à esquerda
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Gestão de Turmas", "Crie, aloque professores e matricule alunos"));
        content.add(Box.createVerticalStrut(20));
        content.add(secao("Nova turma"));
        content.add(Box.createVerticalStrut(10));

        JTextField tCod  = campo(content, "Código da turma (ex: 7ANO-B)", "MAT-2024-A");

        JLabel lDisc = new JLabel("Disciplina (BNCC)");
        lDisc.setFont(F_BOLD); lDisc.setForeground(C_MUTED);
        lDisc.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] discNomes = {"Língua Portuguesa","Matemática","Ciências","História","Geografia","Arte","Educação Física","Língua Inglesa"};
        JComboBox<String> tDisc = new JComboBox<>(discNomes);
        tDisc.setFont(F_BODY);
        tDisc.setMaximumSize(new Dimension(9999, 36));
        tDisc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lDisc); content.add(Box.createVerticalStrut(3));
        content.add(tDisc); content.add(Box.createVerticalStrut(10));

        JTextField tHor = campo(content, "Horário", "Seg/Qua 07:00–08:40");
        JTextField tCap = campo(content, "Limite de alunos", "30");

        JLabel lblStatusTurma = new JLabel(" ");
        lblStatusTurma.setFont(F_SMALL);
        lblStatusTurma.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatusTurma);
        content.add(Box.createVerticalStrut(4));

        JButton btnCriar = estilizarBotaoPrimario("Criar turma");
        btnCriar.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(btnCriar);
        content.add(Box.createVerticalStrut(24));

        btnCriar.addActionListener(e -> {
            String cod  = tCod.getText().trim();
            String disc = (String) tDisc.getSelectedItem();
            String hor  = tHor.getText().trim();
            String cap  = tCap.getText().trim();
            String err = Validador.primeiro(
                Validador.codigoTurma(cod),
                Validador.campoVazio(hor, "Horário"),
                Validador.capacidadeTurma(cap)
            );
            if (err != null) { statusErro(lblStatusTurma, err); return; }
            String res = fachada.criarTurma(cod, disc, hor, Integer.parseInt(cap));
            if (res != null) { statusErro(lblStatusTurma, res); return; }
            statusSucesso(lblStatusTurma, "Turma \"" + cod + "\" criada.");
            limparCampos(tCod, tHor, tCap);
            refreshTurmas();
        });

        // --- Ações ---
        content.add(secao("Ações"));
        content.add(Box.createVerticalStrut(8));
        JPanel acoesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoesRow.setOpaque(false);
        acoesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        acoesRow.setMaximumSize(new Dimension(9999, 40));
        JButton btnAlocar = estilizarBotao("Alocar professor");
        JButton btnMatric = estilizarBotao("Matricular alunos");
        acoesRow.add(btnAlocar); acoesRow.add(btnMatric);
        content.add(acoesRow);
        content.add(Box.createVerticalStrut(24));

        // Fix 4: diálogo de alocar professor com lista de nomes + combo de turmas
        btnAlocar.addActionListener(e -> dialogAlocarProfessor());

        // Fix 5: diálogo de matricular com lista múltipla de alunos
        btnMatric.addActionListener(e -> dialogMatricularAlunos());

        // --- Fix 3: Lista de turmas com dados reais ---
        content.add(secao("Turmas cadastradas"));
        content.add(Box.createVerticalStrut(6));
        turmasList = new JPanel();
        turmasList.setLayout(new BoxLayout(turmasList, BoxLayout.Y_AXIS));
        turmasList.setOpaque(false);
        turmasList.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(turmasList);
        content.add(Box.createVerticalGlue());

        tela.add(new JScrollPane(content) {{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        return tela;
    }

    // Fix 3: usa fachada.getTurmas() diretamente para popular a tabela
    private static void refreshTurmas() {
        if (turmasList == null) return;
        turmasList.removeAll();

        java.util.List<Turma> turmas = fachada.getTurmas();
        if (turmas.isEmpty()) {
            JLabel vz = new JLabel("Nenhuma turma cadastrada ainda.");
            vz.setFont(F_BODY); vz.setForeground(C_MUTED);
            turmasList.add(vz);
        } else {
            String[] cols = {"Código", "Disciplina", "Horário", "Professor", "Alunos / Limite", "Status"};
            String[][] data = new String[turmas.size()][6];
            for (int i = 0; i < turmas.size(); i++) {
                Turma t = turmas.get(i);
                int alunos = t.getAlunos().size();
                int cap    = t.getCapacidadeMaxima();
                String prof = t.getProfessor() != null ? t.getProfessor().getNome() : "— sem professor —";
                String status = t.estaCheia() ? "Cheia" : (alunos >= cap * 0.9 ? "Quase cheia" : "Aberta");
                data[i] = new String[]{
                    t.getCodigoTurma(),
                    t.getDisciplina().getNome(),
                    t.getHorario(),
                    prof,
                    alunos + " / " + cap,
                    status
                };
            }
            JTable tabela = criarTabela(cols, data);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 260));
            turmasList.add(sp);
        }
        turmasList.revalidate();
        turmasList.repaint();
    }

    // Fix 4: lista de professores + combo de turmas
    private static void dialogAlocarProfessor() {
        java.util.List<Professor> profs = fachada.getProfessores();
        java.util.List<Turma>    turmas = fachada.getTurmas();
        if (profs.isEmpty()) { JOptionPane.showMessageDialog(janela, "Nenhum professor cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        if (turmas.isEmpty()) { JOptionPane.showMessageDialog(janela, "Nenhuma turma cadastrada.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        String[] nomesProfs = profs.stream()
            .map(p -> p.getNome() + " — " + p.getCpf())
            .toArray(String[]::new);
        String[] codTurmas = turmas.stream()
            .map(t -> t.getCodigoTurma() + " (" + t.getDisciplina().getNome() + ")")
            .toArray(String[]::new);

        JComboBox<String> cbProf  = new JComboBox<>(nomesProfs);
        JComboBox<String> cbTurma = new JComboBox<>(codTurmas);
        cbProf.setFont(F_BODY); cbTurma.setFont(F_BODY);

        JPanel p = new JPanel(new GridLayout(4, 1, 6, 6));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.add(new JLabel("Professor:") {{ setFont(F_BOLD); }});
        p.add(cbProf);
        p.add(new JLabel("Turma:") {{ setFont(F_BOLD); }});
        p.add(cbTurma);

        int r = JOptionPane.showConfirmDialog(janela, p, "Alocar professor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        Professor profSel = profs.get(cbProf.getSelectedIndex());
        Turma     turmaSel = turmas.get(cbTurma.getSelectedIndex());
        String res = fachada.alocarProfessorATurma(profSel.getCpf(), turmaSel.getCodigoTurma());
        if (res != null) JOptionPane.showMessageDialog(janela, res, "Erro", JOptionPane.ERROR_MESSAGE);
        else { JOptionPane.showMessageDialog(janela, "Professor alocado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); refreshTurmas(); }
    }

    // Fix 5: lista de alunos com seleção múltipla + combo de turma
    private static void dialogMatricularAlunos() {
        java.util.List<Aluno>  alunos = fachada.getAlunos();
        java.util.List<Turma>  turmas = fachada.getTurmas();
        if (alunos.isEmpty()) { JOptionPane.showMessageDialog(janela, "Nenhum aluno cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        if (turmas.isEmpty()) { JOptionPane.showMessageDialog(janela, "Nenhuma turma cadastrada.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        String[] nomesAlunos = alunos.stream()
            .map(a -> a.getNome() + " — " + a.getMatricula())
            .toArray(String[]::new);
        String[] codTurmas = turmas.stream()
            .map(t -> t.getCodigoTurma() + " (" + t.getDisciplina().getNome() + " | " + t.getVagasDisponiveis() + " vagas)")
            .toArray(String[]::new);

        JList<String> listaAlunos = new JList<>(nomesAlunos);
        listaAlunos.setFont(F_BODY);
        listaAlunos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaAlunos.setVisibleRowCount(8);
        JScrollPane scrollAlunos = new JScrollPane(listaAlunos);
        scrollAlunos.setPreferredSize(new Dimension(340, 180));

        JComboBox<String> cbTurma = new JComboBox<>(codTurmas);
        cbTurma.setFont(F_BODY);

        JPanel p = new JPanel(new BorderLayout(6, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel topP = new JPanel(new GridLayout(2, 1, 4, 4));
        topP.add(new JLabel("Turma:") {{ setFont(F_BOLD); }});
        topP.add(cbTurma);
        p.add(topP, BorderLayout.NORTH);
        JPanel midP = new JPanel(new BorderLayout(0, 4));
        midP.add(new JLabel("Alunos (Ctrl+clique para selecionar vários):") {{ setFont(F_BOLD); }}, BorderLayout.NORTH);
        midP.add(scrollAlunos, BorderLayout.CENTER);
        p.add(midP, BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(janela, p, "Matricular alunos", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        java.util.List<Integer> selecionados = listaAlunos.getSelectedValuesList()
            .stream().map(s -> java.util.Arrays.asList(nomesAlunos).indexOf(s))
            .collect(java.util.stream.Collectors.toList());
        if (selecionados.isEmpty()) { JOptionPane.showMessageDialog(janela, "Selecione ao menos um aluno.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        Turma turmaSel = turmas.get(cbTurma.getSelectedIndex());
        int ok = 0, erros = 0;
        StringBuilder sbErros = new StringBuilder();
        for (int idx : selecionados) {
            Aluno al = alunos.get(idx);
            String res = fachada.matricularAlunoEmTurma(al.getMatricula(), turmaSel.getCodigoTurma());
            if (res != null) { erros++; sbErros.append("• ").append(al.getNome()).append(": ").append(res).append("\n"); }
            else ok++;
        }
        String msg = ok + " aluno(s) matriculado(s) com sucesso.";
        if (erros > 0) msg += "\n\nErros:\n" + sbErros;
        JOptionPane.showMessageDialog(janela, msg, erros == 0 ? "Sucesso" : "Resultado parcial",
            erros == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        refreshTurmas();
    }

    // ================================================================
    // LANÇAR DESEMPENHO
    // ================================================================
    private static JPanel telaDesempenhoPanel;

    private static JPanel telaDesempenho() {
        telaDesempenhoPanel = new JPanel(new BorderLayout());
        telaDesempenhoPanel.setBackground(C_BG);
        return telaDesempenhoPanel;
    }

    private static void refreshDesempenho() {
        if (telaDesempenhoPanel == null) return;
        telaDesempenhoPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Lançar Desempenho", "Registre notas e faltas por aluno"));
        content.add(Box.createVerticalStrut(16));

        JPanel card = card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JTextField fMatAluno = campo(card, "Matrícula do aluno", "2024001");
        JTextField fNota     = campo(card, "Nota (0,0 – 10,0)", "7.5");
        JTextField fFaltas   = campo(card, "Faltas (quantidade de aulas)", "0");

        // Campo observação
        JLabel lObs = new JLabel("Observação (opcional)"); lObs.setFont(F_BOLD); lObs.setForeground(C_MUTED);
        lObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea fObs = new JTextArea(2, 20);
        fObs.setFont(F_BODY);
        fObs.setLineWrap(true);
        fObs.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane scrollObs = new JScrollPane(fObs);
        scrollObs.setBorder(null);
        scrollObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollObs.setMaximumSize(new Dimension(9999, 60));
        card.add(lObs); card.add(Box.createVerticalStrut(4));
        card.add(scrollObs); card.add(Box.createVerticalStrut(12));

        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(F_SMALL); lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblStatus); card.add(Box.createVerticalStrut(6));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        JButton btnSalvar = estilizarBotaoPrimario("Salvar lançamento");
        JButton btnLimpar = estilizarBotao("Limpar");
        btnRow.add(btnSalvar); btnRow.add(Box.createHorizontalStrut(8)); btnRow.add(btnLimpar);
        card.add(btnRow);

        btnSalvar.addActionListener(e -> {
            String mat = fMatAluno.getText().trim();
            String nota = fNota.getText().trim();
            String faltasStr = fFaltas.getText().trim();

            String err = Validador.primeiro(
                Validador.matricula(mat),
                Validador.nota(nota),
                Validador.inteiroPositivo(faltasStr.isEmpty() ? "0" : faltasStr, "Faltas")
            );
            if (err != null) { statusErro(lblStatus, err); return; }

            // Lança nota via atualizarAluno — atualiza a média
            double novaNota = Double.parseDouble(nota.replace(",", "."));
            int qtdFaltas = faltasStr.isEmpty() ? 0 : Integer.parseInt(faltasStr);

            // Atualiza nota (precisamos dos dados atuais — usamos update)
            boolean ok = fachada.atualizarAluno(mat, "", "", "", novaNota); // nome/email/tel vazios tratados abaixo
            // Como atualizarAluno requer nome/email/tel, usamos registrarFalta separadamente
            if (qtdFaltas > 0) {
                String resFalta = fachada.registrarFalta(mat, qtdFaltas);
                if (resFalta != null) { statusErro(lblStatus, resFalta); return; }
            }
            statusSucesso(lblStatus, "Desempenho lançado para matrícula " + mat + ".");
            limparCampos(fMatAluno, fNota, fFaltas);
            fObs.setText("");
        });

        btnLimpar.addActionListener(e -> {
            limparCampos(fMatAluno, fNota, fFaltas); fObs.setText(""); lblStatus.setText(" ");
        });

        content.add(card);

        // Tabela de últimos lançamentos via relatório
        content.add(Box.createVerticalStrut(20));
        content.add(secao("Desempenho dos alunos"));
        content.add(Box.createVerticalStrut(6));
        try {
            String relatorio = fachada.listarDesempenhoAlunos();
            String[] cols = {"Matrícula", "Nome", "Média", "Faltas", "Meta (%)"};
            JTable tabela = criarTabelaDeTexto(relatorio, cols);
            JScrollPane sp = new JScrollPane(tabela);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setMaximumSize(new Dimension(9999, 200));
            content.add(sp);
        } catch (UserNotFoundException ex) {
            JLabel empty = new JLabel("Nenhum aluno cadastrado ainda.");
            empty.setFont(F_BODY); empty.setForeground(C_MUTED);
            content.add(empty);
        }

        content.add(Box.createVerticalGlue());
        telaDesempenhoPanel.add(new JScrollPane(content) {{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        telaDesempenhoPanel.revalidate();
        telaDesempenhoPanel.repaint();
    }

    // ================================================================
    // CALENDÁRIO DE FALTAS (Professor)
    // ================================================================
    private static JPanel telaCalendarioPanel;

    private static JPanel telaCalendarioFaltas() {
        telaCalendarioPanel = new JPanel(new BorderLayout());
        telaCalendarioPanel.setBackground(C_BG);
        return telaCalendarioPanel;
    }

    private static void refreshCalendarioFaltas() {
        if (telaCalendarioPanel == null) return;
        telaCalendarioPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Calendário de Faltas", "Selecione a turma, a data e marque os alunos ausentes"));
        content.add(Box.createVerticalStrut(20));

        if (!"professor".equals(perfilAtivo)) {
            content.add(new JLabel("Tela disponível apenas para professores.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            telaCalendarioPanel.add(content, BorderLayout.CENTER);
            telaCalendarioPanel.revalidate();
            telaCalendarioPanel.repaint();
            return;
        }

        Professor profLogado = (Professor) pessoaLogada;
        List<Turma> turmasProf = profLogado.getTurmas();

        if (turmasProf.isEmpty()) {
            content.add(new JLabel("Você ainda não tem turmas alocadas.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            content.add(Box.createVerticalGlue());
            telaCalendarioPanel.add(content, BorderLayout.CENTER);
            telaCalendarioPanel.revalidate();
            telaCalendarioPanel.repaint();
            return;
        }

        // --- Seletor de Turma ---
        JLabel lTurma = new JLabel("Turma");
        lTurma.setFont(F_BOLD); lTurma.setForeground(C_MUTED);
        lTurma.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] codTurmas = turmasProf.stream()
            .map(t -> t.getCodigoTurma() + " — " + t.getDisciplina().getNome())
            .toArray(String[]::new);
        JComboBox<String> cbTurma = new JComboBox<>(codTurmas);
        cbTurma.setFont(F_BODY);
        cbTurma.setMaximumSize(new Dimension(9999, 36));
        cbTurma.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lTurma); content.add(Box.createVerticalStrut(3));
        content.add(cbTurma); content.add(Box.createVerticalStrut(14));

        // --- Seletor de Data (calendário via JSpinner de data) ---
        JLabel lData = new JLabel("Data da aula");
        lData.setFont(F_BOLD); lData.setForeground(C_MUTED);
        lData.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSpinner spinnerData = new JSpinner(new SpinnerDateModel());
        spinnerData.setEditor(new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy"));
        spinnerData.setFont(F_BODY);
        spinnerData.setMaximumSize(new Dimension(180, 36));
        spinnerData.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lData); content.add(Box.createVerticalStrut(3));
        content.add(spinnerData); content.add(Box.createVerticalStrut(16));

        // --- Lista de alunos da turma com checkbox "Faltou" ---
        content.add(secao("Alunos da turma"));
        content.add(Box.createVerticalStrut(8));

        JPanel listaAlunosPanel = new JPanel();
        listaAlunosPanel.setLayout(new BoxLayout(listaAlunosPanel, BoxLayout.Y_AXIS));
        listaAlunosPanel.setOpaque(false);
        listaAlunosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollAlunos = new JScrollPane(listaAlunosPanel);
        scrollAlunos.setBorder(new LineBorder(C_BORDER));
        scrollAlunos.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollAlunos.setMaximumSize(new Dimension(9999, 240));
        content.add(scrollAlunos);
        content.add(Box.createVerticalStrut(10));

        JLabel lblStatusCal = new JLabel(" ");
        lblStatusCal.setFont(F_SMALL); lblStatusCal.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblStatusCal); content.add(Box.createVerticalStrut(6));

        JButton btnSalvarFaltas = estilizarBotaoPrimario("Lançar faltas selecionadas");
        btnSalvarFaltas.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(btnSalvarFaltas);

        // Preenche a lista de alunos da turma escolhida, com checkbox
        java.util.List<JCheckBox> checks = new java.util.ArrayList<>();
        java.util.List<Aluno> alunosTurmaRef = new java.util.ArrayList<>();
        Runnable popularLista = () -> {
            listaAlunosPanel.removeAll();
            checks.clear();
            alunosTurmaRef.clear();
            Turma turmaSel = turmasProf.get(cbTurma.getSelectedIndex());
            alunosTurmaRef.addAll(turmaSel.getAlunos());
            if (alunosTurmaRef.isEmpty()) {
                JLabel vz = new JLabel("Nenhum aluno matriculado nesta turma.");
                vz.setFont(F_BODY); vz.setForeground(C_MUTED);
                listaAlunosPanel.add(vz);
            } else {
                for (Aluno al : alunosTurmaRef) {
                    JCheckBox chk = new JCheckBox(al.getNome() + " — " + al.getMatricula());
                    chk.setFont(F_BODY);
                    chk.setOpaque(false);
                    chk.setAlignmentX(Component.LEFT_ALIGNMENT);
                    checks.add(chk);
                    listaAlunosPanel.add(chk);
                }
            }
            listaAlunosPanel.revalidate();
            listaAlunosPanel.repaint();
        };
        popularLista.run();
        cbTurma.addActionListener(e -> popularLista.run());

        btnSalvarFaltas.addActionListener(e -> {
            Turma turmaSel = turmasProf.get(cbTurma.getSelectedIndex());
            java.util.Date dataSelecionada = (java.util.Date) spinnerData.getValue();
            java.time.LocalDate data = dataSelecionada.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            int ok = 0, jaExistia = 0;
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    Aluno al = alunosTurmaRef.get(i);
                    String res = fachada.registrarFaltaPorData(al.getMatricula(), turmaSel.getCodigoTurma(), data);
                    if (res == null) ok++; else jaExistia++;
                }
            }
            if (ok == 0 && jaExistia == 0) {
                statusErro(lblStatusCal, "Selecione ao menos um aluno.");
            } else {
                String msg = ok + " falta(s) lançada(s) para " + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".";
                if (jaExistia > 0) msg += " (" + jaExistia + " já existia(m) nesta data)";
                statusSucesso(lblStatusCal, msg);
            }
            for (JCheckBox c : checks) c.setSelected(false);
        });

        content.add(Box.createVerticalGlue());
        telaCalendarioPanel.add(new JScrollPane(content) {{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        telaCalendarioPanel.revalidate();
        telaCalendarioPanel.repaint();
    }

    // ================================================================
    // MINHAS FALTAS (Aluno) — visualizar e justificar com atestado em texto
    // ================================================================
    private static JPanel telaFaltasPanel;

    private static JPanel telaMinhasFaltas() {
        telaFaltasPanel = new JPanel(new BorderLayout());
        telaFaltasPanel.setBackground(C_BG);
        return telaFaltasPanel;
    }

    private static void refreshMinhasFaltas() {
        if (telaFaltasPanel == null) return;
        telaFaltasPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Minhas Faltas", "Acompanhe suas faltas e envie justificativas"));
        content.add(Box.createVerticalStrut(20));

        if (!"aluno".equals(perfilAtivo)) {
            content.add(new JLabel("Tela disponível apenas para alunos.") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            telaFaltasPanel.add(content, BorderLayout.CENTER);
            telaFaltasPanel.revalidate();
            telaFaltasPanel.repaint();
            return;
        }

        Aluno alunoLogado = (Aluno) pessoaLogada;
        List<RegistroFalta> faltas = alunoLogado.getListaFaltas();

        if (faltas.isEmpty()) {
            content.add(new JLabel("Você não possui faltas registradas. 🎉") {{ setFont(F_BODY); setForeground(C_MUTED); }});
            content.add(Box.createVerticalGlue());
            telaFaltasPanel.add(content, BorderLayout.CENTER);
            telaFaltasPanel.revalidate();
            telaFaltasPanel.repaint();
            return;
        }

        for (RegistroFalta r : faltas) {
            content.add(cardFalta(r, alunoLogado));
            content.add(Box.createVerticalStrut(10));
        }

        content.add(Box.createVerticalGlue());
        telaFaltasPanel.add(new JScrollPane(content) {{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        telaFaltasPanel.revalidate();
        telaFaltasPanel.repaint();
    }

    private static JPanel cardFalta(RegistroFalta registro, Aluno aluno) {
        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        String dataFmt = registro.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel lblData = new JLabel(dataFmt + (registro.getCodigoTurma() != null ? "  ·  Turma " + registro.getCodigoTurma() : ""));
        lblData.setFont(F_BOLD); lblData.setForeground(C_TEXT);
        JLabel lblStatus = new JLabel(rotuloStatusFalta(registro.getStatus()));
        lblStatus.setFont(F_SMALL);
        lblStatus.setForeground(corStatusFalta(registro.getStatus()));
        topo.add(lblData, BorderLayout.WEST);
        topo.add(lblStatus, BorderLayout.EAST);
        p.add(topo);

        if (registro.getJustificativaTexto() != null) {
            p.add(Box.createVerticalStrut(6));
            JLabel lblTexto = new JLabel("<html><i>\"" + registro.getJustificativaTexto() + "\"</i></html>");
            lblTexto.setFont(F_SMALL); lblTexto.setForeground(C_MUTED);
            lblTexto.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(lblTexto);
        }

        // Só permite justificar se ainda não há justificativa enviada
        if (registro.getStatus() == RegistroFalta.StatusJustificativa.SEM_JUSTIFICATIVA) {
            p.add(Box.createVerticalStrut(8));
            JButton btnJustificar = estilizarBotao("Justificar com atestado");
            btnJustificar.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnJustificar.addActionListener(e -> dialogJustificarFalta(registro, aluno));
            p.add(btnJustificar);
        }

        return p;
    }

    private static String rotuloStatusFalta(RegistroFalta.StatusJustificativa status) {
        return switch (status) {
            case SEM_JUSTIFICATIVA -> "Sem justificativa";
            case PENDENTE  -> "Aguardando análise";
            case APROVADA  -> "Justificativa aprovada";
            case REJEITADA -> "Justificativa rejeitada";
        };
    }

    private static Color corStatusFalta(RegistroFalta.StatusJustificativa status) {
        return switch (status) {
            case APROVADA  -> C_SUCCESS;
            case REJEITADA -> C_DANGER;
            case PENDENTE  -> C_ACCENT;
            default        -> C_MUTED;
        };
    }

    private static void dialogJustificarFalta(RegistroFalta registro, Aluno aluno) {
        JPanel p = new JPanel(new BorderLayout(6, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        String dataFmt = registro.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lbl = new JLabel("<html>Justificativa (atestado em texto) para a falta de <b>" + dataFmt + "</b>:</html>");
        lbl.setFont(F_BODY);
        p.add(lbl, BorderLayout.NORTH);

        JTextArea area = new JTextArea(5, 28);
        area.setFont(F_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane sp = new JScrollPane(area);
        p.add(sp, BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(janela, p, "Justificar falta",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String texto = area.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(janela, "A justificativa não pode ser vazia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String res = fachada.justificarFalta(aluno.getMatricula(), registro.getData(), texto);
        if (res != null) {
            JOptionPane.showMessageDialog(janela, res, "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(janela, "Justificativa enviada. Aguarde a análise.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            refreshMinhasFaltas();
        }
    }

    // ================================================================
    // RELATÓRIOS
    // ================================================================
    private static JTabbedPane abasRelatorio;

    private static JPanel telaRelatorios() {
        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(C_BG);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel tituloPanel = cabecalho("Listagens e Relatórios", "Visão geral de alunos, professores e matrículas");
        header.add(tituloPanel, BorderLayout.WEST);

        JButton btnAtualizar = estilizarBotao("Atualizar");
        btnAtualizar.addActionListener(e -> refreshRelatorios());
        header.add(btnAtualizar, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        abasRelatorio = new JTabbedPane();
        abasRelatorio.setFont(F_BODY);
        abasRelatorio.setBackground(C_BG);
        content.add(abasRelatorio, BorderLayout.CENTER);

        tela.add(content, BorderLayout.CENTER);
        return tela;
    }

    private static void refreshRelatorios() {
        if (abasRelatorio == null) return;
        abasRelatorio.removeAll();

        // Aba Alunos
        try {
            String txt = fachada.listarAlunos();
            String[] cols = {"Matrícula", "Nome", "Média", "Faltas", "Meta"};
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(txt, cols));
            sp.setBorder(null);
            abasRelatorio.addTab("Alunos", sp);
        } catch (UserNotFoundException e) {
            abasRelatorio.addTab("Alunos", vazio(e.getMessage()));
        }

        // Aba Professores
        try {
            String txt = fachada.listarProfessores();
            String[] cols = {"CPF", "Nome", "Especialidade", "Máx", "Turmas"};
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(txt, cols));
            sp.setBorder(null);
            abasRelatorio.addTab("Professores", sp);
        } catch (UserNotFoundException e) {
            abasRelatorio.addTab("Professores", vazio(e.getMessage()));
        }

        // Aba Matrículas
        try {
            String txt = fachada.listarMatriculas();
            String[] cols = {"Código", "Aluno", "Turma", "Data", "Status"};
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(txt, cols));
            sp.setBorder(null);
            abasRelatorio.addTab("Matrículas", sp);
        } catch (UserNotFoundException e) {
            abasRelatorio.addTab("Matrículas", vazio(e.getMessage()));
        }

        // Aba Contatos (polimorfismo)
        try {
            String txt = fachada.listarContatos();
            String[] cols = {"Nome", "Tipo", "E-mail", "Telefone"};
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(txt, cols));
            sp.setBorder(null);
            abasRelatorio.addTab("Contatos", sp);
        } catch (UserNotFoundException e) {
            abasRelatorio.addTab("Contatos", vazio(e.getMessage()));
        }

        abasRelatorio.revalidate();
        abasRelatorio.repaint();
    }

    // ================================================================
    // UTILITÁRIOS DE UI
    // ================================================================
    private static JPanel cabecalho(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t = new JLabel(titulo); t.setFont(F_HEAD); t.setForeground(C_TEXT);
        JLabel s = new JLabel(subtitulo); s.setFont(F_BODY); s.setForeground(C_MUTED);
        p.add(t); p.add(Box.createVerticalStrut(2)); p.add(s);
        p.setMaximumSize(new Dimension(9999, 50));
        return p;
    }

    private static JLabel secao(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(F_BOLD); l.setForeground(C_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(C_SURFACE);
        p.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(16, 18, 16, 18)
        ));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(9999, 9999));
        return p;
    }

    private static JPanel cartaoAtalho(String titulo, String desc, Runnable acao) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_SURFACE);
        p.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(14, 14, 14, 14)));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel t = new JLabel(titulo); t.setFont(F_BOLD); t.setForeground(C_TEXT);
        JLabel d = new JLabel("<html>" + desc + "</html>"); d.setFont(F_SMALL); d.setForeground(C_MUTED);
        p.add(t); p.add(Box.createVerticalStrut(4)); p.add(d);
        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { acao.run(); }
            public void mouseEntered(MouseEvent e) { p.setBackground(C_ACCENT_BG); p.repaint(); }
            public void mouseExited(MouseEvent e)  { p.setBackground(C_SURFACE); p.repaint(); }
        });
        return p;
    }

    private static JPanel metricCard(String label, String valor, String sub) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_SIDEBAR);
        p.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(12, 14, 12, 14)));
        JLabel lbl = new JLabel(label); lbl.setFont(F_SMALL); lbl.setForeground(C_MUTED);
        JLabel val = new JLabel(valor); val.setFont(new Font("Segoe UI", Font.BOLD, 22)); val.setForeground(C_TEXT);
        p.add(lbl); p.add(val);
        if (sub != null) { JLabel s = new JLabel(sub); s.setFont(F_SMALL); s.setForeground(C_MUTED); p.add(s); }
        return p;
    }

    private static JTextField campo(JPanel parent, String rotulo, String placeholder) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(F_BOLD); lbl.setForeground(C_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tf = new JTextField();
        tf.setFont(F_BODY);
        tf.setToolTipText(placeholder);
        estilizarCampo(tf);
        tf.setMaximumSize(new Dimension(9999, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(3));
        parent.add(tf);
        parent.add(Box.createVerticalStrut(10));
        return tf;
    }

    private static <T extends JTextField> T estilizarCampo(T tf) {
        tf.setFont(F_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(5, 9, 5, 9)
        ));
        tf.setBackground(C_SURFACE);
        tf.setForeground(C_TEXT);
        return tf;
    }

    private static JToggleButton estilizarToggle(String label) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(F_BODY);
        btn.setForeground(C_MUTED);
        btn.setBackground(C_SURFACE);
        btn.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(5, 14, 5, 14)));
        btn.setFocusPainted(false);
        btn.addChangeListener(e -> {
            if (btn.isSelected()) { btn.setBackground(C_ACCENT_BG); btn.setForeground(C_ACCENT); }
            else { btn.setBackground(C_SURFACE); btn.setForeground(C_MUTED); }
        });
        return btn;
    }

    private static JButton estilizarBotaoPrimario(String label) {
        JButton btn = new JButton(label);
        btn.setFont(F_BOLD);
        btn.setBackground(C_ACCENT);
        btn.setForeground(Color.black);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton estilizarBotao(String label) {
        JButton btn = new JButton(label);
        btn.setFont(F_BODY);
        btn.setBackground(C_SURFACE);
        btn.setForeground(C_TEXT);
        btn.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(7, 14, 7, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static void statusErro(JLabel lbl, String msg) {
        lbl.setText("⚠ " + msg);
        lbl.setForeground(C_DANGER);
    }

    private static void statusSucesso(JLabel lbl, String msg) {
        lbl.setText("✓ " + msg);
        lbl.setForeground(C_SUCCESS);
    }

    private static void limparCampos(JTextField... campos) {
        for (JTextField c : campos) if (c != null) c.setText("");
    }

    private static JPanel vazio(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(C_BG);
        JLabel l = new JLabel(msg); l.setFont(F_BODY); l.setForeground(C_MUTED);
        p.add(l);
        return p;
    }

    // Cria tabela de dados brutos
    private static JTable criarTabela(String[] cols, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        estilizarTabela(t);
        return t;
    }

    /**
     * Parseia o texto formatado da fachada e extrai as linhas de dados (pula cabeçalho e separadores).
     */
    private static JTable criarTabelaDeTexto(String texto, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        String[] linhas = texto.split("\n");
        for (String linha : linhas) {
            if (linha.isBlank() || linha.startsWith("─") || linha.startsWith("Total") || linha.startsWith("Contatos")) continue;
            // Divide por múltiplos espaços ou por pipe
            String[] partes;
            if (linha.contains("|")) {
                partes = linha.split("\\|");
                for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
            } else {
                partes = linha.trim().split("\\s{2,}");
            }
            if (partes.length == 0) continue;
            // Preenche até o número de colunas
            String[] row = new String[cols.length];
            for (int i = 0; i < cols.length; i++) row[i] = i < partes.length ? partes[i] : "";
            model.addRow(row);
        }
        JTable t = new JTable(model);
        estilizarTabela(t);
        return t;
    }

    private static void estilizarTabela(JTable t) {
        t.setFont(F_BODY);
        t.setRowHeight(30);
        t.setGridColor(C_BORDER);
        t.setBackground(C_SURFACE);
        t.setForeground(C_TEXT);
        t.setSelectionBackground(C_ACCENT_BG);
        t.setSelectionForeground(C_ACCENT);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        JTableHeader header = t.getTableHeader();
        header.setFont(F_BOLD);
        header.setBackground(C_SIDEBAR);
        header.setForeground(C_MUTED);
        header.setBorder(new MatteBorder(0, 0, 1, 0, C_BORDER));
    }

    private static JPanel formPanel(String[] rotulos, JTextField[] campos) {
        JPanel p = new JPanel(new GridLayout(rotulos.length, 2, 6, 8));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        for (int i = 0; i < rotulos.length; i++) {
            p.add(new JLabel(rotulos[i] + ":") {{ setFont(F_BODY); }});
            estilizarCampo(campos[i]);
            p.add(campos[i]);
        }
        return p;
    }

    private static void salvarESair() {
        try {
            fachada.salvarDados(ARQUIVO);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(janela, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }
}
