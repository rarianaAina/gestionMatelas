package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Departements extends ClassMAPTable {


    private int idDepartement;
    private String nom;
    private int responsableId;

    // Constructeur avec paramètres
    public Departements(int idDepartement, String nom, int responsableId) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.responsableId = responsableId;
    }

    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
    }

    // Constructeur par défaut
    public Departements() {
        this.setNomTable("DEPARTEMENTS");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idDepartement);
    }

    @Override
    public String getAttributIDName() {
        return "idDepartement";
    }

    // Getters et setters

    // Méthode pour obtenir tous les Departements
    public Departements[] getAllDepartements(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (Departements[]) CGenUtil.rechercher(new Departements(), null, null, c, "");
    }

    public String departementsToJson(Connection c) throws Exception {
        Departements[] departementsRecus = getAllDepartements(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(departementsRecus);
    }
}
