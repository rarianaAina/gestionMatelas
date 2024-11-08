package itu.station.mesureCuve;

import bean.CGenUtil;
import itu.station.tools.Cuve;

import java.sql.Connection;

public class CuveMesureAvecVirj implements CuveMesureSignature{
    @Override
    public CuveMesure getCuveCarbQtyByCuve(Cuve cuve, double qty, Connection connection) throws Exception {
        return null;
    }

    @Override
    public CuveMesure getCuveCarbQtyByCuve(String idCuve, double qty, Connection connection) throws Exception {

        CuveMesure[] mesList = (CuveMesure[]) CGenUtil.rechercher(new CuveMesure(),"SELECT * " +
                "FROM CUVE_MESURES " +
                "WHERE MESURE = ( " +
                "    SELECT MAX(MESURE) " +
                "    FROM CUVE_MESURES " +
                "    WHERE IDCUVE = '"+idCuve+"' AND MESURE <= "+qty+" " +
                ") AND IDCUVE = '"+idCuve+"' " +
                "UNION ALL " +
                "SELECT * " +
                "FROM CUVE_MESURES " +
                "WHERE MESURE = ( " +
                "    SELECT MIN(MESURE) " +
                "    FROM CUVE_MESURES " +
                "    WHERE IDCUVE = '"+idCuve+"' AND MESURE >= "+qty+" " +
                ") AND IDCUVE = '"+idCuve+"'",connection);

        CuveMesure cm = new CuveMesure();
        cm.setId("Temp");
        cm.setMesure(qty);
        cm.setIdCuve(idCuve);
        cm.setQteLitre(getEquivalence(mesList,qty));
        return cm;
    }
    public double getEquivalence(CuveMesure[] sup,double qty){
        //a = qty - sup[0].getMesure()
        //ratio = a/(-sup[0].getMesure()+sup[1].getMesure())
        //v = sup[0].GetQte() + (ratio*(sup[1].getQte()-sup[0].getQte())
        double a = qty - sup[0].getMesure();
        System.out.println("a"+a);
        double ratio = a/(-sup[0].getMesure() + sup[1].getMesure());
        double v = sup[0].getQteLitre() + (ratio*(sup[1].getQteLitre()-sup[0].getQteLitre()));
        System.out.println("Calcul = "+ratio+" * ("+sup[1].getQteLitre()+" - "+sup[0].getQteLitre()+")");
        return v;
    }
}
