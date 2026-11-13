# Territory Predictive Insights

Sistema de monitoramento territorial desenvolvido em Java (console), que utiliza dados de sensores para armazenar medições, gerar previsões e emitir alertas sobre possíveis riscos de alagamento.

**Global Solution 2º/2026 – OOP: Territory (CitizenTech)**

## Integrantes

| Nome | RM |
|---|---|
| Pedro Mirabella Tonarque | 563419 |
| Guilherme Mota Melo | 565887 |
| Pedro Henrique Oliveira Marques | 563054 |

**Repositório:** https://github.com/Pedro020108/territory-predictive-insights- 

## 1. Problema e público beneficiado

**Problema escolhido:** chuvas intensas e risco de alagamento em regiões vulneráveis.

O problema afeta principalmente moradores de áreas baixas, próximas a córregos e galerias pluviais. Também pode dificultar o trabalho das equipes comunitárias e da Defesa Civil, que precisam se preparar antes que a situação se agrave.

A proposta do sistema é ajudar a responder à seguinte pergunta: *o que podemos fazer hoje para diminuir os impactos das chuvas nos próximos dias?*

Com base nas leituras dos sensores, o sistema gera previsões e alertas que podem ajudar na preparação das equipes, na identificação de áreas de risco e na adoção de medidas preventivas.

O sistema funciona como uma ferramenta de apoio. A decisão final continua sendo responsabilidade das pessoas envolvidas.

Para o projeto, utilizamos uma região fictícia chamada **Bairro Baixada do Rio**. Não utilizamos dados pessoais, como nomes, CPF ou endereços.

## 2. Tecnologias utilizadas

- Java 17
- Maven
- JDBC
- H2 Database

## 3. Como executar o projeto

**Pré-requisito:** ter o JDK 17 ou superior instalado.

Não é necessário instalar um banco de dados separadamente, pois o H2 é configurado pelo Maven e criado automaticamente na primeira execução.

**Pelo IntelliJ:**

1. Acessar `File > Open` e selecionar o arquivo `pom.xml`.
2. Aguardar o Maven baixar as dependências. Se necessário, clicar em *Reload All Maven Projects*.
3. Abrir `src/main/java/app/SistemaMonitoramento.java`.
4. Executar o método `main` pelo botão verde do IntelliJ.

**Pelo terminal:**

```bash
mvn clean test
mvn compile exec:java
```

O primeiro comando executa os testes e o segundo inicia o sistema.

**Banco de dados:**

O projeto utiliza o H2 em arquivo, localizado em `./dados/territorio.mv.db`. O banco e as tabelas `MEDICAO` e `ALERTA` são criados automaticamente na primeira execução.

Como os dados ficam salvos no arquivo, o histórico continua disponível mesmo depois de fechar e abrir o programa novamente.

Para apagar o histórico e começar novamente, basta excluir a pasta `dados/`.

**Código de acesso:** `1234`.

Esse código é solicitado para alterar os limites dos alertas pelo menu 6, opção 2.

> Caso os acentos apareçam incorretamente no console, execute a JVM com `-Dfile.encoding=UTF-8`.

### Roteiro para testar o sistema

1. Acessar o menu 2 e realizar algumas leituras, de preferência umas 10 vezes.
2. Acessar o menu 3 e selecionar a opção para executar todas as estratégias de previsão.
3. Acessar o menu 5 para consultar os alertas.
4. Acessar o menu 4 para consultar o histórico e testar a exportação para CSV.
5. Acessar o menu 6 para testar algumas situações, como medição negativa, valor fora da faixa, código incorreto e sensor com defeito.
6. Fechar o programa e abrir novamente para conferir se o histórico continua salvo.

## 4. Estrutura do projeto

```text
src/main/java
├── dominio/
│   ├── ComponenteTerritorial
│   ├── RegiaoMonitorada
│   ├── Sensor e suas 3 implementações
│   ├── Medicao
│   ├── Alerta
│   ├── NivelAlerta
│   ├── DadosTerritorio
│   ├── EstrategiaPrevisao
│   ├── PrevisaoMediaMovel
│   ├── PrevisaoComparacao
│   └── Formato
│
├── excecao/
│   ├── DadosInvalidosException
│   └── FonteIndisponivelException
│
├── persistencia/
│   ├── Conexao
│   ├── MedicaoDAO
│   └── AlertaDAO
│
├── servico/
│   ├── ServicoMonitoramento
│   ├── FabricaPrevisao
│   ├── ConfiguracaoSistema
│   └── ResultadoPrevisao
│
└── app/
    └── SistemaMonitoramento

src/test/java/testes
└── Testes JUnit 5 e Mockito (37 testes)
```

## 5. Conceitos de OOP utilizados

Durante o desenvolvimento, utilizamos os principais conceitos de programação orientada a objetos:

- **Abstração e herança:** criamos a classe abstrata `ComponenteTerritorial`, que possui o método abstrato `verificarStatus()`. A classe `RegiaoMonitorada` herda dela. Também utilizamos a classe abstrata `EstrategiaPrevisao`, que é herdada por `PrevisaoMediaMovel` e `PrevisaoComparacao`.

- **Interface e polimorfismo:** criamos a interface `Sensor`, com três implementações: `Pluviometro` (mm), `SensorNivelAgua` (cm) e `SensorUmidadeSolo` (%). Assim, o serviço consegue trabalhar com diferentes sensores utilizando a mesma interface, sem precisar verificar o tipo de cada objeto com `if` ou `instanceof`.

