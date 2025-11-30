import domain.*;
import persistence.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

  
    private static GerenciadorDeFilmes gerFilmes = new GerenciadorDeFilmes();
    private static GerenciadorDeSalas gerSalas = new GerenciadorDeSalas();
    private static GerenciadorDeSessoes gerSessoes = new GerenciadorDeSessoes();
    private static GerenciadorDeIngressos gerIngressos = new GerenciadorDeIngressos();
    private static Map<String, Cliente> clientes = new HashMap<>();

    private static PersistenceManager persistencia = new PersistenceManager();
    private static final String CAMINHO_JSON = "estado_cinema.json";

    public static void main(String[] args) {
        carregarDados();

        while (true) {
            System.out.println("\n===== CINEMA - MENU PRINCIPAL =====");
            System.out.println("1. Cadastrar Filme");
            System.out.println("2. Cadastrar Sala");
            System.out.println("3. Criar Sessao");
            System.out.println("4. Cadastrar Cliente");
            System.out.println("5. Vender Ingresso");
            System.out.println("6. Cancelar Ingresso");
            System.out.println("7. Listar Filmes");
            System.out.println("8. Listar Sessoes");
            System.out.println("9. Salvar Estado");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");

            int op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> cadastrarFilme();
                case 2 -> cadastrarSala();
                case 3 -> criarSessao();
                case 4 -> cadastrarCliente();
                case 5 -> venderIngresso();
                case 6 -> cancelarIngresso();
                case 7 -> listarFilmes();
                case 8 -> listarSessoes();
                case 9 -> salvarDados();
                case 0 -> {
                    salvarDados();
                    System.out.println("Saindo...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }



    private static void cadastrarFilme() {
        System.out.println("Título: ");
        String titulo = scanner.nextLine();

        System.out.println("Gênero: ");
        String genero = scanner.nextLine();

        System.out.println("Duração em minutos: ");
        int min = Integer.parseInt(scanner.nextLine());

        System.out.println("Classificação etária: ");
        int idade = Integer.parseInt(scanner.nextLine());

        String id = UUID.randomUUID().toString();

        Filme f = new Filme(id, titulo, genero, Duration.ofMinutes(min), idade, "", "");
        gerFilmes.adicionar(f);

        System.out.println("Filme cadastrado!");
    }

    private static void cadastrarSala() {
        System.out.println("Nome da sala: ");
        String nome = scanner.nextLine();

        System.out.println("Capacidade: ");
        int cap = Integer.parseInt(scanner.nextLine());

        String id = UUID.randomUUID().toString();

        Sala s = new Sala(id, nome, cap);
        gerSalas.adicionar(s);

        System.out.println("Sala cadastrada!");
    }

    private static void cadastrarCliente() {
        System.out.println("Nome: ");
        String nome = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Data nascimento (AAAA-MM-DD): ");
        LocalDate dn = LocalDate.parse(scanner.nextLine());

        String id = UUID.randomUUID().toString();

        Cliente c = new Cliente(id, nome, email, dn);
        clientes.put(id, c);

        System.out.println("Cliente cadastrado!");
    }

    private static void criarSessao() {
        listarFilmes();
        System.out.println("ID do filme: ");
        String idFilme = scanner.nextLine();
        Filme f = gerFilmes.buscarPorId(idFilme);
        if (f == null) {
            System.out.println("Filme não encontrado.");
            return;
        }

        listarSalas();
        System.out.println("ID da sala: ");
        String idSala = scanner.nextLine();
        Sala s = gerSalas.buscarPorId(idSala);
        if (s == null) {
            System.out.println("Sala não encontrada.");
            return;
        }

        System.out.println("Data e hora (AAAA-MM-DDTHH:MM): ");
        LocalDateTime dt = LocalDateTime.parse(scanner.nextLine());

        System.out.println("Preço: ");
        double preco = Double.parseDouble(scanner.nextLine());

        String id = UUID.randomUUID().toString();

        try {
            Sessao sessao = new Sessao(id, f, s, dt, preco);
            gerSessoes.adicionar(sessao);
            System.out.println("Sessão criada!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void venderIngresso() {
        listarClientes();
        System.out.println("ID cliente: ");
        String idCliente = scanner.nextLine();
        Cliente c = clientes.get(idCliente);
        if (c == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }

        listarSessoes();
        System.out.println("ID sessão: ");
        String idSessao = scanner.nextLine();
        Sessao s = gerSessoes.buscarPorId(idSessao);
        if (s == null) {
            System.out.println("Sessão não encontrada.");
            return;
        }

        System.out.println("Assento (ex: S10): ");
        String assento = scanner.nextLine();

        String id = UUID.randomUUID().toString();

        try {
            gerIngressos.vender(id, c, s, assento, LocalDateTime.now());
            System.out.println("Ingresso vendido!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void cancelarIngresso() {
        System.out.println("ID do ingresso: ");
        String id = scanner.nextLine();

        try {
            gerIngressos.cancelar(id, LocalDateTime.now());
            System.out.println("Ingresso cancelado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // =====================================================
    // LISTAGENS
    // =====================================================

    private static void listarFilmes() {
        System.out.println("=== FILMES ===");
        gerFilmes.listarTodos().forEach(System.out::println);
    }

    private static void listarSalas() {
        System.out.println("=== SALAS ===");
        gerSalas.listarTodos().forEach(System.out::println);
    }

    private static void listarSessoes() {
        System.out.println("=== SESSÕES ===");
        gerSessoes.listarTodos().forEach(System.out::println);
    }

    private static void listarClientes() {
        System.out.println("=== CLIENTES ===");
        clientes.values().forEach(System.out::println);
    }

    // =====================================================
    // PERSISTÊNCIA
    // =====================================================

    private static void salvarDados() {
        try {
            persistencia.save(CAMINHO_JSON, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes.values());
            System.out.println("Estado salvo com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    private static void carregarDados() {
        try {
            if (!new java.io.File(CAMINHO_JSON).exists()) return;
            persistencia.load(CAMINHO_JSON, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes);
            System.out.println("Estado carregado!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }
}
