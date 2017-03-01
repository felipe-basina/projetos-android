package no.domain.boaviagem.domain;

import java.util.Date;

/**
 * Created by Felipe on 07/06/2016.
 */
public class Viagem {

    private Long id;
    private String destino;
    private Integer tipoViagem;
    private Date dataChegada;
    private Date dataSaida;
    private Double orcamento;
    private Integer quantidadePessoas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Integer getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(Integer tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public Date getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(Date dataChegada) {
        this.dataChegada = dataChegada;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Double getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Double orcamento) {
        this.orcamento = orcamento;
    }

    public Integer getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Viagem viagem = (Viagem) o;

        return id != null ? id.equals(viagem.id) : viagem.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Viagem(Long id, String destino, Integer tipoViagem, Date dataChegada, Date dataSaida, Double orcamento, Integer quantidadePessoas) {
        this.id = id;
        this.destino = destino;
        this.tipoViagem = tipoViagem;
        this.dataChegada = dataChegada;
        this.dataSaida = dataSaida;
        this.orcamento = orcamento;
        this.quantidadePessoas = quantidadePessoas;
    }

    public Viagem() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Viagem{");
        sb.append("id=").append(id);
        sb.append(", destino='").append(destino).append('\'');
        sb.append(", tipoViagem=").append(tipoViagem);
        sb.append(", dataChegada=").append(dataChegada);
        sb.append(", dataSaida=").append(dataSaida);
        sb.append(", orcamento=").append(orcamento);
        sb.append(", quantidadePessoas=").append(quantidadePessoas);
        sb.append('}');
        return sb.toString();
    }
}
