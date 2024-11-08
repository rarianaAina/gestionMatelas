package servlet;

import itu.station.finance.StatutFinancier;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet(name = "Finance",urlPatterns = {"/prevision"})
public class PrevisionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String datePrev = req.getParameter("datePrev");

        } catch (Exception e) {
            resp.sendRedirect("index.jsp?but=pages/prevision-result.jsp&error="+e.getMessage());
            throw new RuntimeException(e);
        }
/*        try {
            String dateMin = req.getParameter("datePrev");
            StatutFinancier statutFinancier = new StatutFinancier();
            finance.EtatDeFinance etatDeFinance = statutFinancier.getEtatDeFinance(dateMin,dateMax,null);
            req.getSession().setAttribute("etatFinancier",etatDeFinance) ;
            resp.sendRedirect("index.jsp?but=pages/bilan-result.jsp");
        } catch (Exception e) {
            resp.sendRedirect("index.jsp?but=pages/bilan-result.jsp&error="+e.getMessage());
            throw new RuntimeException(e);
        }*/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
