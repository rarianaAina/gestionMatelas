/*
package servlet;

import clientEJB.EjbServiceProvider;
import itu.station.dataGenerator.DataGenerator;
import itu.station.dataGenerator.DataGeneratorAjbService;
import itu.station.dataGenerator.DataGeneratorEjbSignature;
import itu.station.prelevement.FactureClient;
import itu.station.prelevement.Prelevement;
import itu.station.prelevement.PrelevementService;
import itu.station.prelevement.PrelevementSignature;
import utilitaire.UtilDB;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "Prelevement",urlPatterns = {"/prelevement"})
public class PrelevementServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            PrelevementSignature prelevementSignature = EjbServiceProvider.getPrelevementEjbService();
            Prelevement prelevement = new Prelevement();

            // Set parameters from the request
            prelevement.setQte(req.getParameter("qte"));
            prelevement.setDaty(req.getParameter("daty"));
            prelevement.setHeure(req.getParameter("heure"));
            prelevement.setIdPompe(req.getParameter("idPompe"));
            prelevement.setIdUtilisateur(req.getParameter("idUtilisateur"));

            FactureClient facture = prelevementSignature.ciblerDeuxBases(prelevement, null);

            if (facture != null && facture.getMontant() != -1) {
                // Redirect if facture is valid
                req.getSession().setAttribute("facture", facture);
                resp.sendRedirect("index.jsp?but=pages/facture.jsp");
            } else {
                // Redirect to another page if facture is null or montant is -1
                resp.sendRedirect("index.jsp?but=pages/prelevement.jsp&val=success");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = null;
        try {
            conn = new UtilDB().GetConn();
            if(req.getParameter("idFacture")!=null){
                req.getSession().setAttribute("facture",new FactureClient(req.getParameter("idFacture"),conn));
                resp.sendRedirect("index.jsp?but=pages/facture.jsp");
            }
//            DataGenerator.generateData();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
*/

package servlet;

import clientEJB.EjbServiceProvider;
import itu.station.prelevement.FactureClient;
import itu.station.prelevement.Prelevement;
import itu.station.prelevement.PrelevementSignature;
import utilitaire.UtilDB;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "Prelevement", urlPatterns = {"/prelevement"})
public class PrelevementServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrelevementSignature prelevementSignature = EjbServiceProvider.getPrelevementEjbService();
        Prelevement prelevement = new Prelevement();

        // Vérification du Content-Type pour différencier les sources de requêtes
        String contentType = req.getContentType();

        try {
            if ("application/json".equalsIgnoreCase(contentType)) {
                // Si le Content-Type est JSON, lire le JSON depuis la requête
                StringBuilder jsonBuffer = new StringBuilder();
                String line;
                try (BufferedReader reader = req.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBuffer.append(line);
                    }
                }
                JSONObject jsonData = new JSONObject(jsonBuffer.toString());

                // Extraire les données JSON et les affecter à l'objet Prelevement
                prelevement.setQte(jsonData.getString("qte"));
                prelevement.setDaty(jsonData.getString("daty"));
                prelevement.setHeure(jsonData.getString("heure"));
                prelevement.setIdPompe(jsonData.getString("idPompe"));
                prelevement.setIdUtilisateur(jsonData.getString("idUtilisateur"));

                // Logique de traitement pour le JSON (même logique que pour les requêtes classiques)
                FactureClient facture = prelevementSignature.ciblerDeuxBases(prelevement, null);

                // Réponse JSON pour l'application mobile
                resp.setContentType("application/json");
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", facture != null && facture.getMontant() != -1);
                resp.getWriter().write(jsonResponse.toString());

            } else {
                // Si le Content-Type n'est pas JSON, traitement des requêtes classiques
                prelevement.setQte(req.getParameter("qte"));
                prelevement.setDaty(req.getParameter("daty"));
                prelevement.setHeure(req.getParameter("heure"));
                prelevement.setIdPompe(req.getParameter("idPompe"));
                prelevement.setIdUtilisateur(req.getParameter("idUtilisateur"));

                FactureClient facture = prelevementSignature.ciblerDeuxBases(prelevement, null);

                if (facture != null && facture.getMontant() != -1) {
                    req.getSession().setAttribute("facture", facture);
                    resp.sendRedirect("index.jsp?but=pages/facture.jsp");
                } else {
                    resp.sendRedirect("index.jsp?but=pages/prelevement.jsp&val=success");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = null;
        try {
            conn = new UtilDB().GetConn();
            if (req.getParameter("idFacture") != null) {
                req.getSession().setAttribute("facture", new FactureClient(req.getParameter("idFacture"), conn));
                resp.sendRedirect("index.jsp?but=pages/facture.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

