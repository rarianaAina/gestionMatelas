package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Competence extends ClassMAPTable {

    private int idCompetence;
    private String nom;
    private String description;

    // Constructeur avec paramètres
    public Competence(int idCompetence, String nom, String description) {
        this.idCompetence = idCompetence;
        this.nom = nom;
        this.description = description;
    }

    public int getIdCompetence() {
        return idCompetence;
    }

    public void setIdCompetence(int idCompetence) {
        this.idCompetence = idCompetence;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Constructeur par défaut
    public Competence() {
        this.setNomTable("COMPETENCE");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idCompetence);
    }

    @Override
    public String getAttributIDName() {
        return "idCompetence";
    }

    // Getters et setters

    // Méthode pour obtenir toutes les Compétences
    public Competence[] getAllCompetences(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (Competence[]) CGenUtil.rechercher(new Competence(), null, null, c, "");
    }

    public String competencesToJson(Connection c) throws Exception {
        Competence[] competencesRecus = getAllCompetences(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(competencesRecus);
    }
}
