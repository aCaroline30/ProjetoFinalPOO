import domain.*;
import persistence.PersistenceManager;

import java.io.IOException;
import java.time.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GerenciadorDeFilmes gerFilmes = new GerenciadorDeFilmes();
    private static final GerenciadorDeSalas gerSalas = new GerenciadorDeSalas();
    private static final GerenciadorDeSessoes gerSessoes = new GerenciadorDeSessoes();
    private static final GerenciadorDeIngressos gerIngressos = new GerenciadorDeIngressos();
    private static final GeradorDeRelatorios gerRelatorios = new GeradorDeRelatorios(gerIngressos);
    private static final Map<String, Cliente> clientes = new LinkedHashMap<>();
    private static final AuthManager auth = new AuthManager();
    private static final PersistenceManager persistencia = new PersistenceManager();
    private static final String CAMINHO = "estado_cinema.json";

    public static void main(String[] args) {
        seedUsuarios();
        carregarDados();
        menuLoop();
    }

    private static void menuLoop() {
        while (true) {
            mostrarMenuPrincipal();
            String opc = scanner.nextLine().trim();
            switch (opc) {
                case "1": entrar(); break;
                case "2": gerenciarFilmesFlow(); break;
                case "3": gerenciarSalasFlow(); break;
                case "4": gerenciarClientesFlow(); break;
                case "5": cadastrarSessaoFlow(); break;
                case "6": editarSessaoFlow(); break;
                case "7": removerSessaoFlow(); break;
                case "8": venderIngressoFlow(); break;
                case "9": cancelarIngressoFlow(); break;
                case "10": relatoriosFlow(); break;
                case "11": listarTudo(); break;
                case "12": logoutFlow(); break;
                case "0": salvarDados(); System.out.println("Saindo..."); return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== SISTEMA DE CINEMA ===");
        System.out.println("Usuário logado: " + (auth.isLogado() ? auth.getUsuarioLogado().getUsername() + " (" + auth.getUsuarioLogado().getRole() + ")" : "nenhum"));
        System.out.println("1 - Entrar");
        System.out.println("2 - Gerenciar Filmes (ADMIN)");
        System.out.println("3 - Gerenciar Salas (ADMIN)");
        System.out.println("4 - Gerenciar Clientes (ADMIN)");
        System.out.println("5 - Cadastrar sessão (ADMIN)");
        System.out.println("6 - Editar sessão (ADMIN)");
        System.out.println("7 - Remover sessão (ADMIN)");
        System.out.println("8 - Vender ingresso");
        System.out.println("9 - Cancelar ingresso");
        System.out.println("10 - Relatórios (ADMIN)");
        System.out.println("11 - Listar tudo");
        System.out.println("12 - Logout");
        System.out.println("0 - Salvar e Sair");
        System.out.print("Escolha: ");
    }

    private static void seedUsuarios() {
        Usuario admin = new Usuario(UUID.randomUUID().toString(), "admin", "admin", Usuario.Role.ADMIN);
        Usuario op = new Usuario(UUID.randomUUID().toString(), "operador", "1234", Usuario.Role.OPERADOR);
        auth.adicionarUsuario(admin); auth.adicionarUsuario(op);
    }

    private static void entrar() {
        if (auth.isLogado()) { System.out.println("Já logado como " + auth.getUsuarioLogado().getUsername()); return; }
        System.out.print("Usuário: "); String u = scanner.nextLine().trim();
        System.out.print("Senha: "); String s = scanner.nextLine().trim();
        if (auth.login(u, s)) System.out.println("Login efetuado: " + u); else System.out.println("Credenciais inválidas.");
    }

    private static void logoutFlow() { auth.logout(); System.out.println("Logout efetuado."); }

    private static boolean checarAdmin() {
        if (!auth.isLogado()) { System.out.println("Login necessário."); return false; }
        if (!auth.possuiRole(Usuario.Role.ADMIN)) { System.out.println("Permissão ADMIN necessária."); return false; }
        return true;
    }


    private static void gerenciarFilmesFlow() {
        if (!checarAdmin()) return;
        System.out.println("1-Cadastrar 2-Editar 3-Remover 4-Listar 0-Voltar"); String op = scanner.nextLine().trim();
        switch (op) {
            case "1": cadastrarFilme(); break;
            case "2": editarFilmeFlow(); break;
            case "3": removerFilmeFlow(); break;
            case "4": listarFilmesFull(); break;
            default: break;
        }
    }
    private static void cadastrarFilme() {
        System.out.print("Título: "); String titulo = scanner.nextLine().trim();
        System.out.print("Gênero: "); String genero = scanner.nextLine().trim();
        System.out.print("Duração (min): "); int dur = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Classificação: "); int cl = Integer.parseInt(scanner.nextLine().trim());
        String id = "FIL-" + UUID.randomUUID().toString().substring(0,8);
        Filme f = new Filme(id, titulo, genero, Duration.ofMinutes(dur), cl, "", "");
        gerFilmes.adicionar(f); System.out.println("Filme criado: " + id);
    }
    private static void editarFilmeFlow() {
        listarFilmesFull(); System.out.print("ID filme: "); String id = scanner.nextLine().trim();
        Filme f = gerFilmes.buscarPorId(id); if (f==null) { System.out.println("Não encontrado"); return;}
        System.out.print("Novo título (enter=manter): "); String t = scanner.nextLine().trim(); if (!t.isEmpty()) f.setTitulo(t);
        System.out.print("Novo genero (enter=manter): "); String g = scanner.nextLine().trim(); if (!g.isEmpty()) f.setGenero(g);
    }
    private static void removerFilmeFlow() {
        listarFilmesFull(); System.out.print("ID filme remover: "); String id = scanner.nextLine().trim();
        if (!gerSessoes.listarPorFilme(id).isEmpty()) { System.out.println("Existem sessões vinculadas. Remova-as primeiro."); return; }
        gerFilmes.remover(id); System.out.println("Removido.");
    }
    private static void listarFilmesFull() { for (Filme f : gerFilmes.listarTodos()) System.out.println(f); }

    // --- salas ---
    private static void gerenciarSalasFlow() {
        if (!checarAdmin()) return;
        System.out.println("1-Cadastrar 2-Editar 3-Remover 4-Listar"); String op = scanner.nextLine().trim();
        switch (op) {
            case "1": cadastrarSalaFlow(); break;
            case "2": editarSalaFlow(); break;
            case "3": removerSalaFlow(); break;
            case "4": listarSalas(); break;
            default: break;
        }
    }
    private static void cadastrarSalaFlow() {
        System.out.print("Nome sala: "); String nome = scanner.nextLine().trim();
        System.out.print("Capacidade: "); int cap = Integer.parseInt(scanner.nextLine().trim());
        String id = "SAL-" + UUID.randomUUID().toString().substring(0,8);
        Sala s = new Sala(id, nome, cap); gerSalas.adicionar(s); System.out.println("Sala criada: " + id);
    }
    private static void editarSalaFlow() {
        listarSalas(); System.out.print("ID sala: "); String id = scanner.nextLine().trim();
        Sala s = gerSalas.buscarPorId(id); if (s==null) { System.out.println("Não encontrado"); return; }
        if (!gerSessoes.listarTodos().stream().noneMatch(ss -> ss.getSala()!=null && ss.getSala().getId().equals(id))) {
            System.out.println("Existem sessões vinculadas — remova-as antes de alterar capacidade."); return;
        }
        System.out.print("Novo nome (enter=manter): "); String n = scanner.nextLine().trim(); if (!n.isEmpty()) s.setNome(n);
        System.out.print("Nova capacidade (enter=manter): "); String cs = scanner.nextLine().trim(); if (!cs.isEmpty()) s.setCapacidade(Integer.parseInt(cs));
        System.out.println("Atualizada.");
    }
    private static void removerSalaFlow() {
        listarSalas(); System.out.print("ID sala remover: "); String id = scanner.nextLine().trim();
        if (gerSessoes.listarTodos().stream().anyMatch(ss -> ss.getSala()!=null && ss.getSala().getId().equals(id))) { System.out.println("Existem sessões vinculadas."); return; }
        gerSalas.remover(id); System.out.println("Removida.");
    }
    private static void listarSalas() { for (Sala s: gerSalas.listarTodos()) System.out.println(s); }

   
    private static void gerenciarClientesFlow() {
        if (!checarAdmin()) return;
        System.out.println("1-Cadastrar 2-Editar 3-Remover 4-Listar"); String op = scanner.nextLine().trim();
        switch (op) {
            case "1": cadastrarClienteFlow(); break;
            case "2": editarClienteFlow(); break;
            case "3": removerClienteFlow(); break;
            case "4": listarClientes(); break;
            default: break;
        }
    }
    private static void cadastrarClienteFlow() {
        System.out.print("Nome: "); String nome = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Data Nascimento (YYYY-MM-DD): "); LocalDate dn = LocalDate.parse(scanner.nextLine().trim());
        String id = "CLI-" + UUID.randomUUID().toString().substring(0,8);
        Cliente c = new Cliente(id, nome, email, dn); clientes.put(id, c); System.out.println("Cliente criado: " + id);
    }
    private static void editarClienteFlow() {
        listarClientes(); System.out.print("ID cliente: "); String id = scanner.nextLine().trim();
        Cliente c = clientes.get(id); if (c==null) { System.out.println("Não encontrado"); return; }
        System.out.print("Novo nome (enter=manter): "); String n = scanner.nextLine().trim(); if (!n.isEmpty()) c.setNome(n);
        System.out.print("Novo email (enter=manter): "); String e = scanner.nextLine().trim(); if (!e.isEmpty()) c.setEmail(e);
    }
    private static void removerClienteFlow() {
        listarClientes(); System.out.print("ID cliente remover: "); String id = scanner.nextLine().trim();
        if (gerIngressos.listarTodos().stream().anyMatch(i -> i.getCliente()!=null && i.getCliente().getId().equals(id))) { System.out.println("Existem ingressos vinculados."); return; }
        clientes.remove(id); System.out.println("Removido.");
    }
    private static void listarClientes() { for (Cliente c: clientes.values()) System.out.println(c); }

  
    private static void cadastrarSessaoFlow() {
        if (!checarAdmin()) return;
        listarFilmesFull(); System.out.print("ID filme: "); String fid = scanner.nextLine().trim();
        Filme f = gerFilmes.buscarPorId(fid); if (f==null) { System.out.println("Filme não existe"); return; }
        listarSalas(); System.out.print("ID sala: "); String sid = scanner.nextLine().trim();
        Sala s = gerSalas.buscarPorId(sid); if (s==null) { System.out.println("Sala não existe"); return; }
        System.out.print("DataHora (YYYY-MM-DDTHH:MM): "); LocalDateTime dt = LocalDateTime.parse(scanner.nextLine().trim());
        System.out.print("Preço: "); double p = Double.parseDouble(scanner.nextLine().trim());
        String id = "SES-" + UUID.randomUUID().toString().substring(0,8);
        Sessao se = new Sessao(id, f, s, dt, p);
        try { gerSessoes.adicionar(se); System.out.println("Sessão criada: " + id); } catch (Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
    }
    private static void listarSessoes() { for (Sessao se: gerSessoes.listarTodos()) System.out.println(se); }
    private static void editarSessaoFlow() { if (!checarAdmin()) return; listarSessoes(); System.out.print("ID sessao: "); String id = scanner.nextLine().trim();
        Sessao s = gerSessoes.buscarPorId(id); if (s==null) { System.out.println("Não encontrado"); return; }
        listarFilmesFull(); System.out.print("ID novo filme: "); String fid = scanner.nextLine().trim(); Filme f = gerFilmes.buscarPorId(fid);
        listarSalas(); System.out.print("ID nova sala: "); String sid = scanner.nextLine().trim(); Sala sala = gerSalas.buscarPorId(sid);
        System.out.print("Nova dataHora (YYYY-MM-DDTHH:MM): "); LocalDateTime dt = LocalDateTime.parse(scanner.nextLine().trim());
        System.out.print("Novo preço: "); double p = Double.parseDouble(scanner.nextLine().trim());
        try { gerSessoes.editar(id, f, sala, dt, p); System.out.println("Editada."); } catch (Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
    }
    private static void removerSessaoFlow() { if (!checarAdmin()) return; listarSessoes(); System.out.print("ID sessao remover: "); String id = scanner.nextLine().trim();
        if (gerIngressos.listarTodos().stream().anyMatch(i -> i.getSessao()!=null && i.getSessao().getId().equals(id))) { System.out.println("Existem ingressos vinculados."); return; }
        gerSessoes.remover(id); System.out.println("Removida.");
    }

   
    private static void venderIngressoFlow() {
        if (!auth.isLogado()) { System.out.println("Login necessário."); return; }
        listarClientes(); System.out.print("ID cliente (enter para cadastrar rápido): "); String cid = scanner.nextLine().trim();
        Cliente c = null;
        if (cid.isEmpty()) c = cadastrarClienteQuick(); else { c = clientes.get(cid); if (c==null) { System.out.println("Cliente não existe"); return; } }
        listarSessoes(); System.out.print("ID sessao: "); String sid = scanner.nextLine().trim(); Sessao se = gerSessoes.buscarPorId(sid);
        if (se==null) { System.out.println("Sessão inválida"); return; }
        System.out.println("Assentos: "); se.getSala().getAssentos().forEach(a -> System.out.print(a.getCodigo() + (a.isOcupado()? "(X) ":"( ) ")));
        System.out.println(); System.out.print("Assento: "); String ac = scanner.nextLine().trim();
        try { String id = "ING-" + UUID.randomUUID().toString().substring(0,8); gerIngressos.vender(id, c, se, ac, LocalDateTime.now()); System.out.println("Vendido: " + id); }
        catch (Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
    }
    private static Cliente cadastrarClienteQuick() {
        System.out.print("Nome: "); String n = scanner.nextLine().trim();
        System.out.print("Email: "); String e = scanner.nextLine().trim();
        System.out.print("Data (YYYY-MM-DD): "); LocalDate d = LocalDate.parse(scanner.nextLine().trim());
        String id = "CLI-" + UUID.randomUUID().toString().substring(0,8);
        Cliente c = new Cliente(id, n, e, d); clientes.put(id, c); System.out.println("Criado: " + id); return c;
    }
    private static void cancelarIngressoFlow() {
        if (!auth.isLogado()) { System.out.println("Login necessário."); return; }
        System.out.print("ID ingresso: "); String id = scanner.nextLine().trim();
        try { gerIngressos.cancelar(id, LocalDateTime.now()); System.out.println("Cancelado."); } catch (Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
    }

 
    private static void relatoriosFlow() {
        if (!checarAdmin()) return;
        System.out.println("1-Financeiro por período 2-Estatísticas por faixa etária"); String op = scanner.nextLine().trim();
        if ("1".equals(op)) {
            System.out.print("Inicio (YYYY-MM-DDTHH:MM): "); LocalDateTime inicio = LocalDateTime.parse(scanner.nextLine().trim());
            System.out.print("Fim    (YYYY-MM-DDTHH:MM): "); LocalDateTime fim = LocalDateTime.parse(scanner.nextLine().trim());
            GeradorDeRelatorios.RelatorioFinanceiro r = gerRelatorios.gerarRelatorioPeriodo(inicio, fim); gerRelatorios.imprimirRelatorioFinanceiro(r, inicio, fim);
        } else if ("2".equals(op)) {
            Map<String, Map<String, Long>> stats = gerRelatorios.filmesMaisAssistidosPorFaixaEtaria();
            stats.forEach((faixa,mapa) -> { System.out.println("Faixa " + faixa); mapa.forEach((t,q) -> System.out.println(" " + t + " -> " + q)); });
        }
    }

  
    private static void listarTudo() {
        System.out.println("FILMES:"); listarFilmesFull();
        System.out.println("SALAS:"); listarSalas();
        System.out.println("SESSÕES:"); listarSessoes();
        System.out.println("CLIENTES:"); listarClientes();
        System.out.println("INGRESSOS:"); gerIngressos.listarTodos().forEach(System.out::println);
    }

    private static void salvarDados() {
        try { persistencia.save(CAMINHO, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes.values(), Arrays.asList(new Usuario("u","admin","admin", Usuario.Role.ADMIN))); 
            System.out.println("Estado salvo."); } catch (IOException e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private static void carregarDados() {
        try {
            java.io.File f = new java.io.File(CAMINHO);
            if (!f.exists()) { seedDadosExemplo(); return; }
            persistencia.load(CAMINHO, gerFilmes, gerSalas, gerSessoes, gerIngressos, clientes, auth);
            System.out.println("Estado carregado.");
        } catch (IOException e) { System.out.println("Erro ao carregar: " + e.getMessage()); }
    }

    private static void seedDadosExemplo() {
        Filme f1 = new Filme("FIL-001","O Senhor dos Anéis","Fantasia",Duration.ofMinutes(178),14,"","");
        Filme f2 = new Filme("FIL-002","Avatar 2","Aventura",Duration.ofMinutes(192),12,"","");
        gerFilmes.adicionar(f1); gerFilmes.adicionar(f2);
        Sala s1 = new Sala("SAL-01","Sala VIP",5); Sala s2 = new Sala("SAL-02","Sala IMAX",5);
        gerSalas.adicionar(s1); gerSalas.adicionar(s2);
        Sessao se1 = new Sessao("SES-01", f1, s1, LocalDateTime.of(2025,12,10,18,0), 32.0);
        try { gerSessoes.adicionar(se1); } catch(Exception e) {}
        Cliente c1 = new Cliente("CLI-001","João Silva","joao@mail.com", LocalDate.of(1990,4,10));
        clientes.put(c1.getId(), c1);
        try { gerIngressos.vender("ING-001", c1, se1, "S1", LocalDateTime.of(2025,12,1,10,0)); } catch(Exception e) {}
    }
}
