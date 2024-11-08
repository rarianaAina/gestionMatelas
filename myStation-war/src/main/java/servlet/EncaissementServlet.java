package servlet;

import caisse.MvtCaisse;
import itu.station.prelevement.Encaissement;
import itu.station.prelevement.FactureClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet(name = "Encaissement",urlPatterns = {"encaissement"})
public class EncaissementServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Encaissement encaissement = new Encaissement();
            String date = req.getParameter("daty");
            encaissement.setFactureClient((FactureClient) req.getSession().getAttribute("facture"));
            encaissement.setDate(date);
            MvtCaisse mvtCaisse = encaissement.makeMovement(null);
            req.getSession().setAttribute("mvtCaisse",mvtCaisse);
            resp.sendRedirect("index.jsp?but=pages/facture.jsp?noEnc=1");
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
//            throw new RuntimeException(e);
        }
    }
}
