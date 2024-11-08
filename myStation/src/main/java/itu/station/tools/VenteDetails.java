package itu.station.tools;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.io.StringWriter;
import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VenteDetails extends ClassMAPTable {
    private String id;
    private String idVente;
    private String idProduit;
    private String idOrigine;
    private int qte;
    private double pu; // Corrigé en double pour correspondre à la définition PU NUMBER(30,2)
    private double remise;
    private double tva; // Changement de nom pour correspondre aux conventions Java
    private double puAchat;
    private double puVente;
    private String idDevise;
    private double tauxDeChange;
    private String designation;
    private String compte;

    // Constructeur
    public VenteDetails() {
        this.setNomTable("vente_details");
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdVente() {
        return idVente;
    }

    public void setIdVente(String idVente) {
        this.idVente = idVente;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(String idProduit) {
        this.idProduit = idProduit;
    }

    public String getIdOrigine() {
        return idOrigine;
    }

    public void setIdOrigine(String idOrigine) {
        this.idOrigine = idOrigine;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public double getPu() {
        return pu;
    }

    public void setPu(double pu) {
        this.pu = pu;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getTva() {
        return tva;
    }

    public void setTva(double tva) {
        this.tva = tva;
    }

    public double getPuAchat() {
        return puAchat;
    }

    public void setPuAchat(double puAchat) {
        this.puAchat = puAchat;
    }

    public double getPuVente() {
        return puVente;
    }

    public void setPuVente(double puVente) {
        this.puVente = puVente;
    }

    public String getIdDevise() {
        return idDevise;
    }

    public void setIdDevise(String idDevise) {
        this.idDevise = idDevise;
    }

    public double getTauxDeChange() {
        return tauxDeChange;
    }

    public void setTauxDeChange(double tauxDeChange) {
        this.tauxDeChange = tauxDeChange;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    // Méthodes héritées
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
        this.preparePk("VDT", "getSeqVenteDetails");
        this.setId(makePK(c));
    }

    // Méthode pour obtenir toutes les ventes
    public VenteDetails[] getAllVentesDetails(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gallois", "gallois");
        }
        return (VenteDetails[]) CGenUtil.rechercher(new VenteDetails(), null, null, c, "");
    }
    // Méthode pour convertir les ventes en JSON
    public String ventesDetailsToJson(Connection c) throws Exception {
        VenteDetails[] ventesRecues = getAllVentesDetails(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(ventesRecues);
    }


}
