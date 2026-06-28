package com.mycompany.sistemaescolar.Screens.secretaria;

import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.function.Consumer;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class DashboardSecretaria extends JPanel {

    private final FachadaEscolar   fachada;
    private final Consumer<String> navegarPara;

    public DashboardSecretaria(FachadaEscolar fachada, Consumer<String> navegarPara) {
        this.fachada     = fachada;
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

        content.add(cabecalho("Painel de Coordenação", "Visão geral e atalhos rápidos"));
        content.add(Box.createVerticalStrut(16));

        // Métricas reais
        String qtdAlunos = "0", qtdProfs = "0", qtdTurmas = "0", qtdMats = "0";
        try { qtdAlunos = String.valueOf(fachada.listarAlunos().split("\n").length - 2); } catch (Exception ignored) {}
        try { qtdProfs  = String.valueOf(fachada.listarProfessores().split("\n").length - 2); } catch (Exception ignored) {}
        try { qtdMats   = String.valueOf(fachada.listarMatriculas().split("\n").length - 2); } catch (Exception ignored) {}
        try {
            java.util.Set<String> set = new java.util.HashSet<>();
            for (String ln : fachada.listarMatriculas().split("\n")) {
                if (ln.isBlank() || ln.startsWith("─") || ln.startsWith("Código")) continue;
                String[] p = ln.trim().split("\\s{2,}");
                if (p.length >= 3) set.add(p[2].trim());
            }
            if (!set.isEmpty()) qtdTurmas = String.valueOf(set.size());
        } catch (Exception ignored) {}

        JPanel metricas = new JPanel(new GridLayout(1, 4, 10, 0));
        metricas.setOpaque(false);
        metricas.add(metricCard("Alunos",     qtdAlunos, null));
        metricas.add(metricCard("Professores",qtdProfs,  null));
        metricas.add(metricCard("Turmas",     qtdTurmas, null));
        metricas.add(metricCard("Matrículas", qtdMats,   null));
        metricas.setMaximumSize(new Dimension(9999, 90));
        content.add(metricas);
        content.add(Box.createVerticalStrut(20));

        content.add(secao("Atalhos rápidos"));
        content.add(Box.createVerticalStrut(8));
        JPanel atalhos = new JPanel(new GridLayout(1, 3, 10, 0));
        atalhos.setOpaque(false);
        atalhos.add(cartaoAtalho("Cadastrar pessoa",    "Adicione alunos e professores", () -> navegarPara.accept("cadastro")));
        atalhos.add(cartaoAtalho("Gerenciar turmas",    "Crie turmas, aloque professores, matricule alunos", () -> navegarPara.accept("turmas")));
        atalhos.add(cartaoAtalho("Relatórios",          "Veja listagens gerais do sistema", () -> navegarPara.accept("relatorios")));
        atalhos.setMaximumSize(new Dimension(9999, 120));
        content.add(atalhos);
        content.add(Box.createVerticalGlue());

        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}