- **Encapsulamento:** os atributos das classes são privados. A classe `DadosTerritorio` controla as alterações dos limites de alerta pelo método `alterarLimiares(codigo, ...)`, que exige um código de acesso e também verifica se os valores seguem a ordem ATENÇÃO menor que ALERTA e ALERTA menor que CRÍTICO.

- **Exceções personalizadas:** criamos `DadosInvalidosException` para tratar situações como valores negativos, dados fora da faixa permitida, população negativa, limites inválidos, código incorreto e histórico insuficiente. Também criamos `FonteIndisponivelException` para situações como sensores indisponíveis e problemas de acesso ao banco ou arquivo. Essas exceções são tratadas no menu com mensagens explicando o problema.

- **Persistência com JDBC:** utilizamos as classes `MedicaoDAO` e `AlertaDAO` para salvar e consultar os dados no banco H2. As operações utilizam `PreparedStatement` e `try-with-resources`. As medições e os alertas ficam registrados no banco, e as previsões são calculadas utilizando o histórico armazenado.

- **Testes automatizados:** desenvolvemos 37 testes com JUnit 5 e Mockito, utilizando o padrão AAA (Arrange, Act, Assert). Também usamos `assertThrows` para verificar as exceções. Na classe `ServicoMonitoramentoTest`, utilizamos Mockito para simular os DAOs e testar o serviço sem precisar acessar o banco de dados.

## 6. Padrões criacionais

Utilizamos dois padrões criacionais no projeto: Factory e Singleton.

**Factory – `FabricaPrevisao`**

Utilizamos a Factory para criar os diferentes tipos de previsão. Como o sistema possui mais de uma estratégia, a fábrica recebe a opção escolhida e retorna o objeto correspondente.

Por exemplo, o menu pode solicitar uma previsão do tipo `"MEDIA"` ou `"COMPARACAO"`, e a fábrica fica responsável por criar a estratégia correta.

Dessa forma, evitamos deixar toda a lógica de criação espalhada pelo sistema. Caso seja necessário adicionar uma nova estratégia, podemos incluí-la na fábrica sem precisar alterar o funcionamento do serviço e do menu.

**Singleton – `ConfiguracaoSistema`**

Utilizamos o Singleton para manter uma única configuração dos limites de alerta durante a execução do programa.

Isso é importante porque, quando um limite é alterado pelo menu, o restante do sistema precisa utilizar o mesmo valor atualizado.

Para isso, a classe possui um construtor privado e o método `getInstancia()`, que permite acessar a única instância da configuração.

Não utilizamos Singleton nos DAOs ou no serviço, pois eles não precisam compartilhar esse tipo de estado e também devem continuar podendo ser substituídos por mocks nos testes.

## 7. Reflexão sobre o projeto

**Quando o sistema não deve gerar uma previsão?**

Quando não existem leituras suficientes para fazer um cálculo confiável. A média móvel precisa de pelo menos 5 leituras armazenadas no banco, enquanto a comparação precisa de 10 (5 anteriores e 5 atuais).

Se não houver essa quantidade de dados, o sistema informa que ainda não é possível gerar uma previsão ou alerta. Preferimos não apresentar um resultado baseado em poucas medições, pois ele poderia passar uma informação incorreta.

O mesmo acontece com leituras inválidas ou sensores indisponíveis. Esses dados não são utilizados no histórico.

**Quais são as limitações dos dados utilizados?**

As leituras do projeto são simuladas. Mesmo utilizando sensores reais, apenas um sensor por região não seria suficiente para representar todas as condições de um bairro, já que a quantidade de chuva pode variar bastante entre ruas próximas.

Além disso, os cálculos utilizados não consideram fatores como previsão meteorológica, relevo, drenagem e capacidade de absorção do solo.

Os limites de alerta também são exemplos configuráveis e não representam valores oficiais da Defesa Civil. As previsões utilizam os dados anteriores como referência, sem fazer uma análise completa das condições climáticas.

**Por que o sistema não toma a decisão sozinho?**

Porque os dados dos sensores não mostram tudo o que acontece em uma região. As equipes responsáveis conhecem outros fatores importantes, como o histórico de alagamentos, as condições das ruas, as obras e as rotas de evacuação.

Por isso, o sistema apresenta o sensor utilizado, o valor previsto, o limite atingido e uma orientação. Dessa forma, quem recebe o alerta consegue entender de onde veio aquela informação e avaliar se precisa tomar alguma medida.

O objetivo é ajudar na tomada de decisão, e não substituir a avaliação humana.

## 8. Uso de inteligência artificial

Durante o desenvolvimento, utilizamos o Claude para ajudar a corrigir erros de compilação e execução que apareceram no projeto. Também utilizamos a ferramenta para elaborar o README.

Depois, o texto foi revisado pelo grupo e ajustado com as informações reais do projeto.

## 9. Divisão das tarefas

| Integrante     | Responsabilidades | Commit |
|----------------|---|---|
| Pedro Henrique | `pom.xml`, `.gitignore`, pacotes `excecao/` e `dominio/`, incluindo entidades, sensores e estratégias de previsão. | `feat: domínio e exceções` |
| Guilherme Mota | Pacote `persistencia/`, incluindo `Conexao`, `MedicaoDAO`, `AlertaDAO` e criação das tabelas no H2. | `feat: persistência JDBC/H2` |
| Pedro Tonarque | Pacotes `servico/` e `app/`, testes automatizados e README. | `feat: serviço, menu e testes` |

## 10. Funcionalidade extra

**Exportação do histórico para CSV**

Além das funcionalidades principais, o sistema permite exportar o histórico de medições para arquivos CSV pelo menu 4.

Os arquivos são salvos na pasta `dados/`, com o nome `historico_<sensor>.csv` e utilizam `;` como separador.