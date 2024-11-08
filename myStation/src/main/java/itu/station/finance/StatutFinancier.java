package itu.station.finance;

import ejbServer.GeneralEJBLocalServer;
import finance.EtatDeFinance;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.utils.TimeUtils;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;

public class StatutFinancier {
    EtatDeFinance etatDeFinance;

    public EtatDeFinance getEtatDeFinance() {
        return etatDeFinance;
    }

    public void setEtatDeFinance(EtatDeFinance etatDeFinance) {
        this.etatDeFinance = etatDeFinance;
    }

    public EtatDeFinance getEtatDeFinance(Date dateMin, Date dateMax, Connection connection) throws Exception {
        GeneralEJBLocalServer generalEJBLocalServer = EjbClientGetter.getGeneralEjbService();
        EtatDeFinance etatDeFinance1 = generalEJBLocalServer.getEtatDeFinance(dateMin, dateMax, connection);
        this.setEtatDeFinance(etatDeFinance1);
        return this.getEtatDeFinance();
    }
    public EtatDeFinance getEtatDeFinance(String dateMin, String dateMax, Connection connection) throws Exception {
        return getEtatDeFinance(TimeUtils.convertToSqlDate(dateMin,"eng"),TimeUtils.convertToSqlDate(dateMax,"eng"),connection);
    }
}
