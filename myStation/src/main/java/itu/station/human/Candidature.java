package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class Candidature extends ClassMAPTable {

    private int idCandidature;
    private int idOffre;
    private int idCandidat;
    private Date dateCandidature;
    private String statut;

    // Constructeur avec paramètres
    public Candidature(int idCandidature, int idOffre, int idCandidat, Date dateCandidature, String statut) {
        this.idCandidature = idCandidature;
        this.idOffre = idOffre;
        this.idCandidat = idCandidat;
        this.dateCandidature = dateCandidature;
        this.statut = statut;
    }

    public int getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public int getIdCandidat() {
        return idCandidat;
    }

    public void setIdCandidat(int idCandidat) {
        this.idCandidat = idCandidat;
    }

    public Date getDateCandidature() {
        return dateCandidature;
    }

    public void setDateCandidature(Date dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Constructeur par défaut
    public Candidature() {
        this.setNomTable("CANDIDATURE");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idCandidature);
    }

    @Override
    public String getAttributIDName() {
        return "idCandidature";
    }

    // Getters et setters

    // Méthode pour obtenir toutes les Candidatures
    public Candidature[] getAllCandidatures(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (Candidature[]) CGenUtil.rechercher(new Candidature(), null, null, c, "");
    }

    public String candidaturesToJson(Connection c) throws Exception {
        Candidature[] candidaturesRecues = getAllCandidatures(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(candidaturesRecues);
    }
}
