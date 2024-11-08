package itu.station.jauge.anomalie;


import bean.CGenUtil;
import itu.station.prelevement.Prelevement;
import itu.station.prelevement.PrelevementCuve;
import itu.station.prelevement.PrelevementQuantity;
import itu.station.prelevement.PrelevementQuantityCpl;
import itu.station.tools.Cuve;
import itu.station.utils.TimeUtils;
import jauge.Jauge;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Anomalie {
    String idCuve;
    Cuve cuve;
    Date date;
    Jauge jauge;
    PrelevementQuantityCpl[] prelevementQuantityCpls;

    public Jauge getJauge() {
        return jauge;
    }

    public void setJauge(Jauge jauge) {
        this.jauge = jauge;
    }

    public Anomalie() {
    }

    public PrelevementQuantityCpl[] getPrelevementQuantityCpl() {
        return prelevementQuantityCpls;
    }

    public void setPrelevementQuantityCpl(PrelevementQuantityCpl[] prelevementQuantityCpls) {
        this.prelevementQuantityCpls = prelevementQuantityCpls;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(String date) throws Exception {
        if (date.isEmpty()) throw new Exception("Le champ date est nulll");
        this.date = TimeUtils.convertToSqlDate(date,"eng");
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getIdCuve() {
        return idCuve;
    }

    public void setIdCuve(String idCuve) {
        this.idCuve = idCuve;
    }

    public Cuve getCuve() {
        return cuve;
    }

    public void setCuve(Cuve cuve) {
        this.cuve = cuve;
    }

    public Anomalie(String idCuve,String daty,Connection gallois,Connection myStation) throws Exception {
        this.setIdCuve(idCuve);
        this.setDate(daty);
        getJaugeBy(idCuve,this.getDate(),gallois);
        getCuveBy(idCuve,myStation);
        getPrelevementCuves(idCuve,this.getDate(),myStation);
    }

    public Anomalie(String idCuve,Date daty,Connection gallois,Connection myStation) throws Exception {
        this.setIdCuve(idCuve);
        this.setDate(daty);
        getJaugeBy(idCuve,this.getDate(),gallois);
        getCuveBy(idCuve,myStation);
        getPrelevementCuves(idCuve,this.getDate(),myStation);
    }
    public Jauge getJaugeBy(String idCuve,Date date,Connection connection) throws Exception {
        Jauge[] jaugeList = (Jauge[]) CGenUtil.rechercher(new Jauge(),null,null,connection," and idMagasin = '"+idCuve+"' and daty = TO_DATE('"+date.toString()+"','YYYY-MM-dd')");
        this.setJauge(null);
        if (jaugeList.length != 0) this.setJauge(jaugeList[0]);;
        return this.getJauge();
    }
    public Cuve getCuveBy(String idCuve,Connection connection) throws Exception {
        Cuve[] cuveList = (Cuve[]) CGenUtil.rechercher(new Cuve(),null,null,connection," and id = '"+idCuve+"'");
        if (cuveList.length == 0) throw new Exception("Pas de cuve");
        this.setCuve(cuveList[0]);
        return this.getCuve();
    }
    public PrelevementQuantityCpl[] getPrelevementCuves(String idCuve, Date date, Connection connection) throws Exception {
        PrelevementQuantityCpl[] prelevementCuves = (PrelevementQuantityCpl[]) CGenUtil.rechercher(new PrelevementQuantityCpl(),null,null,connection," and id_cuve = '"+idCuve+"' and daty = TO_DATE('"+date.toString()+"','YYYY-MM-dd')");
//        if (prelevementCuves.length == 0) throw new Exception("Aucun prélévement à cette date");
        this.setPrelevementQuantityCpl(prelevementCuves);
        return this.getPrelevementQuantityCpl();
    }
    public double sommerPrelevement(){
        double sum = 0;
        for (int i = 0; i < this.getPrelevementQuantityCpl().length; i++) {
            sum += this.getPrelevementQuantityCpl()[i].getQty();
        }
        return sum;
    }
    public double getTheoricalQuantity(){
        if (this.getJauge() != null) return this.getJauge().getQte()-this.sommerPrelevement();
        if (this.getJauge() == null) return 0-this.sommerPrelevement();
        return 0;
    }
    /*
    *Jauge aujourd'hui .
    * Prélevement aujourd'hui => qte demain = jauge ajd - prel ajd
    */
    public Anomalie getRecentAnomalie(Connection gallois,Connection tsotra) throws Exception {
        return new Anomalie(this.getIdCuve(),TimeUtils.getYesterday(this.getDate()),gallois,tsotra);
    }
}
