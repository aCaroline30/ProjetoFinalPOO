import domain.*;
import persistence.PersistenceManager;

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
    private static GeradorDeRelatorios gerRelatorios = new GeradorDeRelatorios(gerIngressos);

    private static Map<String, Cliente> clientes = new HashMap<>();
    private static AuthManager auth = new AuthManager();

    private static PersistenceManager persistencia = new PersistenceManager();
    private static final String CAMINHO_JSON = "estado_cinema.json";

    public static void main(String[] args) {
        seedUsuarios();
        carregarDados();

        while (true) {
            mostrarMenuPrincipal();
            String opc = scanner.nextLine().trim();
            switch (opc) {
                case "1": entrar(); break;
                case "2": cadastrarSessaoFlow(); break; 
                case "3": editarSessaoFlow(); break;
                case "4": removerSessaoFlow(); break;
                case "5": venderIngressoFlow(); break;
                case "6": cancelarIngressoFlow(); break;
                case "7": relatoriosFlow(); break;
                case "8": listarTudo(); break;
                case "9": logoutFlow(); break;
                case "0": salvarDados(); System.out.println("Saindo"); return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static void seedUsuarios() {
       
        Usuario admin = new Usuario(UUID.randomUUID().toString(), "admin", "admin", Usuario.Role.ADMIN);
        Usuario caixa = new Usuario(UUID.randomUUID().toString(), "caixa", "caixa", Usuario.Role.OPERADOR);
        auth.adicionarUsuario(admin);
        auth.adicionarUsuario(caixa);
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== SISTEMA DE CINEMA ===");
        System.out.println("Usuário logado: " + (auth.isLogado() ? auth.getUsuarioLogado().getUsername() + " (" + auth.getUsuarioLogado().getRole() + ")" : "nenhum"));
        System.out.println("1 - Entrar");
        System.out.println("2 - Cadastrar sessão (ADMIN)");
        System.out.println("3 - Editar sessão (ADMIN)");
        System.out.println("4 - Remover sessão (ADMIN)");
        System.out.println("5 - Vender ingresso");
        System.out.println("6 - Cancelar ingresso");
        System.out.println("7 - Relatórios (ADMIN)");
        System.out.println("8 - Listar tudo");
        System.out.println("9 - Logout");
        System.out.println("0 - Salvar e Sair");
        System.out.print("Escolha: ");
    }

    private static void entrar() {
        if (auth.isLogado()) {
            System.out.println("Já está logado como " + auth.getUsuarioLogado().getUsername());
            return;
        }
        System.out.print("Usuário: ");
        String user = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();
        if (auth.login(user, senha)) System.out.println("Login efetuado: " + auth.getUsuarioLogado().getUsername());
        else System.out.println("Credenciais inválidas.");
    }

    private static void logoutFlow() {
        if (auth.isLogado()) {
            System.out.println("Logout: " + auth.getUsuarioLogado().getUsername());
            auth.logout();
        } else System.out.println("Nenhum usuário logado.");
    }

  

    private static void cadastrarSessaoFlow() {
        if (!checarPermissaoAdmin()) return;
        listarFilmes();
        System.out.print("ID do filme: "); String idFilme = scanner.nextLine().trim();
        Filme f = gerFilmes.buscarPorId(idFilme);
        if (f == null) { System.out.println("Filme não encontrado. Cadastre o filme primeiro."); return; }

        listarSalas();
        System.out.print("ID da sala: "); String idSala = scanner.nextLine().trim();
        Sala s = gerSalas.buscarPorId(idSala);
        if (s == null) { System.out.println("Sala não encontrada. Cadastre primeiro."); return; }

        System.out.print("Data e hora (YYYY-MM-DDTHH:MM): "); LocalDateTime dt = LocalDateTime.parse(scanner.nextLine().trim());
        System.out.print("Preço: "); double preco = Double.parseDouble(scanner.nextLine().trim());
        String id = UUID.randomUUID().toString();
        Sessao sessao = new Sessao(id, f, s, dt, preco);
        try {
            gerSessoes.adicionar(sessao);
            System.out.println("Sessão criada: " + sessao.getId());
        } catch (Exception e) {
            System.out.println("Erro ao criar sessão: " + e.getMessage());
        }
    }

    private static void editarSessaoFlow() {
        if (!checarPermissaoAdmin()) return;
        listarSessoes();
        System.out.print("ID da sessão a editar: "); String id = scanner.nextLine().trim();
        Sessao atual = gerSessoes.buscarPorId(id);
        if (atual == null) { System.out.println("Sessão não encontrada."); return; }

        listarFilmes();
        System.out.print("Novo ID do filme: "); String idFilme = scanner.nextLine().trim();
        Filme f = gerFilmes.buscarPorId(idFilme);
        if (f == null) { System.out.println("Filme não encontrado."); return; }

        listarSalas();
        System.out.print("Novo ID da sala: "); String idSala = scanner.nextLine().trim();
        Sala s = gerSalas.buscarPorId(idSala);
        if (s == null) { System.out.println("Sala não encontrada."); return; }

        System.out.print("Nova data e hora (YYYY-MM-DDTHH:MM): "); LocalDateTime dt = LocalDateTime.parse(scanner.nextLine().trim());
        System.out.print("Novo preço: "); double preco = Double.parseDouble(scanner.nextLine().trim());

        try {
            gerSessoes.editar(id, f, s, dt, preco);
            System.out.println("Sessão editada.");
        } catch (Exception e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        }
    }

    private static void removerSessaoFlow() {
        if (!checarPermissaoAdmin()) return;
        listarSessoes();
        System.out.print("ID da sessão a remover: "); String id = scanner.nextLine().trim();
     
        gerSessoes.remover(id);
        System.out.println("Sessão removida.");
    }


    private static void venderIngressoFlow() {
      
        if (!auth.isLogado()) { System.out.println("Faça login para vender ingressos."); return; }
        listarClientes();
        System.out.print("ID do cliente (ou enter para cadastrar): ");
        String clienteId = scanner.nextLine().trim();
        Cliente cliente = null;
        if (clienteId.isEmpty()) {
            cliente = cadastrarClienteQuick();
        } else {
            cliente = clientes.get(clienteId);
            if (cliente == null) {
                System.out.println("Cliente não encontrado. Cadastre-o.");
                return;
            }
        }

        listarSessoes();
        System.out.print("ID da sessão: "); String sessaoId = scanner.nextLine().trim();
        Sessao sessao = gerSessoes.buscarPorId(sessaoId);
        if (sessao == null) { System.out.println("Sessão não encontrada."); return; }

        System.out.println("Assentos: ");
        sessao.getSala().getAssentos().forEach(a -> System.out.print(a + " "));
        System.out.println();
        System.out.print("Código do assento: "); String codigo = scanner.nextLine().trim();

        try {
            String id = UUID.randomUUID().toString();
            gerIngressos.vender(id, cliente, sessao, codigo, LocalDateTime.now());
            System.out.println("Ingresso vendido (id=" + id + ").");
        } catch (Exception e) {
            System.out.println("Erro na venda: " + e.getMessage());
        }
    }

    private static Cliente cadastrarClienteQuick() {
        System.out.print("Nome: "); String nome = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Data nascimento (YYYY-MM-DD): "); LocalDate dn = LocalDate.parse(scanner.nextLine().trim());
        String id = UUID.randomUUID().toString();
        Cliente c = new Cliente(id, nome, email, dn);
        clientes.put(id, c);
        System.out.println("Cliente cadastrado com id: " + id);
        return c;
    }

    private static void cancelarIngressoFlow() {
        if (!auth.isLogado()) { System.out.println("Faça login para cancelar ingressos."); return; }
        System.out.print("ID do ingresso: "); String id = scanner.nextLine().trim();
        try {
            gerIngressos.cancelar(id, LocalDateTime.now());
            System.out.println("Ingresso cancelado.");
        } catch (Exception e) {
            System.out.println("Erro no cancelamento: " + e.getMessage());
        }
    }

  

    private static void relatoriosFlow() {
        if (!checarPermissaoAdmin()) return;
        System.out.println("1 - Relatório financeiro por período");
        System.out.println("2 - Estatísticas de filmes por faixa etária");
        System.out.print("Escolha: ");
        String op = scanner.nextLine().trim();
        switch (op) {
            case "1":
                System.out.print("Data/hora início (YYYY-MM-DDTHH:MM): "); LocalDateTime inicio = LocalDateTime.parse(scanner.nextLine().trim());
                System.out.print("Data/hora fim (YYYY-MM-DDTHH:MM): "); LocalDateTime fim = LocalDateTime.parse(scanner.nextLine().trim());
                GeradorDeRelatorios.RelatorioFinanceiro r = gerRelatorios.gerarRelatorioPeriodo(inicio, fim);
                gerRelatorios.imprimirRelatorioFinanceiro(r, inicio, fim);
                break;
            case "2":
                Map<String, Map<String, Long>> stats = gerRelatorios.filmesMaisAssistidosPorFaixaEtaria();
                for (String faixa : stats.keySet()) {
                    System.out.println("Faixa " + faixa + ":");
                    Map<String, Long> mapa = stats.get(faixa);
                    if (mapa.isEmpty()) System.out.println("  (sem registros)");
                    else mapa.forEach((titulo, qtd) -> System.out.println("  " + titulo + " -> " + qtd));
                }
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

   

    private static void listarTudo() {
        System.out.println("=== FILMES ==="); gerFilmes.listarTodos().forEach(System.out::println);
        System.out.println("=== SALAS ==="); gerSalas.listarTodos().forEach(System.out::println);
        System.out.println("=== SESSÕES ==="); gerSessoes.listarTodos().forEach(System.out::println);
        System.out.println("=== CLIENTES ==="); clientes.values().forEach(System.out::println);
        System.out.println("=== INGRESSOS ==="); gerIngressos.listarTodos().forEach(System.out::println);
    }

    private static void listarFilmes() {
        System.out.println("=== FILMES ===");
        gerFilmes.listarTodos().forEach(f -> System.out.println(f.getId() + " - " + f.getTitulo()));
    }

    private static void listarSalas() {
        System.out.println("=== SALAS ===");
        gerSalas.listarTodos().forEach(s -> System.out.println(s.getId() + " - " + s.getNome()));
    }

    private static void listarSessoes() {
        System.out.println("=== SESSÕES ===");
        gerSessoes.listarTodos().forEach(s -> System.out.println(s.getId() + " - " + s.getFilme().getTitulo() + " - " + s.getInicio() + " - Sala: " + s.getSala().getNome()));
    }

    private static void listarClientes() {
        System.out.println("=== CLIENTES ===");
        clientes.values().forEach(c -> System.out.println(c.getId() + " - " + c.getNome()));
    }



    private static boolean checarPermissaoAdmin() {
        if (!auth.isLogado()) { System.out.println("Acesso negado: login requerido."); return false; }
        if (!auth.possuiRole(Usuario.Role.ADMIN)) { System.out.println("Acesso negado: perfil ADMIN requerido."); return false; }
        return true;
    }



    private static void salvarDados() {
        try {
            persistencia.save(CAMINHO_JSON, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes.values());
            System.out.println("Estado salvo em " + CAMINHO_JSON);
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    private static void carregarDados() {
        try {
           
            java.io.File f = new java.io.File(CAMINHO_JSON);
            if (!f.exists()) {
              
                seedDadosExemplo();
                return;
            }
            persistencia.load(CAMINHO_JSON, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes);
            System.out.println("Estado carregado de " + CAMINHO_JSON);
        } catch (IOException e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }

    private static void seedDadosExemplo() {
        Filme f1 = new Filme("F1", "Matrix", "Ação", Duration.ofMinutes(136), 16, "", "");
        Filme f2 = new Filme("F2", "O Rei Leão", "Animação", Duration.ofMinutes(88), 0, "", "");
        Filme f3 = new Filme("F3", "Interestelar", "Ficção", Duration.ofMinutes(169), 10, "", "");
        gerFilmes.adicionar(f1); gerFilmes.adicionar(f2); gerFilmes.adicionar(f3);

        Sala s1 = new Sala("S1", "Sala Principal", 10);
        Sala s2 = new Sala("S2", "Sala VIP", 8);
        gerSalas.adicionar(s1); gerSalas.adicionar(s2);

        Sessao se1 = new Sessao("SE1", f1, s1, LocalDateTime.now().plusDays(1).withHour(19).withMinute(30), 25.0);
        Sessao se2 = new Sessao("SE2", f2, s2, LocalDateTime.now().plusDays(1).withHour(16).withMinute(0), 20.0);
        try { gerSessoes.adicionar(se1); gerSessoes.adicionar(se2); } catch(Exception e){}

        Cliente c1 = new Cliente("C1", "Ana", "ana@mail", LocalDate.of(2000,5,10));
        Cliente c2 = new Cliente("C2", "João", "joao@mail", LocalDate.of(2010,8,12));
        Cliente c3 = new Cliente("C3", "Mariana", "mariana@mail", LocalDate.of(1988,11,22));
        clientes.put(c1.getId(), c1); clientes.put(c2.getId(), c2); clientes.put(c3.getId(), c3);
    }
}

