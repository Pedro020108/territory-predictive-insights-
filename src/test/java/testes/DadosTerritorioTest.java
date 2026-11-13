package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.DadosTerritorio;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

    private DadosTerritorio criarChuva() throws DadosInvalidosException {
        return new DadosTerritorio("PLUVIOMETRO", "mm", 15.0, 35.0, 55.0);
    }

    @Test
    void deveClassificarCadaNivelDeAlerta() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio chuva = criarChuva();

        // Act
        Optional<NivelAlerta> abaixo = chuva.classificar(10.0);
        Optional<NivelAlerta> atencao = chuva.classificar(15.0);
        Optional<NivelAlerta> alerta = chuva.classificar(35.0);
        Optional<NivelAlerta> critico = chuva.classificar(60.0);

        // Assert
        assertEquals(Optional.empty(), abaixo);
        assertEquals(Optional.of(NivelAlerta.ATENCAO), atencao);
        assertEquals(Optional.of(NivelAlerta.ALERTA), alerta);
        assertEquals(Optional.of(NivelAlerta.CRITICO), critico);
    }

    @Test
    void deveRecusarLimiarComAlertaMenorQueAtencao() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio("PLUVIOMETRO", "mm", 30.0, 20.0, 55.0));
    }

    @Test
    void deveRecusarLimiarZeroOuNegativo() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio("PLUVIOMETRO", "mm", -5.0, 20.0, 55.0));
    }

    @Test
    void deveRecusarAlteracaoComCodigoDeAcessoErrado() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio chuva = criarChuva();

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> chuva.alterarLimiares("0000", 10.0, 20.0, 30.0));

        // Assert: o limiar antigo continua valendo
        assertEquals(15.0, chuva.getLimiteAtencao(), 0.0001);
        assertEquals(true, erro.getMessage().contains("Código de acesso"));
    }

    @Test
    void deveAlterarLimiaresComCodigoCorreto() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio chuva = criarChuva();

        // Act
        chuva.alterarLimiares("1234", 10.0, 20.0, 30.0);

        // Assert
        assertEquals(10.0, chuva.getLimiteAtencao(), 0.0001);
        assertEquals(20.0, chuva.getLimiteAlerta(), 0.0001);
        assertEquals(30.0, chuva.getLimiteCritico(), 0.0001);
    }
}
