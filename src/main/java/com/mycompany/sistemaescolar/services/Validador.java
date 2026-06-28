package com.mycompany.sistemaescolar.services;

/**
 * Centraliza todas as regras de validação e verificação de duplicidade do sistema.
 * Cada método retorna null se válido, ou uma mensagem de erro se inválido.
 */
public class Validador {

    // ----------------------------------------------------------------
    // CAMPOS GERAIS
    // ----------------------------------------------------------------

    /** Verifica se um campo obrigatório está vazio */
    public static String campoVazio(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty())
            return "O campo \"" + nomeCampo + "\" não pode estar vazio.";
        return null;
    }

    // ----------------------------------------------------------------
    // CPF
    // ----------------------------------------------------------------

    /**
     * Valida formato de CPF: aceita "999.999.999-99" ou "99999999999" (11 dígitos).
     * Também rejeita CPFs com todos os dígitos iguais (ex: 111.111.111-11).
     */
    public static String cpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return "CPF não pode ser vazio.";

        // Remove pontuação para validar apenas dígitos
        String digits = cpf.replaceAll("[.\\-]", "");

        if (!digits.matches("\\d{11}"))
            return "CPF inválido. Use o formato 999.999.999-99 ou 11 dígitos sem pontuação.";

        // Rejeita sequências triviais (000...0, 111...1, etc.)
        if (digits.chars().distinct().count() == 1)
            return "CPF inválido (sequência repetida).";

        // Dígito verificador 1
        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (digits.charAt(i) - '0') * (10 - i);
        int r1 = (soma * 10) % 11;
        if (r1 == 10 || r1 == 11) r1 = 0;
        if (r1 != (digits.charAt(9) - '0'))
            return "CPF inválido (dígito verificador incorreto).";

        // Dígito verificador 2
        soma = 0;
        for (int i = 0; i < 10; i++) soma += (digits.charAt(i) - '0') * (11 - i);
        int r2 = (soma * 10) % 11;
        if (r2 == 10 || r2 == 11) r2 = 0;
        if (r2 != (digits.charAt(10) - '0'))
            return "CPF inválido (dígito verificador incorreto).";

        return null;
    }

    // ----------------------------------------------------------------
    // E-MAIL
    // ----------------------------------------------------------------

    /** Valida formato básico de e-mail: algo@algo.algo */
    public static String email(String email) {
        if (email == null || email.isBlank()) return "E-mail não pode ser vazio.";
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            return "E-mail inválido. Use o formato nome@dominio.com";
        return null;
    }

    // ----------------------------------------------------------------
    // TELEFONE
    // ----------------------------------------------------------------

    /** Valida telefone brasileiro: (99) 99999-9999 ou (99) 9999-9999 */
    public static String telefone(String tel) {
        if (tel == null || tel.isBlank()) return "Telefone não pode ser vazio.";
        String digits = tel.replaceAll("[()\\s\\-]", "");
        if (!digits.matches("\\d{10,11}"))
            return "Telefone inválido. Use (99) 99999-9999 ou (99) 9999-9999.";
        return null;
    }

    // ----------------------------------------------------------------
    // NOTA
    // ----------------------------------------------------------------

    /** Valida se a string pode ser convertida para double entre 0.0 e 10.0 */
    public static String nota(String valor) {
        if (valor == null || valor.isBlank()) return "Nota não pode ser vazia.";
        try {
            double n = Double.parseDouble(valor.replace(",", "."));
            if (n < 0.0 || n > 10.0)
                return "Nota deve estar entre 0,0 e 10,0.";
        } catch (NumberFormatException e) {
            return "Nota inválida. Digite um número decimal (ex: 7,5).";
        }
        return null;
    }

    // ----------------------------------------------------------------
    // QUANTIDADE (inteiro positivo)
    // ----------------------------------------------------------------

    public static String inteiroPositivo(String valor, String nomeCampo) {
        if (valor == null || valor.isBlank()) return nomeCampo + " não pode ser vazio.";
        try {
            int n = Integer.parseInt(valor);
            if (n <= 0) return nomeCampo + " deve ser maior que zero.";
        } catch (NumberFormatException e) {
            return nomeCampo + " inválido. Digite um número inteiro positivo.";
        }
        return null;
    }

    // ----------------------------------------------------------------
    // CAPACIDADE DE TURMA (limite de alunos)
    // ----------------------------------------------------------------

    /** Capacidade máxima de alunos por turma: inteiro entre 1 e 100. */
    public static String capacidadeTurma(String valor) {
        if (valor == null || valor.isBlank()) return "Capacidade máxima não pode ser vazia.";
        try {
            int n = Integer.parseInt(valor);
            if (n <= 0) return "Capacidade máxima deve ser maior que zero.";
            if (n > 100) return "Capacidade máxima não pode ser maior que 100 alunos.";
        } catch (NumberFormatException e) {
            return "Capacidade máxima inválida. Digite um número inteiro (ex: 30).";
        }
        return null;
    }

    // ----------------------------------------------------------------
    // NOME
    // ----------------------------------------------------------------

    /** Nome deve ter ao menos 3 caracteres e apenas letras/espaços */
    public static String nome(String nome) {
        if (nome == null || nome.isBlank()) return "Nome não pode ser vazio.";
        if (nome.trim().length() < 3) return "Nome deve ter ao menos 3 caracteres.";
        if (!nome.matches("[\\p{L} .'-]+"))
            return "Nome inválido. Use apenas letras e espaços.";
        return null;
    }

    // ----------------------------------------------------------------
    // CÓDIGO DE TURMA
    // ----------------------------------------------------------------

    /** Código de turma: letras, números e hífen, mínimo 3 caracteres */
    public static String codigoTurma(String codigo) {
        if (codigo == null || codigo.isBlank()) return "Código da turma não pode ser vazio.";
        if (!codigo.matches("[A-Za-z0-9\\-]{3,15}"))
            return "Código inválido. Use letras, números e hífen (ex: 7ANO-B). Entre 3 e 15 caracteres.";
        return null;
    }

    // ----------------------------------------------------------------
    // MATRÍCULA DE ALUNO
    // ----------------------------------------------------------------

    /** Matrícula: letras e números, 4 a 15 caracteres */
    public static String matricula(String mat) {
        if (mat == null || mat.isBlank()) return "Matrícula não pode ser vazia.";
        if (!mat.matches("[A-Za-z0-9]{4,15}"))
            return "Matrícula inválida. Use apenas letras e números (4 a 15 caracteres).";
        return null;
    }

    // ----------------------------------------------------------------
    // HELPER: acumula erros e retorna null se tudo ok
    // ----------------------------------------------------------------

    /**
     * Recebe vários resultados de validação e retorna a primeira mensagem de erro
     * encontrada, ou null se todos forem válidos.
     */
    public static String primeiro(String... resultados) {
        for (String r : resultados) {
            if (r != null) return r;
        }
        return null;
    }
}
