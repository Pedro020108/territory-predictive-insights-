package servico;

import dominio.DadosTerritorio;
import dominio.Pluviometro;
import dominio.SensorNivelAgua;
import dominio.SensorUmidadeSolo;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PADRÃO SINGLETON: existe uma única instância guardando os limiares de todos os sensores.
 * O construtor é privado; todo mundo pega a instância por getInstancia().
 * Assim, se um limiar muda no menu, o serviço de alertas enxerga a mudança na hora.
 */
public final class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private final Map<String, DadosTerritorio> limiaresPorSensor = new LinkedHashMap<>();

    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    /** Volta todos os limiares para os valores iniciais (exemplos configuráveis, não valores oficiais). */
    public synchronized void restaurarPadroes() {
        try {
            limiaresPorSensor.clear();
            limiaresPorSensor.put(Pluviometro.TIPO,
                    new DadosTerritorio(Pluviometro.TIPO, "mm", 15.0, 35.0, 55.0));
            limiaresPorSensor.put(SensorNivelAgua.TIPO,
                    new DadosTerritorio(SensorNivelAgua.TIPO, "cm", 10.0, 25.0, 40.0));
            limiaresPorSensor.put(SensorUmidadeSolo.TIPO,
                    new DadosTerritorio(SensorUmidadeSolo.TIPO, "%", 60.0, 80.0, 95.0));
        } catch (DadosInvalidosException e) {
            throw new IllegalStateException("Os limiares padrão do sistema estão inválidos: " + e.getMessage(), e);
        }
    }

    public synchronized DadosTerritorio getDados(String tipoSensor) throws DadosInvalidosException {
        DadosTerritorio dados = limiaresPorSensor.get(tipoSensor);
        if (dados == null) {
            throw new DadosInvalidosException("Não há limiares configurados para o sensor '" + tipoSensor + "'.");
        }
        return dados;
    }

    public synchronized Collection<DadosTerritorio> listarDados() {
        return new ArrayList<>(limiaresPorSensor.values());
    }
}
