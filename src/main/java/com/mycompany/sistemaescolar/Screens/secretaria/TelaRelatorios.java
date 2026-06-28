package com.mycompany.sistemaescolar.Screens.secretaria;

import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;
import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaRelatorios extends JPanel {

    private final FachadaEscolar fachada;
    private JTabbedPane abas;

    public TelaRelatorios(FachadaEscolar fachada) {
        this.fachada = fachada;
        setLayout(new BorderLayout());
        setBackground(C_BG);
        construir();
    }

    private void construir() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(cabecalho("Listagens e Relatórios", "Visão geral de alunos, professores e matrículas"), BorderLayout.WEST);
        JButton btnAtualizar = botao("Atualizar");
        btnAtualizar.addActionListener(e -> refresh());
        header.add(btnAtualizar, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        abas = new JTabbedPane();
        abas.setFont(F_BODY);
        abas.setBackground(C_BG);
        content.add(abas, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    public void refresh() {
        if (abas == null) return;
        abas.removeAll();

        try {
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(fachada.listarAlunos(),
                new String[]{"Matrícula","Nome","Média","Faltas","Meta"}));
            sp.setBorder(null); abas.addTab("Alunos", sp);
        } catch (UserNotFoundException e) { abas.addTab("Alunos", vazio(e.getMessage())); }

        try {
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(fachada.listarProfessores(),
                new String[]{"CPF","Nome","Especialidade","Máx","Turmas"}));
            sp.setBorder(null); abas.addTab("Professores", sp);
        } catch (UserNotFoundException e) { abas.addTab("Professores", vazio(e.getMessage())); }

        try {
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(fachada.listarMatriculas(),
                new String[]{"Código","Aluno","Turma","Data","Status"}));
            sp.setBorder(null); abas.addTab("Matrículas", sp);
        } catch (UserNotFoundException e) { abas.addTab("Matrículas", vazio(e.getMessage())); }

        try {
            JScrollPane sp = new JScrollPane(criarTabelaDeTexto(fachada.listarTodasPessoas(),
                new String[]{"Nome","Tipo","E-mail","Telefone"}));
            sp.setBorder(null); abas.addTab("Todas as Pessoas", sp);
        } catch (UserNotFoundException e) { abas.addTab("Todas as Pessoas", vazio(e.getMessage())); }

        abas.revalidate(); abas.repaint();
    }
}