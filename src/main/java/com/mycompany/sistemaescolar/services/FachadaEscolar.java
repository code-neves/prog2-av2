package com.mycompany.sistemaescolar.services;

import com.mycompany.sistemaescolar.models.Aluno;
import com.mycompany.sistemaescolar.models.Professor;
import com.mycompany.sistemaescolar.models.Disciplina;
import com.mycompany.sistemaescolar.models.Turma;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;

import java.io.*;
import java.util.ArrayList;

public class FachadaEscolar {
    private ArrayList<Professor> listProfessores;
    private ArrayList<Aluno> listAlunos;
    private ArrayList<Disciplina> listDisciplinas;
    private ArrayList<Turma> listTurmas;
    
    public FachadaEscolar() {
        this.listProfessores = new ArrayList<>();
        this.listAlunos = new ArrayList<>();
        this.listDisciplinas = new ArrayList<>();
        this.listTurmas = new ArrayList<>();
        inicializarDisciplinasBNCC();
    }

    private void inicializarDisciplinasBNCC() {
        listDisciplinas.add(new Disciplina("Língua Portuguesa", "Linguagens"));
        listDisciplinas.add(new Disciplina("Matemática", "Matemática"));
        listDisciplinas.add(new Disciplina("Ciências", "Ciências da Natureza"));
        listDisciplinas.add(new Disciplina("História", "Ciências Humanas"));
        listDisciplinas.add(new Disciplina("Geografia", "Ciências Humanas"));
        listDisciplinas.add(new Disciplina("Arte", "Linguagens"));
        listDisciplinas.add(new Disciplina("Educação Física", "Linguagens"));
        listDisciplinas.add(new Disciplina("Língua Inglesa", "Linguagens"));
    }

    public void cadastrarAluno(String nome, String cpf, String email, 
            String telefone, String matricula, double notaMedia) {
        Aluno aluno = new Aluno(nome, cpf, email, telefone, matricula, notaMedia);
        listAlunos.add(aluno);
    }

    public void cadastrarProfessor(String nome, String cpf, String email, 
            String telefone, String especialidade, int qtdTurmas) {
        Professor professor = new Professor(nome, cpf, email, telefone, especialidade, qtdTurmas);
        listProfessores.add(professor);
    }

    public boolean atualizarAluno(String matricula, String novoNome, String novoEmail, String novoTelefone, double novaNota) {
        for (Aluno aluno : listAlunos) {
            if (aluno.getMatricula().equals(matricula)) {
                aluno.setNome(novoNome);
                aluno.setEmail(novoEmail);
                aluno.setTelefone(novoTelefone);
                aluno.setNotaMedia(novaNota);
                return true;
            }
        }
        return false;
    }

    public boolean criarTurma(String codigoTurma, String nomeDisciplina, String horario) {
        Disciplina disciplinaSelecionada = null;
        for (Disciplina d : listDisciplinas) {
            if (d.getNome().equalsIgnoreCase(nomeDisciplina)) {
                disciplinaSelecionada = d;
                break;
            }
        }
        if (disciplinaSelecionada == null) {
            return false; 
        }
        // Instancia a turma passando exatamente os 3 parâmetros correspondentes
        Turma novaTurma = new Turma(codigoTurma, disciplinaSelecionada, horario);
        listTurmas.add(novaTurma);
        return true;
    }

    public boolean alocarProfessorATurma(String cpfProfessor, String codigoTurma) {
        Professor profAlvo = null;
        for (Professor p : listProfessores) {
            if (p.getCpf().equals(cpfProfessor)) {
                profAlvo = p;
                break;
            }
        }
        if (profAlvo == null) return false;

        for (Turma t : listTurmas) {
            if (t.getCodigoTurma().equals(codigoTurma)) {
                t.setProfessor(profAlvo);
                return true;
            }
        }
        return false;
    }

    public boolean matricularAlunoEmTurma(String matriculaAluno, String codigoTurma) {
        Aluno alunoAlvo = null;
        for (Aluno a : listAlunos) {
            if (a.getMatricula().equals(matriculaAluno)) {
                alunoAlvo = a;
                break;
            }
        }
        if (alunoAlvo == null) return false;

        for (Turma t : listTurmas) {
            if (t.getCodigoTurma().equals(codigoTurma)) {
                return t.adicionarAluno(alunoAlvo);
            }
        }
        return false;
    }

    public String listarAlunos() throws UserNotFoundException {
        if (listAlunos.isEmpty()) {
            throw new UserNotFoundException("Nenhum aluno cadastrado no sistema.");
        }
        
        StringBuilder tabelaDados = new StringBuilder("Matricula\tNome\t\tNota Media\tMeta Atingida\n");
        tabelaDados.append("----------------------------------------------------------------------\n");
        for (Aluno aluno : listAlunos) {
            tabelaDados.append(aluno.getMatricula()).append("\t")
                       .append(aluno.getNome()).append("\t\t")
                       .append(aluno.getNotaMedia()).append("\t\t")
                       .append(String.format("%.1f", aluno.calcularMeta())).append("%\n");
        }
        return tabelaDados.toString();
    }

    public String listarProfessores() throws UserNotFoundException {
        if (listProfessores.isEmpty()) {
            throw new UserNotFoundException("Nenhum professor cadastrado no sistema.");
        }
        
        StringBuilder tabelaDados = new StringBuilder("CPF\t\tNome\t\tEspecialidade\n");
        tabelaDados.append("--------------------------------------------------\n");
        for (Professor professor : listProfessores) {
            tabelaDados.append(professor.getCpf()).append("\t")
                       .append(professor.getNome()).append("\t\t")
                       .append(professor.getEspecialidade()).append("\n");
        }
        return tabelaDados.toString();
    }

    public String listarDisciplinasBNCC() {
        StringBuilder lista = new StringBuilder("Componentes Curriculares Disponíveis (BNCC):\n");
        for (Disciplina d : listDisciplinas) {
            lista.append("- ").append(d.getNome()).append(" [Área: ").append(d.getAreaConhecimento()).append("]\n");
        }
        return lista.toString();
    }

    @SuppressWarnings("unchecked")
    public void carregarDados(String nomeArquivo) throws IOException, ClassNotFoundException {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) return;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            this.listAlunos = (ArrayList<Aluno>) ois.readObject();
            this.listProfessores = (ArrayList<Professor>) ois.readObject();
            this.listTurmas = (ArrayList<Turma>) ois.readObject();
        }
    }

    public void salvarDados(String nomeArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(this.listAlunos);
            oos.writeObject(this.listProfessores);
            oos.writeObject(this.listTurmas);
        }
    }
}