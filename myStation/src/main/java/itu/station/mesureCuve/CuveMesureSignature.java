package itu.station.mesureCuve;

import itu.station.tools.Cuve;

import java.sql.Connection;

public interface CuveMesureSignature {
    public CuveMesure getCuveCarbQtyByCuve(Cuve cuve,double qty, Connection connection) throws Exception;
    public CuveMesure getCuveCarbQtyByCuve(String idCuve,double qty, Connection connection) throws Exception;
}
