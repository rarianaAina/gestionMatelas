package itu.station.mesureCuve;

import bean.CGenUtil;
import itu.station.tools.Cuve;

import java.sql.Connection;

public class CuveMesureService implements CuveMesureSignature{
    @Override
    public CuveMesure getCuveCarbQtyByCuve(Cuve cuve,double qty, Connection connection) throws Exception {
        return getCuveCarbQtyByCuve(cuve.getId(),qty,connection);
    }
    @Override
    public CuveMesure getCuveCarbQtyByCuve(String idCuve,double qty, Connection connection) throws Exception {
        CuveMesure[] cuveMesures = (CuveMesure[]) CGenUtil.rechercher(new CuveMesure(),null,null,connection," and idCuve = '"+idCuve+"' and mesure = "+qty+"");
        if (cuveMesures.length == 0) throw new Exception("Aucune correspondance par rapport à cette mesure");
        return cuveMesures[0];
    }
}
