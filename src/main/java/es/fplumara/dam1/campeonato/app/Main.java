
package es.fplumara.dam1.campeonato.app;

import es.fplumara.dam1.campeonato.exception.FicheroInvalidoException;
import es.fplumara.dam1.campeonato.io.*;
import es.fplumara.dam1.campeonato.model.*;
import es.fplumara.dam1.campeonato.repository.DeportistaRepositoryImpl;
import es.fplumara.dam1.campeonato.repository.ResultadoRepositoryImpl;
import es.fplumara.dam1.campeonato.service.CampeonatoService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Main de ejemplo para demostrar el flujo mínimo del examen (sin menú complejo).
 * Debe leer ficheros de entrada y escribir un fichero de salida.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Examen DAM1 - Campeonato deportivo (Java 21)");

    // 0) Definimos rutas fijas para los ficheros (carpeta data/)
        Path deportistasCsv = Path.of("data", "deportistas.csv");
        Path resultadosCsv  = Path.of("data", "resultados.csv");
        Path rankingCsvOut  = Path.of("data", "ranking.csv"

                /*
         * FLUJO MÍNIMO (lo que debe hacer tu main)
         *
         * 1) Crear repositorios en memoria
         *    - DeportistaRepositoryImpl
         *    - ResultadoRepositoryImpl
         */
        DeportistaRepositoryImpl deportistaRepo = new DeportistaRepositoryImpl();
        ResultadoRepositoryImpl resultadoRepo  = new ResultadoRepositoryImpl();




        /* 2) Crear el servicio
         *    - CampeonatoService (usa ambos repositorios)
         *
         * 3) Leer datos de ficheros (CSV recomendado)
         *    - Leer "deportistas.csv" y por cada línea crear Deportista y llamar a registrarDeportista(...)
         *    - Leer "resultados.csv" y por cada línea crear Resultado (incluyendo tipoPrueba como enum) y llamar a registrarResultado(...)
         *
         * 4) Mostrar por consola
         *    - Países participantes (Set)
         *    - Ranking (List ordenada por puntos)
         *    - Resultados de un país (List filtrada)
         *
         * 5) Escribir salida a fichero
         *    - Generar el ranking y escribir "ranking.csv" con: idDeportista,nombre,pais,puntos
         */
    }
}
