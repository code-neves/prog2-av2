package com.mycompany.sistemaescolar.Screens.components;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import static com.mycompany.sistemaescolar.Screens.components.Colors.*;

/**
 * Fábrica de componentes visuais reutilizáveis.
 * Todas as telas usam estes métodos para manter consistência.
 */
public class Components {

    private Components() {}

    // ----------------------------------------------------------------
    // Botões
    // ----------------------------------------------------------------
    public static JButton botaoPrimario(String label) {
        JButton btn = new JButton(label);
        btn.setFont(F_BOLD);
        btn.setBackground(C_ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton botao(String label) {
        JButton btn = new JButton(label);
        btn.setFont(F_BODY);
        btn.setBackground(C_SURFACE);
        btn.setForeground(C_TEXT);
        btn.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(7, 14, 7, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JToggleButton toggle(String label) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(F_BODY);
        btn.setForeground(C_MUTED);
        btn.setBackground(C_SURFACE);
        btn.setBorder(new CompoundBorder(new LineBorder(C_BORDER), new EmptyBorder(5, 14, 5, 14)));
        btn.setFocusPainted(false);
        btn.addChangeListener(e -> {
            if (btn.isSelected()) { btn.setBackground(C_ACCENT_BG); btn.setForeground(C_ACCENT); }
            else                  { btn.setBackground(C_SURFACE);   btn.setForeground(C_MUTED);  }
        });
        return btn;
    }

    // ----------------------------------------------------------------
    // Campos de texto
    // ----------------------------------------------------------------
    public static <T extends JTextField> T estilizarCampo(T tf) {
        tf.setFont(F_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(C_BORDER, 1), new EmptyBorder(5, 9, 5, 9)));
        tf.setBackground(C_SURFACE);
        tf.setForeground(C_TEXT);
        return tf;
    }

    /** Adiciona rótulo + campo a um painel BoxLayout e retorna o JTextField. */
    public static JTextField campo(JPanel parent, String rotulo, String placeholder) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(F_BOLD); lbl.setForeground(C_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tf = new JTextField();
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

    // ----------------------------------------------------------------
    // Painéis estruturais
    // ----------------------------------------------------------------
    public static JPanel cabecalho(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t = new JLabel(titulo);     t.setFont(F_HEAD);  t.setForeground(C_TEXT);
        JLabel s = new JLabel(subtitulo);  s.setFont(F_BODY);  s.setForeground(C_MUTED);
        p.add(t); p.add(Box.createVerticalStrut(2)); p.add(s);
        p.setMaximumSize(new Dimension(9999, 50));
        return p;
    }

    public static JLabel secao(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(F_BOLD); l.setForeground(C_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(C_SURFACE);
        p.setBorder(new CompoundBorder(new LineBorder(C_BORDER, 1), new EmptyBorder(16, 18, 16, 18)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(9999, 9999));
        return p;
    }

    public static JPanel metricCard(String label, String valor, String sub) {
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

    public static JPanel cartaoAtalho(String titulo, String desc, Runnable acao) {
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
            public void mouseExited(MouseEvent e)  { p.setBackground(C_SURFACE);   p.repaint(); }
        });
        return p;
    }

    // ----------------------------------------------------------------
    // Feedback de status
    // ----------------------------------------------------------------
    public static void statusErro(JLabel lbl, String msg) {
        lbl.setText("⚠ " + msg);
        lbl.setForeground(C_DANGER);
    }

    public static void statusSucesso(JLabel lbl, String msg) {
        lbl.setText("✓ " + msg);
        lbl.setForeground(C_SUCCESS);
    }

    public static void limparCampos(JTextField... campos) {
        for (JTextField c : campos) if (c != null) c.setText("");
    }

    // ----------------------------------------------------------------
    // Tabelas
    // ----------------------------------------------------------------
    public static JTable criarTabela(String[] cols, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        estilizarTabela(t);
        return t;
    }

    public static JTable criarTabelaDeTexto(String texto, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String linha : texto.split("\n")) {
            if (linha.isBlank() || linha.startsWith("─") || linha.startsWith("Total") || linha.startsWith("Contatos")) continue;
            String[] partes = linha.contains("|")
                ? java.util.Arrays.stream(linha.split("\\|")).map(String::trim).toArray(String[]::new)
                : linha.trim().split("\\s{2,}");
            if (partes.length == 0) continue;
            String[] row = new String[cols.length];
            for (int i = 0; i < cols.length; i++) row[i] = i < partes.length ? partes[i] : "";
            model.addRow(row);
        }
        JTable t = new JTable(model);
        estilizarTabela(t);
        return t;
    }

    public static void estilizarTabela(JTable t) {
        t.setFont(F_BODY);
        t.setRowHeight(30);
        t.setGridColor(C_BORDER);
        t.setBackground(C_SURFACE);
        t.setForeground(C_TEXT);
        t.setSelectionBackground(C_ACCENT_BG);
        t.setSelectionForeground(C_ACCENT);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(F_BOLD);
        h.setBackground(C_SIDEBAR);
        h.setForeground(C_MUTED);
        h.setBorder(new MatteBorder(0, 0, 1, 0, C_BORDER));
    }

    public static JPanel vazio(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(C_BG);
        JLabel l = new JLabel(msg); l.setFont(F_BODY); l.setForeground(C_MUTED);
        p.add(l);
        return p;
    }

    public static JScrollPane scrollSemBorda(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.getViewport().setBackground(C_BG);
        return sp;
    }
}