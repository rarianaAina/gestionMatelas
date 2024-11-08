package itu.station.tools;

import bean.ClassMAPTable;

import java.sql.Connection;

public class Pompiste extends ClassMAPTable {
    String id,nom;
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
        this.preparePk("USR", "GET_SEQ_POMPISTE");
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

    public Pompiste() {
        this.setNomTable("POMPISTE");
    }
}
