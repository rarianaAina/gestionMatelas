package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Cuve extends ClassMAPTable {
    String id;
    double max;
    String id_carb,label;
//    Pompe[] pompes;
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

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getId_carb() {
        return id_carb;
    }

    public void setId_carb(String id_carb) {
        this.id_carb = id_carb;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Cuve() {
        this.setNomTable("cuve");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CV", "GET_SEQ_CUVE");
        this.setId(makePK(c));
    }


    public Carburant getCarburantDetails(Connection c) throws Exception {
        if (c == null) c=new UtilDB().GetConn();
        return ((Carburant[]) CGenUtil.rechercher(new Carburant(),null,null," and id = '"+this.getId_carb()+"'"))[0];
    }
}
