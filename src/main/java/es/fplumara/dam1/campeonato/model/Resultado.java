package es.fplumara.dam1.campeonato.model;

import java.util.Objects;

public class Resultado implements Puntuable{

    // Atributos
    private String id;
    private String idPrueba;
    private TipoPrueba tipoPrueba;
    private String idDeportista;
    private int posicion;

    // Getters

    public String getId() {
        return id;
    }

    public String getIdPrueba() {
        return idPrueba;
    }

    public TipoPrueba getTipoPrueba() {
        return tipoPrueba;
    }

    public String getIdDeportista() {
        return idDeportista;
    }

    public int getPosicion() {
        return posicion;
    }


    @Override
    public int getPuntos() {
        // CON IF:
        if (posicion == 1) return 5;
        if (posicion == 2) return 3;
        if (posicion == 3) return 1;
        return 0;

        // SWITCH
        // return switch (posicion){
        //    case 1 -> 5;
        //    case 2 -> 3;
        //    case 3 -> 1;
        //    default -> 0; };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resultado resultado)) return false;
        return Objects.equals(id, resultado.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
