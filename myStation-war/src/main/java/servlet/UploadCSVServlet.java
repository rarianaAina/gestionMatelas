package servlet;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UploadCSVServlet")
public class UploadCSVServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Vérifier si le formulaire contient un fichier
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> formItems = upload.parseRequest(request);
            for (FileItem item : formItems) {
                if (!item.isFormField()) {
                    // Récupérer le fichier téléchargé
                    String fileName = item.getName();
                    String filePath = getServletContext().getRealPath("/") + "uploads/" + fileName;
                    File storeFile = new File(filePath);

                    // Sauvegarder le fichier
                    item.write(storeFile);

                    // Lire et traiter le fichier CSV
                    processCSV(filePath);

                    // Rediriger vers une page de succès
                    response.sendRedirect("success.jsp");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void processCSV(String filePath) throws SQLException, FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        Connection conn = null;

        try {
            // Établir une connexion à la base de données via utilDB.GetConn
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn("mystation", "mystation");

            // Ignorer la première ligne (les en-têtes des colonnes)
            br.readLine();

            // Préparer la requête d'insertion
            String query = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, " +
                    "DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Lire chaque ligne du fichier CSV
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");

                    // Traiter chaque ligne et insérer dans la table
                    pstmt.setInt(1, Integer.parseInt(values[0]));  // IDBLOCSVAOVAO
                    pstmt.setDouble(2, Double.parseDouble(values[1]));  // LONGUEUR
                    pstmt.setDouble(3, Double.parseDouble(values[2]));  // LARGEUR
                    pstmt.setDouble(4, Double.parseDouble(values[3]));  // HAUTEUR
                    pstmt.setDouble(5, Double.parseDouble(values[4]));  // VOLUME
                    pstmt.setDouble(6, Double.parseDouble(values[5]));  // PRIX_REVIENT
                    pstmt.setDate(7, Date.valueOf(values[6]));  // DATE_FABRICATION
                    pstmt.setTime(8, Time.valueOf(values[7]));  // HEURE_FABRICATION
                    pstmt.setString(9, values[8]);  // IDSOURCE
                    pstmt.setDouble(10, Double.parseDouble(values[9]));  // PRIX_REVIENT_PRA

                    // Ajouter la requête d'insertion dans le batch
                    pstmt.addBatch();
                }

                // Exécuter le batch d'insertion
                pstmt.executeBatch();

                // Valider les modifications
                conn.commit();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            if (conn != null) {
                conn.rollback(); // Rollback en cas d'erreur
            }
        } finally {
            if (conn != null) {
                conn.close(); // Fermer la connexion
            }
        }
    }
}

