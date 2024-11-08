package itu.station.tools;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.io.StringWriter;
import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vente.VenteDetails;

public class Vente extends ClassMAPTable {
    private String id;
    private String designation;
    private String idMagasin;
    private Date daty;
    private String remarque;
    private int etat;
    private String idOrigine;
    private String idClient;
    private int estPrevu;
    private Date datyPrevu;

    public VenteDetails getVenteDetails() {
        return venteDetails;
    }

    public void setVenteDetails(VenteDetails venteDetails) {
        this.venteDetails = venteDetails;
    }

    private VenteDetails venteDetails;
    // Constructeur
    public Vente() {
        this.setNomTable("vente");
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getIdMagasin() {
        return idMagasin;
    }

    public void setIdMagasin(String idMagasin) {
        this.idMagasin = idMagasin;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getIdOrigine() {
        return idOrigine;
    }

    public void setIdOrigine(String idOrigine) {
        this.idOrigine = idOrigine;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public int getEstPrevu() {
        return estPrevu;
    }

    public void setEstPrevu(int estPrevu) {
        this.estPrevu = estPrevu;
    }

    public Date getDatyPrevu() {
        return datyPrevu;
    }

    public void setDatyPrevu(Date datyPrevu) {
        this.datyPrevu = datyPrevu;
    }

    @Override
    public String getTuppleID() {
        return "id";
    }

    @Override
    public String getAttributIDName() {
        return id;
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("VNT", "getSeqVente");
        this.setId(makePK(c));
    }


    // Méthode pour obtenir toutes les ventes
    public Vente[] getAllVentes(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gallois", "gallois");
        }

        // Journaliser la connexion
        System.out.println("Connexion établie : " + (c != null));

        // Obtenir toutes les ventes
        Vente[] ventes = (Vente[]) CGenUtil.rechercher(new Vente(), null, null, c, "");

        // Journaliser le nombre de ventes récupérées
        if (ventes != null) {
            System.out.println("Nombre de ventes récupérées : " + ventes.length);
        } else {
            System.out.println("Aucune vente trouvée.");
        }

        return ventes;
    }
    public Vente[] getVentesByDate(Connection c, Date date) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gallois", "gallois");
        }

        // Date spécifique à rechercher


        // Utiliser le paramètre apresWhere pour filtrer par date
        String apresWhere = "and daty = TO_DATE('" + date + "', 'YYYY-MM-DD')";

        // Journaliser la connexion
        System.out.println("Connexion établie : " + (c != null));

        // Obtenir toutes les ventes
        Vente[] ventes = (Vente[]) CGenUtil.rechercher(new Vente(), null, null, c, apresWhere);

        // Journaliser le nombre de ventes récupérées
        if (ventes != null) {
            System.out.println("Nombre de ventes récupérées : " + ventes.length);
        } else {
            System.out.println("Aucune vente trouvée.");
        }

        return ventes;
    }




    // Méthode pour convertir les ventes en JSON
    public String ventesToJson(Connection c) throws Exception {
        Vente[] ventesRecues = getAllVentes(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(ventesRecues);
    }



}
