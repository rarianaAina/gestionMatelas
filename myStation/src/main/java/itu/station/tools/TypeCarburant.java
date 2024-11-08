package itu.station.tools;

import bean.ClassMAPTable;

import java.sql.Connection;

public class TypeCarburant extends ClassMAPTable {
    String id,libelle,id_unite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getId_unite() {
        return id_unite;
    }

    public void setId_unite(String id_unite) {
        this.id_unite = id_unite;
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
        this.preparePk("TC", "GET_SEQ_TYPE_CARBURANT");
        String pk = makePK(c);
        System.out.println("PRIMARY KEY:::"+pk);
        this.setId(pk);
    }
    public TypeCarburant() {

        this.setNomTable("type_carburant");
    }
}
