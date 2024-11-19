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
                    String uploadPath = getServletContext().getRealPath("/") + "uploads";

                    // Vérifiez si le dossier 'uploads' existe, sinon créez-le
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        boolean dirCreated = uploadDir.mkdirs();
                        if (!dirCreated) {
                            throw new IOException("Impossible de créer le répertoire : " + uploadDir.getAbsolutePath());
                        }
                    }

                    // Construire le chemin complet pour le fichier
                    String filePath = uploadPath + File.separator + fileName;
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
            // Connexion à la base de données
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn("mystation", "mystation");
            conn.setAutoCommit(false);

            // Ignorer la première ligne (en-têtes)
            br.readLine();

            // Préparer la requête
            String query = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, " +
                    "DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Lire chaque ligne du CSV
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    try {
                        // Nettoyer et attribuer chaque valeur
                        pstmt.setString(1, cleanValue(values[0])); // IDBLOCSVAOVAO
                        pstmt.setDouble(2, Double.parseDouble(cleanValue(values[1]))); // LONGUEUR
                        pstmt.setDouble(3, Double.parseDouble(cleanValue(values[2]))); // LARGEUR
                        pstmt.setDouble(4, Double.parseDouble(cleanValue(values[3]))); // HAUTEUR
                        pstmt.setDouble(5, Double.parseDouble(cleanValue(values[4]))); // VOLUME
                        pstmt.setDouble(6, Double.parseDouble(cleanValue(values[5]))); // PRIX_REVIENT
                        pstmt.setDate(7, Date.valueOf(cleanValue(values[6]))); // DATE_FABRICATION
                        pstmt.setString(8, cleanValue(values[7])); // HEURE_FABRICATION
                        pstmt.setString(9, cleanValue(values[8])); // IDSOURCE
                        pstmt.setString(10, cleanValue(values[9])); // PRIX_REVIENT_PRA

                        pstmt.addBatch();
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Erreur de conversion ou ligne invalide : " + line);
                    }
                }

                // Exécuter le batch
                pstmt.executeBatch();
                conn.commit();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
        } finally {
            if (conn != null) conn.close();
        }
    }

    private String cleanValue(String value) {
        if (value == null) {
            return "";
        }
        // Supprimer les espaces et les guillemets autour de la valeur
        return value.trim().replaceAll("^\"|\"$", "");
    }

}

