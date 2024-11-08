package itu.station.jauge;

import itu.station.utils.TimeUtils;

import java.sql.Date;

public class JaugeArgs {
    String idCuve;
    Date daty;
    double mesure;

    public String getIdCuve() {
        return idCuve;
    }

    public void setIdCuve(String idCuve) throws Exception {
        if (idCuve.isEmpty()) throw new Exception("Choose a cuve");
        this.idCuve = idCuve;
    }

    public Date getDaty() {
        return daty;
    }
    public void setDaty(String daty) throws Exception {
        if (daty.isEmpty()) throw new Exception("Daty is null");
        this.setDaty(TimeUtils.convertToSqlDate(daty,"eng"));
    }
    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public double getMesure() {
        return mesure;
    }
    public void setMesure(String mesure) throws Exception {
        if (mesure.isEmpty()) throw new Exception("Mesure is null");
        this.setMesure(Double.parseDouble(mesure));
    }
    public void setMesure(double mesure) throws Exception {
        if (mesure <= 0) throw new Exception("Mesure should be greater than 0");
        this.mesure = mesure;
    }

    public JaugeArgs(String idCuve, Date daty, double mesure) throws Exception {
        this.setIdCuve(idCuve);
        this.setDaty(daty);
        this.setMesure(mesure);
    }
    public JaugeArgs(String idCuve, String daty, String mesure) throws Exception {
        this.setIdCuve(idCuve);
        this.setDaty(daty);
        this.setMesure(mesure);
    }
}
