package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Departement extends ClassMAPTable {
    private String idDepartement;
    private String nomDepartement;

    @Override
    public String getTuppleID() {
        return "idDepartement"; // Nom de l'attribut ID
    }

    @Override
    public String getAttributIDName() {
        return idDepartement; // Valeur de l'attribut ID
    }

    public String getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(String idDepartement) {
        this.idDepartement = idDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    public Departement() {
        this.setNomTable("departement"); // Nom de la table correspondante
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("DE", "GET_SEQ_DEPARTEMENT"); // Méthode pour préparer la clé primaire
        this.setIdDepartement(makePK(c)); // Génération de l'ID
    }

    // Exemple d'une méthode pour obtenir tous les départements
    public Departement[] getAllDepartements(Connection c) throws Exception {
        if (c == null) c = new UtilDB().GetConn(); // Obtention de la connexion

        Departement[] departements = (Departement[]) CGenUtil.rechercher(new Departement(), null, null, c, "");
        return departements; // Retourne la liste des départements
    }

    public String departementsToJson(Connection c) throws Exception {
        Departement[] departementsRecus = getAllDepartements(c); // Récupère les départements

        Gson gson = new GsonBuilder().create();
        return gson.toJson(departementsRecus); // Conversion en JSON
    }
}
