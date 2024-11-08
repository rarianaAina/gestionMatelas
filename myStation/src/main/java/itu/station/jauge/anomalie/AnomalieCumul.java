/*
package itu.station.jauge.anomalie;

import bean.CGenUtil;
import itu.station.prelevement.PrelevementQuantityCpl;
import itu.station.tools.Cuve;
import jauge.Jauge;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AnomalieCumul {
    Anomalie[] anomalies;
    Date limit;
    Cuve cuve;
    String idCuve;
    double cumul;
    // SELECT * FROM jauge WHERE daty <= limit and idCuve =  ORDER by daty ASC;
    // SELECT * FROM prelevementQty WHERE daty <= limit AND idCuve =  ORDER by daty ASC;
    //Anomalie a = new Ameloi();
    //a.setJauge(jauge[i])
    //a.setPrelevementQty(getPrelevementWhereDatyEgaleHierDeJauge(jauge[i].getDaty().yesterday()))

    public Anomalie[] getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(Anomalie[] anomalies) {
        this.anomalies = anomalies;
    }

    public Date getLimit() {
        return limit;
    }

    public void setLimit(Date limit) {
        this.limit = limit;
    }

    public Cuve getCuve() {
        return cuve;
    }

    public void setCuve(Cuve cuve) {
        this.cuve = cuve;
    }

    public String getIdCuve() {
        return idCuve;
    }

    public void setIdCuve(String idCuve) {
        this.idCuve = idCuve;
    }

    public double getCumul() {
        return cumul;
    }

    public void setCumul(double cumul) {
        this.cumul = cumul;
    }

    public AnomalieCumul(String idCuve,Date date,Connection gallois,Connection tsotra) throws Exception {
        this.setIdCuve(idCuve);
        this.setLimit(date);
        initAttributes(gallois,tsotra);
    }

    public void initAttributes(Connection connection, Connection tsotra) throws Exception {
//        Connection connection = new UtilDB().GetConn("gallois","gallois");
//        Connection tsotra = new UtilDB().GetConn();
        Jauge[] jauges = (Jauge[]) CGenUtil.rechercher(new Jauge(),null,null,connection," and idMagasin='"+idCuve+"' and daty <=  TO_DATE('"+limit.toString()+"','YYYY-MM-dd') ORDER BY daty DESC");
        PrelevementQuantityCpl[] prelevementQuantityCpls = (PrelevementQuantityCpl[]) CGenUtil.rechercher(new PrelevementQuantityCpl(),null,null,tsotra," and id_cuve='"+idCuve+"' and daty <=  TO_DATE('"+limit.toString()+"','YYYY-MM-dd') ORDER BY daty DESC");
        List<Anomalie> anomaliesList = new ArrayList<>();
        for (int i = 0; i < jauges.length-1; i++) {
            Anomalie temp = new Anomalie();
            temp.setIdCuve(jauges[i].getIdMagasin());
            temp.setDate(jauges[i].getDaty());
            temp.setJauge(jauges[i+1]);
            temp.setPrelevementQuantityCpl(getPrelByDaty(jauges[i+1].getDaty(),prelevementQuantityCpls));
            this.cumul += temp.getTheoricalQuantity() - jauges[i].getQte();
            anomaliesList.add(temp);
        }
        anomalies = anomaliesList.toArray(new Anomalie[0]);
//        connection.close();
//        tsotra.close();
    }
    public PrelevementQuantityCpl[] getPrelByDaty(Date daty,PrelevementQuantityCpl[] prelevementQuantityCpls){
        List<PrelevementQuantityCpl> prelevementQuantityCpls1 = new ArrayList<>();
        for (int i = 0; i < prelevementQuantityCpls.length; i++) {
            if (prelevementQuantityCpls[i].getDaty().compareTo(daty) == 0){
                prelevementQuantityCpls1.add(prelevementQuantityCpls[i]);
            }
        }
        return prelevementQuantityCpls1.toArray(new PrelevementQuantityCpl[0]);
    }
}
*/
