package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class Proformats extends ClassMAPTable {

    private int idProformat;
    private int EtatProformat;
    private int QteDemande;
    private String Produit;



    private String PrixProduit;

    private Date dateDemande;

    // Constructeur avec paramètres
    public Proformats(int idProformat, int EtatProformat, int QteDemande, String Produit, Date dateDemande, String PrixProduit) {
        this.idProformat = idProformat;
        this.EtatProformat = EtatProformat;
        this.QteDemande = QteDemande;
        this.Produit = Produit;
        this.dateDemande = dateDemande;
        this.PrixProduit = PrixProduit;
    }

    // Constructeur par défaut
    public Proformats() {
        this.setNomTable("PROFORMATS");
    }

    // Override pour retourner l'ID de l'enregistrement
    @Override
    public String getTuppleID() {
        return String.valueOf(idProformat);
    }

    // Override pour retourner le nom de l'attribut ID
    @Override
    public String getAttributIDName() {
        return "idProformat";
    }

    // Getters et setters

    public int getIdProformat() {
        return idProformat;
    }

    public void setIdProformat(int idProformat) {
        this.idProformat = idProformat;
    }

    public int getEtatProformat() {
        return EtatProformat;
    }

    public void setEtatProformat(int EtatProformat) {
        this.EtatProformat = EtatProformat;
    }

    public int getQteDemande() {
        return QteDemande;
    }

    public void setQteDemande(int QteDemande) {
        this.QteDemande = QteDemande;
    }

    public String getProduit() {
        return Produit;
    }

    public void setProduit(String Produit) {
        this.Produit = Produit;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getPrixProduit() {
        return PrixProduit;
    }

    public void setPrixProduit(String prixProduit) {
        PrixProduit = prixProduit;
    }
    // Méthode pour obtenir tous les Proformats
    public Proformats[] getAllProformats(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        // Récupérer tous les proformats avec une condition vide
        return (Proformats[]) CGenUtil.rechercher(new Proformats(), null, null, c, "");
    }
    public Proformats[] getProformatsByDate(String date, Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        String apresWhere = "and dateProformat = TO_DATE('" + date + "', 'YYYY-MM-DD')";
        // Récupérer tous les proformats avec une condition vide
        return (Proformats[]) CGenUtil.rechercher(new Proformats(), null, null, c, apresWhere);
    }

    // Méthode pour convertir la liste des Proformats en JSON
    public String proformatsToJson(Connection c) throws Exception {
        Proformats[] proformatsRecus = getAllProformats(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(proformatsRecus);
    }

    public static String toJson(Proformats[] proformats) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(proformats);
    }
}
