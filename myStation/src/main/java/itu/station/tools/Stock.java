package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Stock extends ClassMAPTable {

    private int idStock;
    private String NOM_PRODUIT;
    private String DESCRIPTION;
    private int QTE;

    public Stock(int idStock, String NOM_PRODUIT, String DESCRIPTION, int QTE) {
        this.idStock = idStock;
        this.NOM_PRODUIT = NOM_PRODUIT;
        this.DESCRIPTION = DESCRIPTION;
        this.QTE = QTE;
    }

    public Stock() {
        this.setNomTable("stock");
    }

    @Override
    public String getTuppleID() {
        return null;
    }

    @Override
    public String getAttributIDName() {
        return null;
    }

    // Getters et setters

    public int getIdStock() {
        return idStock;
    }

    public void setIdStock(int idStock) {
        this.idStock = idStock;
    }

    public String getNOM_PRODUIT() {
        return NOM_PRODUIT;
    }

    public void setNOM_PRODUIT(String NOM_PRODUIT) {
        this.NOM_PRODUIT = NOM_PRODUIT;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public int getQTE() {
        return QTE;
    }

    public void setQTE(int QTE) {
        this.QTE = QTE;
    }

    public Stock[] getAllStock(Connection c) throws Exception {
        // Obtenir la connexion via UtilDB
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        /*String apresWhere = "and idTypeProduit = 'MG0001'";*/
        // Récupérer tous les produits en utilisant une condition vide
        return (Stock[]) CGenUtil.rechercher(new Stock(), null, null, c, "");
    }

    public String stockToJson(Connection c) throws Exception {
        Stock[] stocksRecus = getAllStock(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(stocksRecus);
    }
}