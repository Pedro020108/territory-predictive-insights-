package dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Formato() {
    }

    public static String numero(double valor) {
        return String.format(PT_BR, "%.1f", valor);
    }

    public static String dataHora(LocalDateTime dataHora) {
        return dataHora.format(DATA_HORA);
    }
}
