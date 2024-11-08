package itu.station.stock;

import java.sql.Connection;

public interface MvtArgent {
    public void makeMvtArgent(MvtDTO mvtDTO, Connection connection) throws Exception;
}
