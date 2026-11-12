package dominio;


public enum NivelAlerta {

    ATENCAO("ATENÇÃO", "Acompanhar as leituras de perto nas próximas horas."),
    ALERTA("ALERTA", "Preparar as equipes e avisar os pontos vulneráveis do território."),
    CRITICO("CRÍTICO", "Sugere-se acionar o protocolo de emergência, se a coordenação responsável confirmar.");

    private final String rotulo;
    private final String orientacao;

    NivelAlerta(String rotulo, String orientacao) {
        this.rotulo = rotulo;
        this.orientacao = orientacao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getOrientacao() {
        return orientacao;
    }
}
