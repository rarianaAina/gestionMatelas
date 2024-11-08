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
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vente.Vente;

public class Produit extends ClassMAPTable {
    private String id;
    private String val;
    private String desce;
    private String idTypeProduit;
    private double puAchat;
    private double puVente;
    private String idUnite;
    private String idCategorie;
    private String idSousCategorie;
    private String presentation;
    private double seuilMin;
    private double seuilMax;
    private double puAchatUSD;
    private double puAchatEuro;
    private double puAchatAutreDevise;
    private double puVenteUSD;
    private double puVenteEuro;
    private double puVenteAutreDevise;
    private int isAchat;
    private int isVente;

    public int getQte_stock() {
        return qte_stock;
    }

    public void setQte_stock(int qte_stock) {
        this.qte_stock = qte_stock;
    }

    public int getQte_vendue() {
        return qte_vendue;
    }

    public void setQte_vendue(int qte_vendue) {
        this.qte_vendue = qte_vendue;
    }

    private int qte_stock;

    private int qte_vendue;

    @Override
    public String getTuppleID() {
        return "id";
    }

    @Override
    public String getAttributIDName() {
        return id;
    }

    public Produit() {
        this.setNomTable("produit");
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getDesce() {
        return desce;
    }

    public void setDesce(String desce) {
        this.desce = desce;
    }

    public String getIdTypeProduit() {
        return idTypeProduit;
    }

    public void setIdTypeProduit(String idTypeProduit) {
        this.idTypeProduit = idTypeProduit;
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

    public String getIdUnite() {
        return idUnite;
    }

    public void setIdUnite(String idUnite) {
        this.idUnite = idUnite;
    }

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getIdSousCategorie() {
        return idSousCategorie;
    }

    public void setIdSousCategorie(String idSousCategorie) {
        this.idSousCategorie = idSousCategorie;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public double getSeuilMin() {
        return seuilMin;
    }

    public void setSeuilMin(double seuilMin) {
        this.seuilMin = seuilMin;
    }

    public double getSeuilMax() {
        return seuilMax;
    }

    public void setSeuilMax(double seuilMax) {
        this.seuilMax = seuilMax;
    }

    public double getPuAchatUSD() {
        return puAchatUSD;
    }

    public void setPuAchatUSD(double puAchatUSD) {
        this.puAchatUSD = puAchatUSD;
    }

    public double getPuAchatEuro() {
        return puAchatEuro;
    }

    public void setPuAchatEuro(double puAchatEuro) {
        this.puAchatEuro = puAchatEuro;
    }

    public double getPuAchatAutreDevise() {
        return puAchatAutreDevise;
    }

    public void setPuAchatAutreDevise(double puAchatAutreDevise) {
        this.puAchatAutreDevise = puAchatAutreDevise;
    }

    public double getPuVenteUSD() {
        return puVenteUSD;
    }

    public void setPuVenteUSD(double puVenteUSD) {
        this.puVenteUSD = puVenteUSD;
    }

    public double getPuVenteEuro() {
        return puVenteEuro;
    }

    public void setPuVenteEuro(double puVenteEuro) {
        this.puVenteEuro = puVenteEuro;
    }

    public double getPuVenteAutreDevise() {
        return puVenteAutreDevise;
    }

    public void setPuVenteAutreDevise(double puVenteAutreDevise) {
        this.puVenteAutreDevise = puVenteAutreDevise;
    }

    public int getIsAchat() {
        return isAchat;
    }

    public void setIsAchat(int isAchat) {
        this.isAchat = isAchat;
    }

    public int getIsVente() {
        return isVente;
    }

    public void setIsVente(int isVente) {
        this.isVente = isVente;
    }



    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("PR", "GET_SEQ_PRODUIT");
        this.setId(makePK(c));
    }

    public Produit[] getAllProduits(Connection c) throws Exception {
        // Obtenir la connexion via UtilDB
        if (c == null) {
            c = new UtilDB().GetConn("gallois", "gallois");
        }
        String apresWhere = "and idTypeProduit = 'MG0001'";
        // Récupérer tous les produits en utilisant une condition vide
        return (Produit[]) CGenUtil.rechercher(new Produit(), null, null, c, apresWhere);
    }

    public String produitsToJson(Connection c) throws Exception {
        Produit[] produitsRecus = getAllProduits(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(produitsRecus);
    }


}