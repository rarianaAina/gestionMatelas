package itu.station.tools;

import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {


    private int idEmployee;
    private String nom;
    private String mot_de_passe;
    private String role;
    private String departement;
    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getUsername() {
        return nom;
    }

    public void setUsername(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return mot_de_passe;
    }

    public void setPassword(String password) {
        this.mot_de_passe = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }
    public User findByUsername(String username, Connection connection) throws SQLException {

        connection = new UtilDB().GetConn("gestion", "gestion");
        String query = "SELECT * FROM USERS WHERE nom = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString("nom"));
            user.setPassword(resultSet.getString("mot_de_passe"));
            user.setRole(resultSet.getString("role"));
            return user;
        } else {
            return null; // L'utilisateur n'existe pas
        }
    }
}

