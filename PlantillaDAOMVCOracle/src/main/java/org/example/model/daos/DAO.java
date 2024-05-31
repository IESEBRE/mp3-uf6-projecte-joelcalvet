package org.example.model.daos;


import org.example.model.entities.Ninja;
import org.example.model.exceptions.DAOException;

import java.util.List;

/**
 * Interfície DAO genèrica per a interactuar amb la BD
 * @param <T> Tipus de l'objecte amb el que interactuarem
 */
public interface DAO <T>{

    T get(Long id) throws DAOException; //Retorna un objecte de tipus T per a interactuar amb la BD

    List<T> getAll() throws DAOException; //Retorna una llista de tipus T per a interactuar amb la BD

    void save(T obj) throws DAOException; //Guarda un objecte de tipus T per a interactuar amb la BD

    void update(Ninja obj) throws DAOException; // Actualitza un objecte de tipus T per a interactuar amb la BD

    void delete(Ninja obj) throws DAOException; //Elimina un objecte de tipus T per a interactuar amb la BD

}
