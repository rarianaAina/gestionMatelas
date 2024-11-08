package itu.station.stock;

import itu.station.utils.TimeUtils;

import java.sql.Date;

public class MvtDTO {
    String id_cuve;
    double qte;
    int type_mvt;
    Date daty;

    public String getId_cuve() {
        return id_cuve;
    }

    public void setId_cuve(String id_cuve) throws Exception {
        if (id_cuve.equals("")) throw new Exception("Séléctionnez une cuve");
        this.id_cuve = id_cuve;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(String qte) throws Exception{
        if (qte.equals("") || qte==null)throw new Exception("Remplissez le champ quantité");
        this.setQte(Double.parseDouble(qte));
    }
    public void setQte(double qte) throws Exception{
        if (qte <= 0 ) throw new Exception("Choisissez une quantité positive");
        this.qte = qte;
    }

    public int getType_mvt() {
        return type_mvt;
    }

    public void setType_mvt(int type_mvt) throws Exception {
        if (type_mvt!=1 && type_mvt !=-1) throw new Exception("Veuillez choisir entre entrée et sortie");
        this.type_mvt = type_mvt;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }
    public void setDaty(String daty) throws Exception {
        if (daty.equals("") || daty==null)throw new Exception("Daty est vide");
        this.setDaty(TimeUtils.convertToSqlDate(daty,"eng"));
    }

    public MvtDTO(String id_cuve, double qte, int type_mvt ,Date daty) throws Exception {
        this.setId_cuve(id_cuve);
        this.setQte(qte);
        this.setType_mvt(type_mvt);
        this.setDaty(daty);
    }
    public MvtDTO(String id_cuve, String qte, int type_mvt ,String daty) throws Exception {
        this.setId_cuve(id_cuve);
        this.setQte(qte);
        this.setType_mvt(type_mvt);
        this.setDaty(daty);
    }

    public MvtDTO() {
    }
}
