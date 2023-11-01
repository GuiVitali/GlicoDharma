package com.example.glicodharma;

public class MedicaoHelperClass {

    String username, data, hora, tipo, unidadeMedida, valorMedicao, estadoMedicao;

    public MedicaoHelperClass() {
    }

    public MedicaoHelperClass(String username, String data, String hora, String tipo, String unidadeMedida, String valorMedicao, String estadoMedicao) {
        this.username = username;
        this.data = data;
        this.hora = hora;
        this.tipo = tipo;
        this.unidadeMedida = unidadeMedida;
        this.valorMedicao = valorMedicao;
        this.estadoMedicao = estadoMedicao;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getValorMedicao() {
        return valorMedicao;
    }

    public void setValorMedicao(String valorMedicao) {
        this.valorMedicao = valorMedicao;
    }

    public String getEstadoMedicao() {
        return estadoMedicao;
    }

    public void setEstadoMedicao(String estadoMedicao) {
        this.estadoMedicao = estadoMedicao;
    }
}
