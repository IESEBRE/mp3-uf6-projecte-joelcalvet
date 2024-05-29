package org.example.model.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepció personalitzada per a la capa de persistència
 */
public class DAOException extends Exception{

    public static final Map<Integer, String> missatges = new HashMap<>();
    //num i retorna string, el map
    static {
        missatges.put(0, "Error al connectar a la BD!!");
        missatges.put(1, "Restricció d'integritat violada - clau primària duplicada");
        missatges.put(3, "Has d'introduir un any vàlid (entre 0 i 99)");
        missatges.put(4, "Ha hagut algun problema al borrar un poder a la BD");
        missatges.put(5, "Ha hagut algun problema al actualitzar un poder a la BD");
        missatges.put(6, "Ha hagut algun problema al inserir un poder a la BD");
        missatges.put(7, "Ha hagut algun problema al borrar un ninja amb els seus poders associats, a la BD");
        missatges.put(8, "Ha hagut algun problema al actualitzar un ninja a la BD");
        missatges.put(9, "Ha hagut algun problema al inserir un ninja a la BD");
        missatges.put(10, "No s'han pogut obtenir els poders del ninja de la BD");
        missatges.put(11, "No s'han pogut obtenir els ninjes de la BD");
        missatges.put(12, "No s'ha pogut obtenir el ninja de la BD");
        missatges.put(13, "Ningun camp del ninja pot estar buit");
        missatges.put(14, "El nom del ninja no pot tindre mes de 20 caracters");
        missatges.put(15, "El nom del ninja no pot contenir caracters especials");
        missatges.put(16, "Els anys del ninja tenen que ser un valor numeric");
        missatges.put(17, "El nom del poder no pot tindre mes de 20 caracters");
        missatges.put(18, "La quantitat de chakra del poder ha de ser un valor numeric");
        missatges.put(19, "La quantitat de chakra del poder ha de ser d'entre 0 i 10000");
        missatges.put(20, "No pots introduir un poder amb el mateix nom que un altre, per a un ninja diferent");
        missatges.put(21, "El camp de la quantitat de chakra no pot estar buit");
        missatges.put(22, "Poder no trobat");
        missatges.put(23, "Cap poder seleccionat per modificar");
        missatges.put(24, "Cap Ninja seleccionat per modificar");
        missatges.put(904, "Nom de columna no vàlid");
        missatges.put(936, "Falta expressió en l'ordre SQL");
        missatges.put(942, "La taula o la vista no existeix");
        missatges.put(1000, "S'ha superat el nombre màxim de cursors oberts");
        missatges.put(1400, "Inserció de valor nul en una columna que no permet nuls");
        missatges.put(1403, "No s'ha trobat cap dada");
        missatges.put(1722, "Ha fallat la conversió d'una cadena de caràcters a un número");
        missatges.put(1747, "El nombre de columnes de la vista no coincideix amb el nombre de columnes de les taules subjacents");
        missatges.put(4091, "Modificació d'un procediment o funció en execució actualment");
        missatges.put(6502, "Error numèric o de valor durant l'execució del programa");
        missatges.put(12154, "No s'ha pogut resoldre el nom del servei de la base de dades Oracle o l'identificador de connexió");
        missatges.put(2291, "S'ha violat la restricció d'integritat - no s'ha trobat el registre pare");
        missatges.put(1407, "Inserció de valor nul en una columna que requereix un valor");
        missatges.put(12899, "S'ha superat la longitud màxima per a una columna");
        missatges.put(2290, "S'ha violat una restricció de verificació");
        missatges.put(2292, "S'ha violat la restricció d'integritat - s'ha trobat un registre fill");
        missatges.put(17008, "Connexió tancada");
        missatges.put(17002, "No s'ha pogut establir la connexió");
    }

    //atribut
    private int tipo;

    //constructor al que pasem el tipus
    public DAOException(int tipo){
        this.tipo=tipo;
    }

    //sobreescrivim el get message
        @Override
    public String getMessage(){
        return missatges.get(this.tipo); //el missatge del tipo
    }

    public int getTipo() {
        return tipo;
    }
}
