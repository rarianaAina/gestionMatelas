/*
package servlet;

import clientEJB.EjbServiceProvider;
import itu.station.tools.Vente;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "VenteServlet", urlPatterns = {"/vente"})
public class VenteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection connection = null;
        try {
            Vente vente
            connection = new UtilDB().GetConn("gallois", "gallois");
            resp.sendRedirect("index.jsp?but=pages/vente.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("index.jsp?but=pages/vente.jsp&val=error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}*/
