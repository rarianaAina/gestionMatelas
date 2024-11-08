package itu.station.jauge;

import bean.CGenUtil;
import ejbServer.GeneralEJBLocalServer;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.mesureCuve.CuveMesure;
import itu.station.mesureCuve.CuveMesureAvecVirj;
import itu.station.mesureCuve.CuveMesureService;
import itu.station.mesureCuve.CuveMesureSignature;
import jauge.Jauge;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;

public class JaugeService implements JaugeInterface{
    @Override
    public Jauge makeJauge(JaugeArgs jaugeArgs,Connection gallois,Connection tsotra) throws Exception {
        GeneralEJBLocalServer generalEJBLocalServer = EjbClientGetter.getGeneralEjbService();
        CuveMesureSignature cuveMesureSignature = new CuveMesureService();
        String idCuve = jaugeArgs.getIdCuve();
        try{
            CuveMesure temp = (cuveMesureSignature.getCuveCarbQtyByCuve(idCuve, jaugeArgs.getMesure(), tsotra));
        }catch(Exception e){
            cuveMesureSignature = new CuveMesureAvecVirj();
        }
        Jauge jauge = new Jauge();
        jauge.setDaty(jaugeArgs.getDaty());
        jauge.setIdMagasin(jaugeArgs.getIdCuve());
        jauge.setQte(cuveMesureSignature.getCuveCarbQtyByCuve(idCuve, jaugeArgs.getMesure(), tsotra).getQteLitre());
        return generalEJBLocalServer.jaugerCuve(jauge,gallois);
    }

    @Override
    public Jauge getJaugeByDate(String idCuve,Date date, Connection connection) throws Exception {
        Jauge[] jauges = (Jauge[]) CGenUtil.rechercher(new Jauge(),null,null,connection," and idMadagasin = '"+idCuve+"' daty = TO_DATE('"+date.toString()+"','YYYY-MM-dd')");
        if (jauges.length == 0) throw new Exception("Pas de jauge à cette date");
        return jauges[0];
    }

    public static Jauge getById(String id,Connection gallois) throws Exception {
        Jauge[] jauges = (Jauge[]) CGenUtil.rechercher(new Jauge(),null,null,gallois," and id='"+id+"'");
        if (jauges.length == 0) throw new Exception("Aucune jauge pour cet identifiant");
        return jauges[0];
    }
}
