package com.mycompany.sistemaescolar.Screens.aluno;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.services.FachadaEscolar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;
import static com.mycompany.sistemaescolar.Screens.components.Components.*;

public class TelaMinhasFaltas extends JPanel {

    private final FachadaEscolar fachada;
    private final Aluno          aluno;

    public TelaMinhasFaltas(FachadaEscolar fachada, Pessoa pessoa) {
        this.fachada = fachada;
        this.aluno   = (Aluno) pessoa;
        setLayout(new BorderLayout());
        setBackground(C_BG);
    }

    public void refresh() {
        removeAll();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 28, 28, 28));

        content.add(cabecalho("Minhas Faltas", "Acompanhe suas faltas e envie justificativas"));
        content.add(Box.createVerticalStrut(20));

        List<RegistroFalta> faltas = aluno.getListaFaltas();
        if (faltas.isEmpty()) {
            JLabel ok = new JLabel("Você não possui faltas registradas. 🎉");
            ok.setFont(F_BODY); ok.setForeground(C_MUTED);
            content.add(ok);
        } else {
            for (RegistroFalta r : faltas) {
                content.add(cardFalta(r));
                content.add(Box.createVerticalStrut(10));
            }
        }

        content.add(Box.createVerticalGlue());
        add(scrollSemBorda(content), BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private JPanel cardFalta(RegistroFalta registro) {
        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        String dataFmt = registro.getData()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel lblData = new JLabel(dataFmt +
            (registro.getCodigoTurma() != null ? "  ·  Turma " + registro.getCodigoTurma() : ""));
        lblData.setFont(F_BOLD); lblData.setForeground(C_TEXT);
        JLabel lblStatus = new JLabel(rotuloStatus(registro.getStatus()));
        lblStatus.setFont(F_SMALL);
        lblStatus.setForeground(corStatus(registro.getStatus()));
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

        if (registro.getStatus() == RegistroFalta.StatusJustificativa.SEM_JUSTIFICATIVA) {
            p.add(Box.createVerticalStrut(8));
            JButton btn = botao("Justificar com atestado");
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> dialogJustificar(registro));
            p.add(btn);
        }
        return p;
    }

    private void dialogJustificar(RegistroFalta registro) {
        String dataFmt = registro.getData()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        JPanel p = new JPanel(new BorderLayout(6, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.add(new JLabel("<html>Justificativa para a falta de <b>" + dataFmt + "</b>:</html>") {{
            setFont(F_BODY);
        }}, BorderLayout.NORTH);

        JTextArea area = new JTextArea(5, 28);
        area.setFont(F_BODY); area.setLineWrap(true); area.setWrapStyleWord(true);
        area.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(6, 8, 6, 8)));
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(this, p, "Justificar falta",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String texto = area.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A justificativa não pode ser vazia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String res = fachada.justificarFalta(aluno.getMatricula(), registro.getData(), texto);
        if (res != null) JOptionPane.showMessageDialog(this, res, "Erro", JOptionPane.ERROR_MESSAGE);
        else {
            JOptionPane.showMessageDialog(this, "Justificativa enviada. Aguarde a análise.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }

    private String rotuloStatus(RegistroFalta.StatusJustificativa s) {
        return switch (s) {
            case SEM_JUSTIFICATIVA -> "Sem justificativa";
            case PENDENTE  -> "Aguardando análise";
            case APROVADA  -> "Justificativa aprovada";
            case REJEITADA -> "Justificativa rejeitada";
        };
    }

    private Color corStatus(RegistroFalta.StatusJustificativa s) {
        return switch (s) {
            case APROVADA  -> C_SUCCESS;
            case REJEITADA -> C_DANGER;
            case PENDENTE  -> C_ACCENT;
            default        -> C_MUTED;
        };
    }
}