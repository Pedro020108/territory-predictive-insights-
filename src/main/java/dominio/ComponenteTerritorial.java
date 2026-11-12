package dominio;

import excecao.DadosInvalidosException;


public abstract class ComponenteTerritorial {

    private int id;
    private String nome;
    private String regiao;
    private String status;

    protected ComponenteTerritorial(int id, String nome, String regiao) throws DadosInvalidosException {
        if (id < 0) {
            throw new DadosInvalidosException("O id do componente não pode ser negativo: " + id);
        }
        this.id = id;
        setNome(nome);
        setRegiao(regiao);
        this.status = "NÃO VERIFICADO";
    }

    public abstract String verificarStatus();

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do componente não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do componente não pode ser vazia.");
        }
        this.regiao = regiao.trim();
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return nome + " (" + regiao + ") - " + status;
    }
}
