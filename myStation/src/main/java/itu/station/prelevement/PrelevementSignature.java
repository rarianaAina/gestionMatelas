package itu.station.prelevement;

import javax.ejb.Local;
import java.sql.Connection;
@Local
public interface PrelevementSignature {
    public FactureClient ciblerDeuxBases(Prelevement prelevement,Connection connection) throws Exception;
}
