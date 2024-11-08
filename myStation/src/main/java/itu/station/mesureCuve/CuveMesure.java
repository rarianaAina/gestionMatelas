package itu.station.mesureCuve;

import bean.ClassMAPTable;

public class CuveMesure extends ClassMAPTable {
    String id;
    String idCuve;
    double mesure;
    double qteLitre;
    String idUm;

    public CuveMesure() {
        this.setNomTable("CUVE_MESURES");
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCuve() {
        return idCuve;
    }

    public void setIdCuve(String idCuve) {
        this.idCuve = idCuve;
    }

    public double getMesure() {
        return mesure;
    }

    public void setMesure(double mesure) {
        this.mesure = mesure;
    }

    public double getQteLitre() {
        return qteLitre;
    }

    public void setQteLitre(double qteLitre) {
        this.qteLitre = qteLitre;
    }

    public String getIdUm() {
        return idUm;
    }

    public void setIdUm(String idUm) {
        this.idUm = idUm;
    }
}
