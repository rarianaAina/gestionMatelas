package itu.station.jauge.anomalie;

import itu.station.utils.TimeUtils;
import jauge.Jauge;

import java.sql.Connection;

public class StockManip {
    public double detecterAnomalie(Jauge jauge,Connection gallois,Connection tsotra) throws Exception {
        Anomalie anomalie = new Anomalie(jauge.getIdMagasin(),TimeUtils.getYesterday(jauge.getDaty()),gallois,tsotra);
        if (anomalie.getJauge() == null){
            return 0;
        }
        return anomalie.getTheoricalQuantity() - jauge.getQte();
    }
}