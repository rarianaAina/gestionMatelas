package itu.station.stock.vente;

import itu.station.stock.MvtArgent;
import itu.station.stock.MvtDTO;
import itu.station.stock.MvtService;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class VenteService implements MvtArgent {
    @Override
    public void makeMvtArgent(MvtDTO mvtDto, Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
        if (mvtDto.getType_mvt() != -1) mvtDto.setType_mvt(-1);
        if (mvtDto.getDaty() == null) mvtDto.setDaty(new Date(System.currentTimeMillis()));
        MvtService mvtService = new MvtService();
        mvtService.makeMvt(mvtDto,connection);
    }
}
