package br.com.diversos.exemplos.restauranteapp.modelo;

/**
 * Created by Felipe on 20/02/2017.
 */

public class Restaurante {

    private String nome;

    private String endereco;

    private String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Restaurante{");
        sb.append("nome='").append(nome).append('\'');
        sb.append(", endereco='").append(endereco).append('\'');
        sb.append(", tipo='").append(tipo).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
