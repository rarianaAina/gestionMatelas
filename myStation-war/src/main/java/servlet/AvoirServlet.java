package servlet;

import avoir.AvoirFC;
import clientEJB.EjbServiceProvider;
import ejbServer.GeneralEJBLocalServer;
import itu.station.ejbService.EjbStation2;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.prelevement.Avoir;
import itu.station.prelevement.FactureClient;
import org.json.JSONException;
import utilitaire.UtilDB;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import org.json.JSONObject;
import java.io.BufferedReader;

@WebServlet(name = "Avoir", urlPatterns = {"/genererAvoir"})
public class AvoirServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String contentType = req.getContentType();

        // Vérifier si le contenu est JSON (API) ou un formulaire (JSP)
        if ("application/json".equalsIgnoreCase(contentType)) {
            handleJsonRequest(req, resp);
        } else {
            handleFormRequest(req, resp);
        }
    }

    // Gestion de la requête JSON (API)
    private void handleJsonRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        StringBuilder jsonBuffer = new StringBuilder();

        // Lecture du JSON depuis la requête
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        try {
            JSONObject jsonData = new JSONObject(jsonBuffer.toString());
            String idClient = jsonData.getString("idClient");
            String idProduit = jsonData.getString("idProduit");
            String montant = jsonData.getString("montant");

            processAvoirRequest(req, resp, idClient, idProduit, montant);
        } catch (JSONException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid JSON data. " + e.getMessage() + "\"}");
        }
    }

    // Gestion de la requête de formulaire (JSP)
    private void handleFormRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Récupérer les paramètres du formulaire
        String idClient = req.getParameter("idClient");
        String idProduit = req.getParameter("idProduit");
        String montant = req.getParameter("montant");

        // Vérifier si les paramètres sont présents
        if (idClient == null || idProduit == null || montant == null) {
            req.setAttribute("errorMessage", "Veuillez remplir tous les champs.");
            req.getRequestDispatcher("/erreur.jsp").forward(req, resp);
            return;
        }

        processAvoirRequest(req, resp, idClient, idProduit, montant);


        resp.sendRedirect("index.jsp?but=pages/facture.jsp");
    }

    // Traitement commun de la génération d'un avoir
    private void processAvoirRequest(HttpServletRequest req, HttpServletResponse resp, String idClient, String idProduit, String montant) throws IOException {
        PrintWriter out = resp.getWriter();
        FactureClient factureClient = (FactureClient) req.getSession().getAttribute("facture");

        if (factureClient == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Facture not found in session.\"}");
            return;
        }

        Avoir avoir = new Avoir(factureClient, idClient, idProduit, montant);
        Connection connection = null;

        try {
            connection = new UtilDB().GetConn("gallois", "gallois");
            EjbStation2 generalEJBLocalServer = EjbServiceProvider.getEjbLocalServer();

            AvoirFC avoir1 = generalEJBLocalServer.genererAvoir(avoir, connection);
            req.getSession().setAttribute("avoirAViser", avoir1);

            // Répondre avec un JSON pour les requêtes AJAX
            if ("application/json".equalsIgnoreCase(resp.getContentType())) {
                resp.setContentType("application/json");
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", true);
                resp.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
