/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemaescolar;
import com.mycompany.sistemaescolar.services.FachadaEscolar;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.io.IOException;


/**
 *
 * @author rafae
 */

public class SistemaEscolar {
    
    private static final String NOME_ARQUIVO = "banco_dados_escolar.dat";
    
    public static void main(String[] args) {
        FachadaEscolar fachada = new FachadaEscolar();
        
        //Carregando arquivo
        try {
            fachada.carregarDados(NOME_ARQUIVO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Novo banco de dados local inicializado.");
        }
        
        boolean executando = true;

        while (executando) {
            
            String menu = "Menu do Portal Educacional\n" +
                          "1 - Cadastrar Aluno\n" +
                          "2 - Cadastrar Professor\n" +
                          "3 - Listar Alunos (Ver Metas)\n" +
                          "4 - Listar Professores\n" +
                          "5 - Atualizar Dados de Aluno\n" +
                          "6 - Criar Turma BNCC\n" +
                          "7 - Alocar Professor a Turma\n" +
                          "8 - Matricular Aluno em Turma\n" +
                          "9 - Salvar e Sair";
            
            String opcaoTexto = JOptionPane.showInputDialog(menu);

            if (opcaoTexto == null || opcaoTexto.equals("9")) {
                try {
                    fachada.salvarDados(NOME_ARQUIVO);
                    JOptionPane.showMessageDialog(null, "Sessão salva com sucesso. Fechando aplicação.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Falha crítica ao persistir dados: " + e.getMessage());
                }
                executando = false;
                continue;
            }

            try {
                int opcao = Integer.parseInt(opcaoTexto);

                if (opcao == 1) {
                    String nome = JOptionPane.showInputDialog("Digite o nome do Aluno:");
                    String cpf = JOptionPane.showInputDialog("Digite a cpf:");
                    String email = JOptionPane.showInputDialog("Digite a email:");
                    String telefone = JOptionPane.showInputDialog("Digite a telefone:");
                    String matricula = JOptionPane.showInputDialog("Digite a matricula:");
                    String notaTexto = JOptionPane.showInputDialog("Digite a nota media:");
                    double notaMedia = Double.parseDouble(notaTexto);
                    
                    fachada.cadastrarAluno(nome, cpf, email, telefone, matricula, notaMedia);
                    JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso.");
                    
                } else if (opcao == 2) {
                    String nome = JOptionPane.showInputDialog("Digite o nome do Professor:");
                    String cpf = JOptionPane.showInputDialog("Digite a cpf:");
                    String email = JOptionPane.showInputDialog("Digite a email:");
                    String telefone = JOptionPane.showInputDialog("Digite a telefone:");
                    String especialidade = JOptionPane.showInputDialog("Digite a matricula:");
                    String turmasTexto = JOptionPane.showInputDialog("Digite a quantidade de turmas:");
                    int qtdTurmas = Integer.parseInt(turmasTexto);
                    
                    fachada.cadastrarProfessor(nome, cpf, email, telefone, especialidade, qtdTurmas);
                    JOptionPane.showMessageDialog(null, "Professor cadastrado com sucesso.");
                    
                } else if (opcao == 3 || opcao == 4) {
                    String dadosTabela = (opcao == 3) ? fachada.listarAlunos() : fachada.listarProfessores();

                    JTextArea areaTexto = new JTextArea(dadosTabela);
                    areaTexto.setEditable(false);
                    JScrollPane painelRolagem = new JScrollPane(areaTexto);
                    painelRolagem.setPreferredSize(new Dimension(550, 300));

                    JOptionPane.showMessageDialog(null, painelRolagem, "Relatório Geral", JOptionPane.PLAIN_MESSAGE);
                    
                } else if (opcao == 5) {
                    String matricula = JOptionPane.showInputDialog("Matrícula do Aluno a alterar:");
                    String nome = JOptionPane.showInputDialog("Novo Nome:");
                    String email = JOptionPane.showInputDialog("Novo E-mail:");
                    String telefone = JOptionPane.showInputDialog("Novo Telefone:");
                    String notaTexto = JOptionPane.showInputDialog("Nova Nota Média:");
                    double notaMedia = Double.parseDouble(notaTexto);
                    
                    if (fachada.atualizarAluno(matricula, nome, email, telefone, notaMedia)) {
                        JOptionPane.showMessageDialog(null, "Dados cadastrais modificados com sucesso.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Matrícula não localizada no acervo.");
                    }
                    
                } else if (opcao == 6) {
                    String BNCC_Menu = fachada.listarDisciplinasBNCC();
                    String nomeDisciplina = JOptionPane.showInputDialog(BNCC_Menu + "\nEscreva o nome idêntico da disciplina para a turma:");
                    String codigoTurma = JOptionPane.showInputDialog("Código identificador da Turma (Ex: 7ANO-B):");
                    String horario = JOptionPane.showInputDialog("Horário das aulas (Ex: 08:00 às 09:40):");
                    
                    if (fachada.criarTurma(codigoTurma, nomeDisciplina, horario)) {
                        JOptionPane.showMessageDialog(null, "Turma aberta sob as diretrizes da BNCC.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Componente curricular inválido para as regras nacionais.");
                    }
                    
                } else if (opcao == 7) {
                    String cpfProfessor = JOptionPane.showInputDialog("CPF do Professor:");
                    String codigoTurma = JOptionPane.showInputDialog("Código da Turma:");
                    
                    if (fachada.alocarProfessorATurma(cpfProfessor, codigoTurma)) {
                        JOptionPane.showMessageDialog(null, "Professor vinculado à regência desta turma.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Verifique se o CPF do Professor ou o Código da Turma estão corretos.");
                    }
                    
                } else if (opcao == 8) {
                    String matriculaAluno = JOptionPane.showInputDialog("Matrícula do Aluno:");
                    String codigoTurma = JOptionPane.showInputDialog("Código da Turma:");
                    
                    if (fachada.matricularAlunoEmTurma(matriculaAluno, codigoTurma)) {
                        JOptionPane.showMessageDialog(null, "Inclusão do aluno na turma efetuada.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Não foi possível matricular. Verifique os dados fornecidos.");
                    }
                }
            } catch (NumberFormatException excecaoFormato) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Digite valores numéricos coerentes.");
            } catch (UserNotFoundException excecaoUsuario) {
                JOptionPane.showMessageDialog(null, excecaoUsuario.getMessage());
            }
        }
    }
}