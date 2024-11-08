package itu.station.ejbService;

import avoir.AvoirFC;
import caisse.MvtCaisse;
import itu.station.finance.StatutFinancier;
import itu.station.prelevement.Avoir;
import itu.station.prelevement.Encaissement;
import itu.station.prelevement.Prelevement;
import prelevement.PrelevementCpl;
import vente.Vente;

import javax.ejb.Local;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;

@Local
public interface EjbStation2 {
    public StatutFinancier getStatutFinancier(Date dateMin, Date dateMax, Connection connection) throws Exception;
    public StatutFinancier getStatutFinancier(Date dateMin, Date dateMax);
    public AvoirFC genererAvoir(Avoir avoir, Connection connection) throws Exception;
    public MvtCaisse mvtCaisse(Encaissement encaissement) throws Exception;
    public PrelevementCpl[] getPrelevements() throws NamingException, Exception;

}
