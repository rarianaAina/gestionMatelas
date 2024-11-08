package itu.station.ejbService;

import avoir.AvoirFC;
import caisse.MvtCaisse;
import ejbServer.GeneralEJBLocalServer;
import itu.station.finance.StatutFinancier;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.prelevement.Avoir;
import itu.station.prelevement.Encaissement;
import itu.station.prelevement.Prelevement;
import prelevement.PrelevementCpl;
import utilitaire.UtilDB;
import vente.Vente;

import javax.ejb.Stateless;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;
@Stateless
public class EjbStationService implements EjbStation2{
    @Override
    public StatutFinancier getStatutFinancier(Date dateMin, Date dateMax, Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn("gallois","gallois");
        StatutFinancier statutFinancier = new StatutFinancier();
        statutFinancier.getEtatDeFinance(dateMin,dateMax,connection);
        return null;
    }

    @Override
    public StatutFinancier getStatutFinancier(Date dateMin, Date dateMax) {
        return null;
    }

    @Override
    public AvoirFC genererAvoir(Avoir avoir, Connection connection) throws Exception {
        boolean estOuvert = false;
        try{
            if (connection == null) {connection = new UtilDB().GetConn("gallois","gallois");estOuvert=true;}
            return avoir.genererAvoir("1060",connection);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (estOuvert) connection.close();
        }
    }

    @Override
    public MvtCaisse mvtCaisse(Encaissement encaissement) throws Exception {
        return encaissement.makeMovement(null);
    }

    @Override
    public PrelevementCpl[] getPrelevements() throws Exception{
        GeneralEJBLocalServer generalEJBLocalServer = EjbClientGetter.getGeneralEjbService();
        return generalEJBLocalServer.getAllPrelevementCpl(null);
    }
}
