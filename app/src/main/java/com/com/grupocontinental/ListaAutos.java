package com.com.grupocontinental;

import androidx.appcompat.app.AppCompatActivity;

public class ListaAutos extends AppCompatActivity {
    private String id,marca,modelo,precio,link,foto_1,foto_2,foto_3;

    public ListaAutos(String id, String marca, String modelo, String precio, String link, String foto_1, String foto_2, String foto_3) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.precio = precio;
        this.link = link;
        this.foto_1 = foto_1;
        this.foto_2 = foto_2;
        this.foto_3 = foto_3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFoto_1() {
        return foto_1;
    }

    public void setFoto_1(String foto_1) {
        this.foto_1 = foto_1;
    }

    public String getFoto_2() {
        return foto_2;
    }

    public void setFoto_2(String foto_2) {
        this.foto_2 = foto_2;
    }

    public String getFoto_3() {
        return foto_3;
    }

    public void setFoto_3(String foto_3) {
        this.foto_3 = foto_3;
    }
}
