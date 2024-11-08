package itu.station.stock.achat;

import itu.station.utils.TimeUtils;

import java.sql.Date;

public class AchatArgs {
    String id_cuve;
    double qte;
    Date daty;
//    String heure;

    public String getId_cuve() {
        return id_cuve;
    }

    public void setId_cuve(String id_cuve) {
        this.id_cuve = id_cuve;
    }


    public double getQte() {
        return qte;
    }

    public void setQte(double qte) {
        this.qte = qte;
    }
    public void setQte(String qte) throws Exception {
        if (qte.isEmpty() || qte == null){
            throw new Exception("Remplissez le champ quantité");
        }
        this.qte = Double.parseDouble(qte);
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }
    public void setDaty(String daty) throws Exception {
        if (daty.isEmpty() || daty == null){
            throw new Exception("Remplissez le champ Daty");
        }
        this.daty = TimeUtils.convertToSqlDate(daty,"eng");
    }

}
