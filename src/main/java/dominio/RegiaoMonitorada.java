package dominio;

import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RegiaoMonitorada extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();
    private int populacaoEstimada;

    public RegiaoMonitorada(int id, String nome, String regiao, int populacaoEstimada)
            throws DadosInvalidosException {
        super(id, nome, regiao);
        setPopulacaoEstimada(populacaoEstimada);
    }

    public void adicionarSensor(Sensor sensor) throws DadosInvalidosException {
        if (sensor == null) {
            throw new DadosInvalidosException("Não é possível adicionar um sensor nulo à região.");
        }
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    public int getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    public void setPopulacaoEstimada(int populacaoEstimada) throws DadosInvalidosException {
        if (populacaoEstimada < 0) {
            throw new DadosInvalidosException("A população estimada não pode ser negativa: " + populacaoEstimada);
        }
        this.populacaoEstimada = populacaoEstimada;
    }

    @Override
    public String verificarStatus() {
        long ativos = sensores.stream().filter(Sensor::verificarFuncionamento).count();
        int total = sensores.size();

        String situacao;
        if (total == 0) {
            situacao = "SEM SENSORES";
        } else if (ativos == total) {
            situacao = "OPERACIONAL";
        } else if (ativos == 0) {
            situacao = "FORA DO AR";
        } else {
            situacao = "DEGRADADO";
        }

        setStatus(situacao + " (" + ativos + "/" + total + " sensores ativos)");
        return getStatus();
    }
}
