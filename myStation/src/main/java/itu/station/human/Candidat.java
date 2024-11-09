package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Candidat extends ClassMAPTable {

    private int idCandidat;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String motDePasse;
    private String cvUrl;

    // Constructeur avec paramètres
    public Candidat(int idCandidat, String nom, String prenom, String email, String telephone, String motDePasse, String cvUrl) {
        this.idCandidat = idCandidat;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.cvUrl = cvUrl;
    }

    public int getIdCandidat() {
        return idCandidat;
    }

    public void setIdCandidat(int idCandidat) {
        this.idCandidat = idCandidat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    // Constructeur par défaut
    public Candidat() {
        this.setNomTable("CANDIDAT");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idCandidat);
    }

    @Override
    public String getAttributIDName() {
        return "idCandidat";
    }

    // Getters et setters

    // Méthode pour obtenir tous les Candidats
    public Candidat[] getAllCandidats(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (Candidat[]) CGenUtil.rechercher(new Candidat(), null, null, c, "");
    }

    public String candidatsToJson(Connection c) throws Exception {
        Candidat[] candidatsRecus = getAllCandidats(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(candidatsRecus);
    }
}
