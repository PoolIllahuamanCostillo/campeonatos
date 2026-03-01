package es.fplumara.dam1.campeonato.model;

public class LineaRanking {

    //Atributos
    private String idDeportista;
    private String nombre;
    private String pais;
    private int puntos;

    // Constructor:
    public LineaRanking(String idDeportista, String nombre, String pais, int puntos) {
        this.idDeportista = idDeportista;
        this.nombre = nombre;
        this.pais = pais;
        this.puntos = puntos;
    }


    // Getters:
    public String getIdDeportista() {
        return idDeportista;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public int getPuntos() {
        return puntos;
    }

    @Override
    public String toString() {
        return "LineaRanking{" +
                "idDeportista='" + idDeportista + '\'' +
                ", nombre='" + nombre + '\'' +
                ", pais='" + pais + '\'' +
                ", puntos=" + puntos +
                '}';
    }
}
