package itu.station.prelevement;

import bean.CGenUtil;
import caisse.MvtCaisse;
import ejbServer.GeneralEJBLocalServer;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.utils.TimeUtils;
import utilitaire.UtilDB;
import vente.Vente;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;

public class Encaissement {
    FactureClient factureClient;
    Date date;

    public FactureClient getFactureClient() {
        return factureClient;
    }

    public void setFactureClient(FactureClient factureClient) {
        this.factureClient = factureClient;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setDate(String date) throws Exception {
        if (date == null ||date.equals("")){
            throw new Exception("La date est vide");
        }
        this.date = TimeUtils.convertToSqlDate(date,"eng");
    }

    public boolean estEncaisse(Connection connection) throws Exception {
        MvtCaisse[] mvtCaisses = (MvtCaisse[]) CGenUtil.rechercher(new MvtCaisse(),null,null,connection," and idOrigine='"+this.getFactureClient().getPrelevement().getId()+"'");
        return mvtCaisses.length > 0;
    }

    public MvtCaisse makeMovement(Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn("gallois","gallois");
        if (estEncaisse(connection)) throw new Exception("Prélevement déja encaissé");
        try{
            GeneralEJBLocalServer generalEJBLocalServer = EjbClientGetter.getGeneralEjbService();
            Vente vente = this.getFactureClient().getPrelevement().viser(connection);
            vente.validerObject("1060", connection);
            MvtCaisse caisse = generalEJBLocalServer.mvtCaisse(vente,date,connection);
            connection.commit();
            return caisse;
        }catch (Exception e){
            connection.rollback();
        }finally {
            connection.close();
        }
        return null;
    }

}
