package itu.station.stock;

import bean.ClassMAPTable;
import itu.station.utils.TimeUtils;

import java.sql.Connection;
import java.sql.Date;

public class MvtStock extends ClassMAPTable {
    String id,id_cuve;
    int id_type_mvt;
    double qte,pu;
    Date daty;
    double etatDeStock;

    public double getEtatDeStock() {
        return etatDeStock;
    }

    public void setEtatDeStock(double etatDeStock) throws Exception {
        if (etatDeStock < 0 ) throw new Exception("Etat de stock négatif");
        this.etatDeStock = etatDeStock;
    }

    @Override
    public String getTuppleID() {
        return "id";
    }

    @Override
    public String getAttributIDName() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_cuve() {
        return id_cuve;
    }

    public void setId_cuve(String id_cuve) throws Exception {
        if (id_cuve.equals("") || id_cuve==null){
            throw new Exception("Id cuve ne doit pas être null");
        }
        this.id_cuve = id_cuve;
    }

    public int getId_type_mvt() {
        return id_type_mvt;
    }

    public void setId_type_mvt(int id_type_mvt) throws Exception {
        if (id_type_mvt!=1 && id_type_mvt !=-1){
            throw new Exception("Veuillez choisir entre entrée et sortie");
        }
        this.id_type_mvt = id_type_mvt;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qte) throws Exception {
        if (qte<=0){
            throw new Exception("Veuillez insérez une quantité positive");
        }
        this.qte = qte;
    }

    public double getPu() {
        return pu;
    }

    public void setPu(double pu) throws Exception {
        if (pu<=0){
            throw new Exception("Il y'a une erreure sur le prix unitaire");
        }
        this.pu = pu;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }
    public void setDaty(String daty) throws Exception {
        if (daty.equals("") || daty==null){
            throw new Exception("Daty est vide");
        }
        this.setDaty(TimeUtils.convertToSqlDate(daty,"eng"));
    }

    public MvtStock() {
        this.setNomTable("stock");
    }
    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("MVT", "GET_SEQ_STOCK");
        this.setId(makePK(c));
    }
}
