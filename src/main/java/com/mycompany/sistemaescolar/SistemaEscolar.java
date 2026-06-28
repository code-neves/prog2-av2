package com.mycompany.sistemaescolar;

import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.Screens.components.TelaLogin;

import javax.swing.*;

public class SistemaEscolar {

    private static final String ARQUIVO = "banco_dados_escolar.dat";

    public static void main(String[] args) {
        FachadaEscolar fachada = new FachadaEscolar();
        try { fachada.carregarDados(ARQUIVO); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            new TelaLogin(fachada).setVisible(true);
        });
    }
}