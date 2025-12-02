package persistence;

import java.util.List;
import domain.Filme;
import domain.Sala;
import domain.Cliente;

public class DataSnapshot {
    public List<Filme> filmes;
    public List<Sala> salas;
    public List<Cliente> clientes;
    public List<SessaoDTO> sessoes;
    public List<IngressoDTO> ingressos;
}
