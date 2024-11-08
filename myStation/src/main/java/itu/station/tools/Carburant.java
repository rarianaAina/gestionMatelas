package itu.station.tools;

import bean.ClassMAPTable;

import java.sql.Connection;

public class Carburant extends ClassMAPTable {
    String id,nom,desce;
    double pu_achat,pu_vente;
    String id_type_carburant;

    public Carburant() {
        this.setNomTable("carburant");
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
        this.preparePk("CARB", "GET_SEQ_CARBURANT");
        this.setId(makePK(c));
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDesce() {
        return desce;
    }

    public void setDesce(String desce) {
        this.desce = desce;
    }

    public double getPu_achat() {
        return pu_achat;
    }

    public void setPu_achat(double pu_achat) {
        this.pu_achat = pu_achat;
    }

    public double getPu_vente() {
        return pu_vente;
    }

    public void setPu_vente(double pu_vente) {
        this.pu_vente = pu_vente;
    }

    public String getId_type_carburant() {
        return id_type_carburant;
    }

    public void setId_type_carburant(String id_type_carburant) {
        this.id_type_carburant = id_type_carburant;
    }
}
