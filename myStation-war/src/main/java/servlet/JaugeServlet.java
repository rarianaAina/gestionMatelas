package servlet;

import itu.station.jauge.JaugeArgs;
import itu.station.jauge.JaugeService;
import itu.station.jauge.JaugeInterface;
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

@WebServlet(name = "Jauge" , urlPatterns = {"/jauge"})
public class JaugeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection gallois = null;
        Connection tsotra = null;
        try {
            gallois = new UtilDB().GetConn("gallois","gallois");
            tsotra = new UtilDB().GetConn();
            String date = req.getParameter("daty");
            String idCuve = req.getParameter("idCuve");
            String mesure = req.getParameter("mesure");
            JaugeArgs jaugeArgs = new JaugeArgs(idCuve,date,mesure);
            JaugeInterface jaugeSignature = new JaugeService();
            Jauge savedOne = jaugeSignature.makeJauge(jaugeArgs,gallois,tsotra);
            req.setAttribute("jauge",savedOne);
            gallois.commit();
            tsotra.commit();
            req.getRequestDispatcher("index.jsp?but=pages/jauge-result.jsp").forward(req,resp);
//            resp.sendRedirect("index.jsp?but=pages/jauge-result.jsp");
        }catch (Exception e){
            try {
                req.setAttribute("error",e.getMessage());
                gallois.rollback();
                tsotra.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
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
