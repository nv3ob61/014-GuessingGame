/* 
 * Copyright (C) 2019 Jonsui
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.japo.java.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author nv3ob61
 */
public class Main {

    public static final Scanner SCN
            = new Scanner(System.in, "Windows-1252")
                    .useLocale(Locale.ENGLISH).useDelimiter("\\s+");

    public static int intentos;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //Instanciamos un random tontamente con una seed...
        Random rnd = new Random();

        String[] listaPelis;
        //random que elige la película
        int numRandom;
        //boolean que guarda la llave del asunto.
        boolean partida = true;
        boolean respuestaOk = false;

        String opcion;

        //Vuelca el archivo de las películas, prmero creamos el arrayList
        List<String> listaToArray = new ArrayList<String>();

        try (FileReader fr = new FileReader("peliculas.txt");
                BufferedReader br = new BufferedReader(fr)) {
            boolean lecturaOk = true;
            do {
                String linea = br.readLine();
                if (linea == null) {
                    lecturaOk = false;
                }
                listaToArray.add(linea);
            } while (lecturaOk);
            listaPelis = (String[]) listaToArray.toArray(new String[listaToArray.size()]);
        }

        while (partida) {
            char[] acumulador;
            System.out.println("Iniciamos el juego. Endivina la pel·lícula.");
            numRandom = rnd.nextInt(listaPelis.length);
            //pasa a un array de char la palabra seleccionada por el random
            //de la lista de películas
            String nombre = listaPelis[numRandom];
            char palabraaChar[] = listaPelis[numRandom].toCharArray();
            //Pasamos tooodos los char del array a mayúsculas...
            for (int i = 0; i < palabraaChar.length; i++) {
                palabraaChar[i] = Character.toUpperCase(palabraaChar[i]);
            }
            //total de intentos por palabra, al menos tantas como letras tiene.
            int chancesAcierto = intentosPartida(nombre);
            intentos = chancesAcierto;
            //num de intentos inicial es igual al método intentosPartida
            //array que va cambiando los - por intentos del jugador
            char[] playerIntento = new char[palabraaChar.length];
            //falta array para acumular las letras que ya han salido....
            acumulador = new char[playerIntento.length + 5];

            //bucle por si en la frase hay espacios, para representarlos también
            for (int i = 0; i < palabraaChar.length; i++) {
                if (Character.isWhitespace(palabraaChar[i])) {
                    playerIntento[i] = ' ';
                } else {
                    playerIntento[i] = '_';
                }
            }

            while (!respuestaOk && intentos > -1) {
                int contador = 0;
                //imprime la palabra en '_'
                muestraLineas(playerIntento, palabraaChar);
                System.out.printf("%nTe quedan %d intentos.%n%n",
                        intentos);
                //Entrada de la letra 
                System.out.println("Introduce una letra: ");
                char entrada = SCN.nextLine().charAt(0);
                entrada = Character.toUpperCase(entrada);

                //Condición para salir con el '-'
                if (entrada == '-') {
                    respuestaOk = true;
                    partida = false;
                    System.out.println("SALIDA: Forzada usuario");
                    //Si no ha insertado un '-' para salir... ¿Qué sucede después?
                } else {
                    intentos--;
                    //Filtra solo letras y números.
                    if (Character.isLetterOrDigit(entrada) || entrada == '('
                            || entrada == ')') {
                        if (comprobarIntento(acumulador, entrada) != -1) {
                            System.out.println("ERROR: valor repetido");
                            intentos++;
                        } else {
                            //Si entrada es letra, dígito o () se acumula.
                            acumulador[(acumulador.length - 1)] = entrada;
                            ordenaAcumulador(acumulador);
                            for (int i = 0; i < palabraaChar.length; i++) {
                                if (palabraaChar[i] == entrada) {
                                    playerIntento[i] = entrada;
                                    contador++;
                                }
                            }
                            if (contador >= 1) {
                                intentos++;
                            }
                            if (palabraAcertada(playerIntento)) {
                                respuestaOk = true;
                                System.out.printf("%n%nLa respuesta correcta es: %s%n", nombre);
                                System.out.println("SALIDA: TEST: FELICIDADES!");
                            }
                        }
                    } else {
                        System.out.println("ERROR: ENTRADA no válida");
                    }
                }
            }
            if (intentos < 0) {
                System.out.printf("%n%nLa respuesta correcta ERA: %s%n", nombre);
                System.out.println("SALIDA: TEST: La cagaste");
            }

            do {
                //Reiniciamos a los valores iniciales por si han cambiado arriba.
                partida = true;
                respuestaOk = false;
                System.out.println("¿Desea jugar otra vez? (S/N): ");
                opcion = SCN.nextLine();
                opcion = opcion.toUpperCase();
                if (opcion.equals("N")) {
                    System.out.println("SALIDA: Fin del programa.");
                    partida = false;
                }
            } while (!opcion.equals("S") && !opcion.equals("N"));
//            System.out.println(listaPelis.length);
        }
    }

    public static int comprobarIntento(char[] arreglo, char busqueda) {
        for (int x = 0; x < arreglo.length; x++) {
            if (arreglo[x] == busqueda) {
                return x;
            }
        }
        return -1;
    }

    public static final int intentosPartida(String nombre) {
        //genera el número de intentos a partir de la long del String y la
        // frecuencia de repetición de los char que lo forman.
        //¿Por qué no? - Igual se puede mejorar el método.
        int[] freq = new int[nombre.length()];
        int i, j;
        int count = 0;

        char string[] = nombre.toCharArray();

        for (i = 0; i < nombre.length(); i++) {
            freq[i] = 1;
            for (j = i + 1; j < nombre.length(); j++) {
                if (string[i] == string[j]) {
                    freq[i]++;
                    //Set string[j] a 0 para evitar que imprima un char 
                    string[j] = '0';
                }
            }
        }
//        System.out.println(string.length);  TEST TEST TEST
        for (i = 0; i < freq.length; i++) {
            //cuenta las posiciones distintas del espacio, en blanco, o un 0
            if (string[i] != ' ' && string[i] != '0') {
                count++;
            }
        }
        //Añade 1/4 al total.
        return count + (count / 4);
    }

    public static void muestraLineas(char array[], char original[]) {
        // Pinta las líneas al inicio.... _ _ _ _ _ _ _ _
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        //Salto
        System.out.println();
    }

    public static void ordenaAcumulador(char[] acumula) {
        char alma;

        for (int i = 0; i < acumula.length - 1; i++) {
            for (int j = i + 1; j < acumula.length; j++) {
                if (acumula[i] < acumula[j]) {
                    alma = acumula[i];
                    acumula[i] = acumula[j];
                    acumula[j] = alma;
                }
            }
        }
    }

    public static boolean palabraAcertada(char[] array) {
        boolean condition = true;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '_') {
                condition = false;
            }
        }
        return condition;
    }
}
