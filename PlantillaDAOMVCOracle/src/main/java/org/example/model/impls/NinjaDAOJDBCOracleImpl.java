package org.example.model.impls;

import org.example.model.daos.DAO;
import org.example.model.entities.Ninja;
import org.example.model.exceptions.DAOException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class NinjaDAOJDBCOracleImpl implements DAO<Ninja> {

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/database.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DB_URL = properties.getProperty("url");
        DB_USER = properties.getProperty("username");
        DB_PASSWORD = properties.getProperty("password");
    }

    /**
     * Retorna un objecte Ninja a partir de la seva ID
     *
     * @param id ID del ninja
     * @return Ninja
     */
    @Override
    public Ninja get(Long id) throws DAOException {
        Ninja ninja = null;

        String query = "SELECT * FROM NINJA WHERE ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    // Creem primer l'objecte Ninja sense els poders
                    ninja = new Ninja(rs.getLong("ID"), rs.getString("NOM"), rs.getDouble("ANYS"), rs.getBoolean("VIU"), new TreeSet<Ninja.Poder>());
                    // Assignem els poders a l'objecte Ninja un cop creat
                    ninja.setPoder(getPoders(ninja, id));
                }
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(12);
            }
        }

        return ninja;
    }

    /**
     * Retorna tots els ninjas de la base de dades
     *
     * @return Llista de ninjes
     */
    @Override
    public List<Ninja> getAll() throws DAOException {
        List<Ninja> ninjas = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement("SELECT * FROM NINJA");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ninjas.add(new Ninja(rs.getLong("ID"), rs.getString("NOM"), rs.getDouble("ANYS"), rs.getBoolean("VIU"), new TreeSet<Ninja.Poder>()));
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(11);
            }
        }

        return ninjas;
    }

    /**
     * Retorna els poders d'un ninja a partir de la seva ID
     *
     * @param ninja   Ninja al que pertanyen els poders
     * @param ninjaId ID del ninja
     * @return Conjunt de poders
     */
    public Set<Ninja.Poder> getPoders(Ninja ninja, long ninjaId) throws SQLException, DAOException {
        Set<Ninja.Poder> poders = new TreeSet<>();

        String query = "SELECT * FROM poder WHERE NINJA_ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query)) {

            st.setLong(1, ninjaId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    // Utilitzem l'objecte Ninja existent per crear el poder
                    Ninja.Poder poder = new Ninja.Poder(Ninja.Poder.TipusChakra.valueOf(rs.getString("TIPUS_CHAKRA")), rs.getInt("QUANTITAT_CHAKRA"));
                    poder.setId(rs.getInt("ID"));
                    poders.add(poder);
                }
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(10);
            }
        }

        return poders;
    }

    /**
     * Guarda un ninja a la base de dades
     *
     * @param obj Ninja a guardar
     */
    @Override
    public void save(Ninja obj) throws DAOException {
        String query = "INSERT INTO NINJA(NOM, ANYS, VIU) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query, new String[]{"ID"})) {

            con.setAutoCommit(false);

            st.setString(1, obj.getNom());
            st.setDouble(2, obj.getAnys());
            st.setBoolean(3, obj.isViu());
            st.executeUpdate();

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    obj.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating ninja failed, no ID obtained.");
                }
            }

            con.commit();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(9);
            }
        }
    }

    /**
     * Actualitza un ninja a la base de dades
     *
     * @param obj Ninja a actualitzar
     */
    @Override
    public void update(Ninja obj) throws DAOException {
        if (get(obj.getId()) == null) {
            throw new DAOException(8);
        }

        String query = "UPDATE NINJA SET NOM = ?, ANYS = ?, VIU = ? WHERE ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query)) {

            st.setString(1, obj.getNom());
            st.setDouble(2, obj.getAnys());
            st.setBoolean(3, obj.isViu());
            st.setLong(4, obj.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(8);
            }
        }
    }

    /**
     * Elimina un ninja de la base de dades
     *
     * @param obj Ninja a eliminar
     */
    @Override
    public void delete(Ninja obj) throws DAOException {
        if (get(obj.getId()) == null) {
            throw new DAOException(7);
        }

        String queryNinja = "DELETE FROM NINJA WHERE ID = ?";
        String queryPoder = "DELETE FROM PODER WHERE NINJA_ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            con.setAutoCommit(false); // Desactiva l'enviament automàtic de consultes

            try (PreparedStatement stNinja = con.prepareStatement(queryNinja);
                 PreparedStatement stPoder = con.prepareStatement(queryPoder)) {

                stPoder.setLong(1, obj.getId());
                stPoder.executeUpdate();

                stNinja.setLong(1, obj.getId());
                stNinja.executeUpdate();

                con.commit(); // Envia totes les consultes a la base de dades
            } catch (SQLException e) {
                con.rollback(); // Desfà qualsevol canvi si hi ha hagut un error
                int errorCode = e.getErrorCode();
                if (DAOException.missatges.containsKey(errorCode)) {
                    throw new DAOException(errorCode);
                } else {
                    throw new DAOException(7);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(7);
        }
    }


    /**
     * Guarda un poder a la base de dades
     *
     * @param poder   Poder a guardar
     * @param ninjaId ID del ninja al que pertany el poder
     */
    public void savePoder(Ninja.Poder poder, long ninjaId) throws DAOException {
        if (poder == null || poder.getTipusChakra() == null) {
            throw new IllegalArgumentException("Poder or TipusChakra no pot ser null");
        }

        String sql = "INSERT INTO poder (TIPUS_CHAKRA, QUANTITAT_CHAKRA, NINJA_ID) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, poder.getTipusChakra().name());
            ps.setInt(2, poder.getQuantitatChakra());
            ps.setLong(3, ninjaId);

            ps.executeUpdate();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(6);
            }
        }
    }

    /**
     * Actualitza un poder a la base de dades
     *
     * @param poder   Poder a actualitzar
     * @param ninjaId ID del ninja al que pertany el poder
     */
    public void updatePoder(Ninja.Poder poder, long ninjaId) throws DAOException {
        String query = "UPDATE Poder SET TIPUS_CHAKRA = ?, QUANTITAT_CHAKRA = ? WHERE ID = ? AND NINJA_ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query)) {

            st.setString(1, poder.getTipusChakra().name());
            st.setInt(2, poder.getQuantitatChakra());
            st.setLong(3, poder.getId());
            st.setLong(4, ninjaId);
            st.executeUpdate();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(5);
            }
        }
    }

    /**
     * Elimina un poder de la base de dades
     *
     * @param poder   Poder a eliminar
     * @param ninjaId ID del ninja al que pertany el poder
     */
    public void deletePoder(Ninja.Poder poder, long ninjaId) throws DAOException {
        String query = "DELETE FROM Poder WHERE ID = ? AND NINJA_ID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement st = con.prepareStatement(query)) {

            st.setLong(1, poder.getId());
            st.setLong(2, ninjaId);
            st.executeUpdate();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (DAOException.missatges.containsKey(errorCode)) {
                throw new DAOException(errorCode);
            } else {
                throw new DAOException(4);
            }
        }
    }
}
