package com.mycompany.sistemaescolar.services;

import com.mycompany.sistemaescolar.models.*;
import com.mycompany.sistemaescolar.exceptions.UserNotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FachadaEscolar {

    private ArrayList<Professor>  listProfessores;
    private ArrayList<Aluno>      listAlunos;
    private ArrayList<Disciplina> listDisciplinas;
    private ArrayList<Turma>      listTurmas;
    private ArrayList<Matricula>  listMatriculas;

    public FachadaEscolar() {
        this.listProfessores = new ArrayList<>();
        this.listAlunos      = new ArrayList<>();
        this.listDisciplinas = new ArrayList<>();
        this.listTurmas      = new ArrayList<>();
        this.listMatriculas  = new ArrayList<>();
        inicializarDisciplinasBNCC();
    }

    // ----------------------------------------------------------------
    // Disciplinas BNCC
    // ----------------------------------------------------------------
    private void inicializarDisciplinasBNCC() {
        listDisciplinas.add(new Disciplina("Língua Portuguesa",  "Linguagens"));
        listDisciplinas.add(new Disciplina("Matemática",         "Matemática"));
        listDisciplinas.add(new Disciplina("Ciências",           "Ciências da Natureza"));
        listDisciplinas.add(new Disciplina("História",           "Ciências Humanas"));
        listDisciplinas.add(new Disciplina("Geografia",          "Ciências Humanas"));
        listDisciplinas.add(new Disciplina("Arte",               "Linguagens"));
        listDisciplinas.add(new Disciplina("Educação Física",    "Linguagens"));
        listDisciplinas.add(new Disciplina("Língua Inglesa",     "Linguagens"));
    }

    // ----------------------------------------------------------------
    // VERIFICAÇÕES DE DUPLICIDADE
    // ----------------------------------------------------------------
    public String verificarCpfDuplicado(String cpf) {
        for (Aluno a : listAlunos)
            if (a.getCpf().equals(cpf))
                return "CPF já cadastrado para o aluno: " + a.getNome();
        for (Professor p : listProfessores)
            if (p.getCpf().equals(cpf))
                return "CPF já cadastrado para o professor: " + p.getNome();
        return null;
    }

    public String verificarMatriculaDuplicada(String matricula) {
        for (Aluno a : listAlunos)
            if (a.getMatricula().equalsIgnoreCase(matricula))
                return "Matrícula \"" + matricula + "\" já está em uso pelo aluno: " + a.getNome();
        return null;
    }

    public String verificarCodigoTurmaDuplicado(String codigo) {
        for (Turma t : listTurmas)
            if (t.getCodigoTurma().equalsIgnoreCase(codigo))
                return "Código de turma \"" + codigo + "\" já existe.";
        return null;
    }

    public String verificarEmailDuplicado(String email) {
        for (Aluno a : listAlunos)
            if (a.getEmail().equalsIgnoreCase(email))
                return "E-mail já cadastrado para o aluno: " + a.getNome();
        for (Professor p : listProfessores)
            if (p.getEmail().equalsIgnoreCase(email))
                return "E-mail já cadastrado para o professor: " + p.getNome();
        return null;
    }

    // ----------------------------------------------------------------
    // CADASTROS
    // ----------------------------------------------------------------
    public String cadastrarAluno(String nome, String cpf, String email,
                                 String telefone, String matricula, double notaMedia) {
        String erro;
        erro = verificarCpfDuplicado(cpf);        if (erro != null) return erro;
        erro = verificarMatriculaDuplicada(matricula); if (erro != null) return erro;
        erro = verificarEmailDuplicado(email);    if (erro != null) return erro;

        listAlunos.add(new Aluno(nome, cpf, email, telefone, matricula, notaMedia));
        return null;
    }

    public String cadastrarProfessor(String nome, String cpf, String email,
                                     String telefone, String especialidade, int maxTurmas) {
        String erro;
        erro = verificarCpfDuplicado(cpf);     if (erro != null) return erro;
        erro = verificarEmailDuplicado(email); if (erro != null) return erro;

        listProfessores.add(new Professor(nome, cpf, email, telefone, especialidade, maxTurmas));
        return null;
    }

    // ----------------------------------------------------------------
    // Atualização de aluno
    // ----------------------------------------------------------------
    public boolean atualizarAluno(String matricula, String novoNome,
                                  String novoEmail, String novoTelefone, double novaNota) {
        for (Aluno a : listAlunos) {
            if (a.getMatricula().equals(matricula)) {
                if (!a.getEmail().equalsIgnoreCase(novoEmail)) {
                    String erro = verificarEmailDuplicado(novoEmail);
                    if (erro != null) return false;
                }
                a.setNome(novoNome);
                a.setEmail(novoEmail);
                a.setTelefone(novoTelefone);
                a.setNotaMedia(novaNota);
                return true;
            }
        }
        return false;
    }

    // ----------------------------------------------------------------
    // Turmas
    // ----------------------------------------------------------------
    public String criarTurma(String codigoTurma, String nomeDisciplina, String horario, int capacidadeMaxima) {
        String erro = verificarCodigoTurmaDuplicado(codigoTurma);
        if (erro != null) return erro;

        Disciplina disc = null;
        for (Disciplina d : listDisciplinas)
            if (d.getNome().equalsIgnoreCase(nomeDisciplina)) { disc = d; break; }
        if (disc == null)
            return "Disciplina \"" + nomeDisciplina + "\" não encontrada. Use o nome exato da lista BNCC.";

        listTurmas.add(new Turma(codigoTurma, disc, horario, capacidadeMaxima));
        return null;
    }

    public String alocarProfessorATurma(String cpfProfessor, String codigoTurma) {
        Professor prof = buscarProfessorPorCpf(cpfProfessor);
        if (prof == null)
            return "Professor com CPF \"" + cpfProfessor + "\" não encontrado.";

        for (Turma t : listTurmas) {
            if (t.getCodigoTurma().equals(codigoTurma)) {
                for (Turma tp : prof.getTurmas())
                    if (tp.getCodigoTurma().equals(codigoTurma))
                        return "Professor " + prof.getNome() + " já está alocado nesta turma.";

                if (prof.getTurmas().size() >= prof.getMaxTurmas())
                    return "Professor " + prof.getNome() + " atingiu o limite de "
                           + prof.getMaxTurmas() + " turma(s).";

                prof.adicionarTurma(t);
                t.setProfessor(prof);
                return null;
            }
        }
        return "Turma \"" + codigoTurma + "\" não encontrada.";
    }

    public String matricularAlunoEmTurma(String matriculaAluno, String codigoTurma) {
        Aluno aluno = buscarAlunoPorMatricula(matriculaAluno);
        if (aluno == null)
            return "Aluno com matrícula \"" + matriculaAluno + "\" não encontrado.";

        for (Turma t : listTurmas) {
            if (t.getCodigoTurma().equals(codigoTurma)) {
                for (Aluno a : t.getAlunos())
                    if (a.getMatricula().equals(matriculaAluno))
                        return "Aluno " + aluno.getNome() + " já está matriculado nesta turma.";

                // Verifica o limite de alunos por turma ANTES de tentar matricular
                if (t.estaCheia())
                    return "Turma \"" + codigoTurma + "\" atingiu o limite de "
                           + t.getCapacidadeMaxima() + " aluno(s). Não há vagas disponíveis.";

                t.adicionarAluno(aluno);
                aluno.adicionarTurma(t);
                listMatriculas.add(new Matricula(aluno, t));
                return null;
            }
        }
        return "Turma \"" + codigoTurma + "\" não encontrada.";
    }

    // ----------------------------------------------------------------
    // AUTENTICAÇÃO (Login)
    // ----------------------------------------------------------------
    private static final String LOGIN_COORDENACAO  = "root";
    private static final String SENHA_COORDENACAO  = "root";

    /**
     * Autentica por login + senha.
     * - Coordenação: login fixo "root" / senha fixa "root".
     * - Aluno: login = CPF, senha = matrícula (Aluno.getSenha() é polimórfico).
     * - Professor: login = CPF, senha = CPF (Professor.getSenha() é polimórfico).
     * Retorna null se as credenciais forem inválidas.
     */
    public LoginResult autenticar(String login, String senha) {
        if (login == null || senha == null) return null;

        if (login.equals(LOGIN_COORDENACAO) && senha.equals(SENHA_COORDENACAO))
            return new LoginResult(LoginResult.Papel.COORDENACAO, null);

        for (Aluno a : listAlunos)
            if (a.getCpf().equals(login) && a.getSenha().equals(senha))
                return new LoginResult(LoginResult.Papel.ALUNO, a);

        for (Professor p : listProfessores)
            if (p.getCpf().equals(login) && p.getSenha().equals(senha))
                return new LoginResult(LoginResult.Papel.PROFESSOR, p);

        return null; // credenciais inválidas
    }

    // ----------------------------------------------------------------
    // Registro de faltas — usa Professor.registrarFrequencia() (polimorfismo)
    // ----------------------------------------------------------------
    public String registrarFalta(String matriculaAluno, int quantidade) {
        Aluno aluno = buscarAlunoPorMatricula(matriculaAluno);
        if (aluno == null)
            return "Aluno com matrícula \"" + matriculaAluno + "\" não encontrado.";

        // Procura um professor da primeira turma do aluno para usar o método pedagógico
        if (!aluno.getTurmas().isEmpty()) {
            Professor prof = aluno.getTurmas().get(0).getProfessor();
            if (prof != null) {
                if (!prof.registrarFrequencia(aluno, quantidade))
                    return "Quantidade de faltas deve ser maior que zero.";
                return null;
            }
        }
        // Fallback direto se não há professor alocado
        if (!aluno.registrarFalta(quantidade))
            return "Quantidade de faltas deve ser maior que zero.";
        return null;
    }

    /**
     * Registra falta de UM aluno em uma data específica (usado pelo
     * calendário de faltas do Professor). codigoTurma identifica em
     * qual turma a falta ocorreu.
     */
    public String registrarFaltaPorData(String matriculaAluno, String codigoTurma, java.time.LocalDate data) {
        Aluno aluno = buscarAlunoPorMatricula(matriculaAluno);
        if (aluno == null)
            return "Aluno com matrícula \"" + matriculaAluno + "\" não encontrado.";
        if (data == null)
            return "Data inválida.";
        if (data.isAfter(java.time.LocalDate.now()))
            return "Não é possível lançar falta em data futura.";

        boolean ok = aluno.registrarFalta(data, codigoTurma);
        if (!ok) return "Já existe falta lançada para este aluno nesta data/turma.";
        return null;
    }

    /**
     * Aluno envia a justificativa (texto) de uma falta específica, pela data.
     * Fica com status PENDENTE até alguém (professor/coordenação) avaliar.
     */
    public String justificarFalta(String matriculaAluno, java.time.LocalDate data, String textoJustificativa) {
        Aluno aluno = buscarAlunoPorMatricula(matriculaAluno);
        if (aluno == null)
            return "Aluno com matrícula \"" + matriculaAluno + "\" não encontrado.";
        if (textoJustificativa == null || textoJustificativa.isBlank())
            return "A justificativa não pode ser vazia.";

        boolean ok = aluno.justificarFalta(data, textoJustificativa);
        if (!ok) return "Não foi encontrada falta registrada nesta data.";
        return null;
    }

    // ----------------------------------------------------------------
    // LISTAGENS
    // ----------------------------------------------------------------

    /**
     * Lista TODAS as pessoas (alunos e professores) usando POLIMORFISMO.
     * Itera sobre List<Pessoa> e chama exibirResumo() — Java decide em
     * tempo de execução qual versão chamar (Aluno ou Professor).
     * Isso é polimorfismo por herança na prática.
     */
    public String listarTodasPessoas() throws UserNotFoundException {
        if (listAlunos.isEmpty() && listProfessores.isEmpty())
            throw new UserNotFoundException("Nenhuma pessoa cadastrada no sistema.");

        // List<Pessoa> — tipo da classe abstrata — recebe alunos E professores
        List<Pessoa> todasPessoas = new ArrayList<>();
        todasPessoas.addAll(listAlunos);
        todasPessoas.addAll(listProfessores);

        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(todasPessoas.size()).append(" pessoa(s)\n");
        sb.append("─".repeat(90)).append("\n");
        // Aqui o polimorfismo acontece: exibirResumo() é chamado via List<Pessoa>
        for (Pessoa p : todasPessoas)
            sb.append(p.exibirResumo()).append("\n");

        return sb.toString();
    }

    /**
     * Lista desempenho usando polimorfismo da INTERFACE Desempenho.
     * A variável é do tipo Desempenho, não Aluno.
     */
    public String listarDesempenhoAlunos() throws UserNotFoundException {
        if (listAlunos.isEmpty())
            throw new UserNotFoundException("Nenhum aluno cadastrado no sistema.");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-20s %-8s %-8s %-10s%n",
                "Matrícula", "Nome", "Média", "Faltas", "Meta (%)"));
        sb.append("─".repeat(65)).append("\n");

        // Polimorfismo de interface: variável do tipo Desempenho
        for (Aluno a : listAlunos) {
            Desempenho d = a; // referência pela interface
            sb.append(String.format("%-12s %-20s %-8.1f %-8d %-6.1f%%%n",
                    a.getMatricula(), a.getNome(),
                    a.getNotaMedia(), a.getFaltas(),
                    d.calcularMeta())); // chamado via interface
        }
        return sb.toString();
    }

    public String listarAlunos() throws UserNotFoundException {
        if (listAlunos.isEmpty())
            throw new UserNotFoundException("Nenhum aluno cadastrado no sistema.");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-20s %-6s %-6s %-10s%n",
                "Matrícula", "Nome", "Média", "Faltas", "Meta"));
        sb.append("─".repeat(60)).append("\n");
        for (Aluno a : listAlunos)
            sb.append(String.format("%-12s %-20s %-6.1f %-6d %-6.1f%%%n",
                    a.getMatricula(), a.getNome(),
                    a.getNotaMedia(), a.getFaltas(), a.calcularMeta()));
        return sb.toString();
    }

    public String listarProfessores() throws UserNotFoundException {
        if (listProfessores.isEmpty())
            throw new UserNotFoundException("Nenhum professor cadastrado no sistema.");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-14s %-20s %-18s %-5s %-5s%n",
                "CPF", "Nome", "Especialidade", "Max", "Atual"));
        sb.append("─".repeat(70)).append("\n");
        for (Professor p : listProfessores)
            sb.append(String.format("%-14s %-20s %-18s %-5d %-5d%n",
                    p.getCpf(), p.getNome(), p.getEspecialidade(),
                    p.getMaxTurmas(), p.getQtdTurmasAlocadas()));
        return sb.toString();
    }

    public String listarTurmasDoAluno(String matriculaAluno) throws UserNotFoundException {
        Aluno aluno = buscarAlunoPorMatricula(matriculaAluno);
        if (aluno == null)
            throw new UserNotFoundException("Aluno não encontrado: " + matriculaAluno);

        List<Turma> turmas = aluno.getTurmas();
        if (turmas.isEmpty()) return "Aluno " + aluno.getNome() + " não está em nenhuma turma.";

        StringBuilder sb = new StringBuilder("Turmas de " + aluno.getNome() + ":\n");
        sb.append("─".repeat(60)).append("\n");
        for (Turma t : turmas) {
            String prof = t.getProfessor() != null ? t.getProfessor().getNome() : "Sem professor";
            sb.append(String.format("  %-10s | %-18s | %-20s | %s | Vagas: %d/%d%n",
                    t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(), prof,
                    t.getAlunos().size(), t.getCapacidadeMaxima()));
        }
        return sb.toString();
    }

    public String listarTurmasDoProfessor(String cpfProfessor) throws UserNotFoundException {
        Professor prof = buscarProfessorPorCpf(cpfProfessor);
        if (prof == null)
            throw new UserNotFoundException("Professor não encontrado: " + cpfProfessor);

        List<Turma> turmas = prof.getTurmas();
        if (turmas.isEmpty()) return "Professor " + prof.getNome() + " não leciona em nenhuma turma.";

        StringBuilder sb = new StringBuilder("Turmas de " + prof.getNome() + ":\n");
        sb.append("─".repeat(50)).append("\n");
        for (Turma t : turmas)
            sb.append(String.format("  %-10s | %-18s | %s | Vagas: %d/%d%n",
                    t.getCodigoTurma(), t.getDisciplina().getNome(), t.getHorario(),
                    t.getAlunos().size(), t.getCapacidadeMaxima()));
        return sb.toString();
    }

    public String listarMatriculas() throws UserNotFoundException {
        if (listMatriculas.isEmpty())
            throw new UserNotFoundException("Nenhuma matrícula registrada.");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s %-20s %-10s %-12s %-10s%n",
                "Código", "Aluno", "Turma", "Data", "Status"));
        sb.append("─".repeat(90)).append("\n");
        for (Matricula m : listMatriculas)
            sb.append(String.format("%-30s %-20s %-10s %-12s %-10s%n",
                    m.getCodigo(), m.getAluno().getNome(),
                    m.getTurma().getCodigoTurma(),
                    m.getDataMatricula(), m.getStatus()));
        return sb.toString();
    }

    /**
     * Lista os contatos de todas as pessoas usando o método CONCRETO
     * exibirContato(), definido uma única vez na classe abstrata Pessoa.
     * Mesmo sendo concreto (não abstrato), o método internamente chama
     * getTipo() — que É abstrato — então o texto muda de acordo com o
     * tipo real do objeto (Aluno ou Professor) em tempo de execução.
     */
    public String listarContatos() throws UserNotFoundException {
        if (listAlunos.isEmpty() && listProfessores.isEmpty())
            throw new UserNotFoundException("Nenhuma pessoa cadastrada no sistema.");

        List<Pessoa> todasPessoas = new ArrayList<>();
        todasPessoas.addAll(listAlunos);
        todasPessoas.addAll(listProfessores);

        StringBuilder sb = new StringBuilder("Contatos cadastrados:\n");
        sb.append("─".repeat(70)).append("\n");
        for (Pessoa p : todasPessoas)
            sb.append(p.exibirContato()).append("\n");

        return sb.toString();
    }

    public String listarDisciplinasBNCC() {
        StringBuilder sb = new StringBuilder("Componentes Curriculares (BNCC):\n");
        sb.append("─".repeat(50)).append("\n");
        for (Disciplina d : listDisciplinas)
            sb.append(String.format("  %-20s [%s]%n", d.getNome(), d.getAreaConhecimento()));
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // Acesso às listas (para a GUI montar combos e tabelas)
    // ----------------------------------------------------------------
    public List<Turma>     getTurmas()      { return listTurmas; }
    public List<Aluno>     getAlunos()      { return listAlunos; }
    public List<Professor> getProfessores() { return listProfessores; }

        // ----------------------------------------------------------------
    // Persistência
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void carregarDados(String nomeArquivo) throws IOException, ClassNotFoundException {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            this.listAlunos      = (ArrayList<Aluno>)     ois.readObject();
            this.listProfessores = (ArrayList<Professor>) ois.readObject();
            this.listTurmas      = (ArrayList<Turma>)     ois.readObject();
            this.listMatriculas  = (ArrayList<Matricula>) ois.readObject();
        }
    }

    public void salvarDados(String nomeArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(this.listAlunos);
            oos.writeObject(this.listProfessores);
            oos.writeObject(this.listTurmas);
            oos.writeObject(this.listMatriculas);
        }
    }

    // ----------------------------------------------------------------
    // Helpers privados
    // ----------------------------------------------------------------
    private Aluno buscarAlunoPorMatricula(String matricula) {
        for (Aluno a : listAlunos)
            if (a.getMatricula().equals(matricula)) return a;
        return null;
    }

    private Professor buscarProfessorPorCpf(String cpf) {
        for (Professor p : listProfessores)
            if (p.getCpf().equals(cpf)) return p;
        return null;
    }
}
