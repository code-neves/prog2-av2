package com.mycompany.sistemaescolar.Screens.components;

import java.awt.*;

/**
 * Paleta de cores e fontes globais do sistema.
 * Importe esta classe em todas as telas para manter consistência visual.
 */
public class Colors {

    private Colors() {}

    // ----------------------------------------------------------------
    // Cores
    // ----------------------------------------------------------------
    public static final Color C_BG         = new Color(0xF5F4F0);
    public static final Color C_SURFACE    = new Color(0xFFFFFF);
    public static final Color C_SIDEBAR    = new Color(0xF0EEE8);
    public static final Color C_BORDER     = new Color(0xDDDAD2);
    public static final Color C_ACCENT     = new Color(0x1A6FD4);
    public static final Color C_ACCENT_BG  = new Color(0xE6F0FB);
    public static final Color C_TEXT       = new Color(0x1A1A18);
    public static final Color C_MUTED      = new Color(0x888780);
    public static final Color C_SUCCESS    = new Color(0x0F6E56);
    public static final Color C_DANGER     = new Color(0xA32D2D);

    // ----------------------------------------------------------------
    // Fontes
    // ----------------------------------------------------------------
    public static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_HEAD  = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
}