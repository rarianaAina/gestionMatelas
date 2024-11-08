package itu.station.stock.achat;

import itu.station.stock.MvtArgent;
import itu.station.stock.MvtDTO;
import itu.station.stock.MvtService;
import utilitaire.UtilDB;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.Date;

//@Stateless
public class AchatService implements MvtArgent {
    @Override
    public void makeMvtArgent(MvtDTO mvtDTO, Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
        if (mvtDTO.getType_mvt() != 1) mvtDTO.setType_mvt(1);
        if (mvtDTO.getDaty() == null) mvtDTO.setDaty(new Date(System.currentTimeMillis()));
        MvtService mvtService = new MvtService();
        mvtService.makeMvt(mvtDTO,connection);
    }
}
