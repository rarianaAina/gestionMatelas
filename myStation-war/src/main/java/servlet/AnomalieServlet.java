package servlet;

import itu.station.jauge.JaugeService;
import itu.station.jauge.anomalie.AnomalieCumul;
import itu.station.jauge.anomalie.StockManip;
import jauge.Jauge;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "anomalie",urlPatterns = {"anomalie"})
public class AnomalieServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection gallois = new UtilDB().GetConn("gallois","gallois");
        Connection tsotra = new UtilDB().GetConn();
        try {
            StockManip stockManip = new StockManip();
            String idJauge = req.getParameter("idJauge");
            Jauge jauge = JaugeService.getById(idJauge,gallois);
            double val = stockManip.detecterAnomalie(jauge,gallois,tsotra);
            System.out.println("Boolean Value:"+val);
            AnomalieCumul anomalieCumul = new AnomalieCumul(jauge.getIdMagasin(),jauge.getDaty(),gallois,tsotra);
            req.setAttribute("anomalie",val);
            req.setAttribute("anomalieCumul",anomalieCumul);
            req.getRequestDispatcher("index.jsp?but=pages/anomalie-result.jsp").forward(req,resp);
        }catch (Exception e){
        }finally {
            try {
                gallois.close();
                tsotra.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection gallois = new UtilDB().GetConn("gallois","gallois");
        Connection tsotra = new UtilDB().GetConn();
        try {
            StockManip stockManip = new StockManip();
            String idJauge = req.getParameter("idJauge");
            Jauge jauge = JaugeService.getById(idJauge,gallois);
            double val = stockManip.detecterAnomalie(jauge,gallois,tsotra);
            System.out.println("Boolean Value:"+val);
            AnomalieCumul anomalieCumul = new AnomalieCumul(jauge.getIdMagasin(),jauge.getDaty(),gallois,tsotra);
            req.setAttribute("anomalie",val);
            req.setAttribute("anomalieCumul",anomalieCumul);
            System.out.println("cumul:"+anomalieCumul.getCumul());
            req.getRequestDispatcher("index.jsp?but=pages/anomalie-result.jsp").forward(req,resp);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                gallois.close();
                tsotra.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
