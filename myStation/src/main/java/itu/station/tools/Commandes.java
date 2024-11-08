package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class Commandes extends ClassMAPTable {

    private int idCommande;
    private int etatCommande;
    private int qte;
    private String produit;
    private Date dateCommande;

    // Constructeur avec paramètres
    public Commandes(int idCommande, int etatCommande, int qte, String produit, Date dateCommande) {
        this.idCommande = idCommande;
        this.etatCommande = etatCommande;
        this.qte = qte;
        this.produit = produit;
        this.dateCommande = dateCommande;
    }

    // Constructeur par défaut
    public Commandes() {
        this.setNomTable("COMMANDES");
    }

    // Override pour retourner l'ID de l'enregistrement
    @Override
    public String getTuppleID() {
        return String.valueOf(idCommande);
    }

    // Override pour retourner le nom de l'attribut ID
    @Override
    public String getAttributIDName() {
        return "idCommande";
    }

    // Getters et setters

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getEtatCommande() {
        return etatCommande;
    }

    public void setEtatCommande(int etatCommande) {
        this.etatCommande = etatCommande;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    // Méthode pour obtenir toutes les Commandes
    public Commandes[] getAllCommandes(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        // Récupérer toutes les commandes avec une condition vide
        return (Commandes[]) CGenUtil.rechercher(new Commandes(), null, null, c, "");
    }

    // Méthode pour convertir la liste des Commandes en JSON
    public String commandesToJson(Connection c) throws Exception {
        Commandes[] commandesRecues = getAllCommandes(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(commandesRecues);
    }

    public static String toJson(Commandes[] commandes) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(commandes);
    }
}
