package itu.station.jauge;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;

public interface JaugeInterface {
    public jauge.Jauge makeJauge(JaugeArgs jaugeArgs, Connection gallois, Connection tsotra) throws Exception;
    public jauge.Jauge getJaugeByDate(String idCuve,Date date,Connection connection) throws Exception;
}
