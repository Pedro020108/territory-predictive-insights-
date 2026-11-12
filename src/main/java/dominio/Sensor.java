package dominio;

import excecao.FonteIndisponivelException;


public interface Sensor {

    double lerValor() throws FonteIndisponivelException;

    boolean verificarFuncionamento();

    String getTipo();

    String getUnidade();

    double getValorMaximo();

    void setFuncionando(boolean funcionando);
}
