package itu.station.stock;

import bean.CGenUtil;
import itu.station.tools.Carburant;
import itu.station.tools.Cuve;
import utilitaire.UtilDB;

import java.sql.Connection;

public class MvtService {
    public void makeMvt(MvtDTO mvtDTO, Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
            Cuve cuve = getCuveById(connection);
            Carburant carburant = cuve.getCarburantDetails(connection);
            int typeMvt = mvtDTO.getType_mvt();
            MvtStock stock = createStockLine(cuve,carburant,typeMvt);
            stock.construirePK(connection);
            stock.setId_type_mvt(typeMvt);
            stock.setQte(mvtDTO.getQte());
            stock.setDaty(mvtDTO.getDaty());
            MvtStock previousLine = null;
            double stockQty = 0;
            if (getLastMvt(cuve,connection) != null){
                previousLine = getLastMvt(cuve,connection);
                stockQty = previousLine.getEtatDeStock();
            }
            System.out.println("STOCK QTY PRV LINE:"+stockQty);
            System.out.println("Type mvt:"+typeMvt);
            System.out.println("STOCK QTY :"+stock.getQte());

            stock.setEtatDeStock(stockQty+(typeMvt*stock.getQte()));
            CGenUtil.save(stock,connection);
    }
    protected Cuve getCuveById(Connection c) throws Exception {
        if (c==null) c = new UtilDB().GetConn();
        return ((Cuve[]) CGenUtil.rechercher(new Cuve(),null,null," "))[0];
    }
    protected MvtStock createStockLine(Cuve cuve,Carburant carburant,int typeMov) throws Exception {
        MvtStock mvtStock = new MvtStock();
        mvtStock.setId_cuve(cuve.getId());
        double pu = carburant.getPu_achat();
        if (typeMov == -1) pu = carburant.getPu_vente();
        mvtStock.setPu(pu);
        return mvtStock;
    }
    protected MvtStock getLastMvt(Cuve cuve,Connection c) throws Exception {
        if (c==null) c = new UtilDB().GetConn();
        MvtStock[] mvtStocks = ((MvtStock[]) CGenUtil.rechercher(new MvtStock(),null,null," and daty=(SELECT MAX(daty) FROM stock WHERE id_cuve = '"+ cuve.getId() +"') and id_cuve = '"+cuve.getId()+"'"));
        MvtStock ret = null;
        if (mvtStocks.length != 0) ret = mvtStocks[0];
        return ret;
    }
}
