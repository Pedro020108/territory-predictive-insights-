package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * PADRÃO FACTORY: a criação das estratégias de previsão fica concentrada aqui.
 * O restante do sistema pede "MEDIA" ou "COMPARACAO" e recebe o objeto pronto,
 * sem if/instanceof espalhado. Para criar uma nova estratégia, basta registrá-la no catálogo.
 */
public final class FabricaPrevisao {

    public static final String MEDIA = "MEDIA";
    public static final String COMPARACAO = "COMPARACAO";

    private static final Map<String, Supplier<EstrategiaPrevisao>> CATALOGO = new LinkedHashMap<>();

    static {
        CATALOGO.put(MEDIA, PrevisaoMediaMovel::new);
        CATALOGO.put(COMPARACAO, PrevisaoComparacao::new);
    }

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(String tipo) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> criador = (tipo == null) ? null : CATALOGO.get(tipo.trim().toUpperCase());
        if (criador == null) {
            throw new DadosInvalidosException(
                    "Tipo de previsão desconhecido: '" + tipo + "'. Opções disponíveis: " + CATALOGO.keySet());
        }
        return criador.get();
    }

    public static List<String> tiposDisponiveis() {
        return new ArrayList<>(CATALOGO.keySet());
    }
}
