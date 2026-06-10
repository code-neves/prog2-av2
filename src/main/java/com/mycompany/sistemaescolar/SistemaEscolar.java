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


/**
 *
 * @author rafae
 */

public class SistemaEscolar {
    public static void main(String[] args) {
        FachadaEscolar fachada = new FachadaEscolar();
        boolean executando = true;

        while (executando) {
            String menu = "Menu do Portal Educacional\n1 - Cadastrar Aluno\n2 - Cadastrar Professor\n3 - Listar Alunos\n4 - Listar Professores\n5 - Sair";
            String opcaoTexto = JOptionPane.showInputDialog(menu);

            if (opcaoTexto == null || opcaoTexto.equals("5")) {
                executando = false;
                continue;
            }

            try {
                int opcao = Integer.parseInt(opcaoTexto);

                if (opcao == 1) {
                    String nome = JOptionPane.showInputDialog("Digite o nome do Aluno:");
                    String matricula = JOptionPane.showInputDialog("Digite a matricula:");
                    String notaTexto = JOptionPane.showInputDialog("Digite a nota media:");
                    double notaMedia = Double.parseDouble(notaTexto);
                    fachada.cadastrarAluno(nome, matricula, notaMedia);
                    JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso.");
                    
                } else if (opcao == 2) {
                    String nome = JOptionPane.showInputDialog("Digite o nome do Professor:");
                    String matricula = JOptionPane.showInputDialog("Digite a matricula:");
                    String turmasTexto = JOptionPane.showInputDialog("Digite a quantidade de turmas:");
                    int quantidadeTurmas = Integer.parseInt(turmasTexto);
                    fachada.cadastrarProfessor(nome, matricula, quantidadeTurmas);
                    JOptionPane.showMessageDialog(null, "Professor cadastrado com sucesso.");
                    
                } else if (opcao == 3 || opcao == 4) {
                    String dadosTabela = "";
                    if (opcao == 3) {
                        dadosTabela = fachada.listarAlunos();
                    } else {
                        dadosTabela = fachada.listarProfessores();
                    }

                    JTextArea areaTexto = new JTextArea(dadosTabela);
                    areaTexto.setEditable(false);
                    JScrollPane painelRolagem = new JScrollPane(areaTexto);
                    painelRolagem.setPreferredSize(new Dimension(400, 200));

                    JOptionPane.showMessageDialog(null, painelRolagem, "Tabela de Registros", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (NumberFormatException excecaoFormato) {
                JOptionPane.showMessageDialog(null, "Erro: Insira um valor numerico valido.");
            } catch (UserNotFoundException excecaoUsuario) {
                JOptionPane.showMessageDialog(null, excecaoUsuario.getMessage());
            }
        }
    }
}