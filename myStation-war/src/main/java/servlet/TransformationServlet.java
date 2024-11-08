package servlet;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.station.kidoro.Kidoro;
import utilitaire.UtilDB;

@WebServlet("/traitementTransformation")
public class TransformationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idBloc = request.getParameter("idBloc");
        int ecartAccepte = Integer.parseInt(request.getParameter("ecartAccepte"));
        int fu1 = Integer.parseInt(request.getParameter("fu1"));
        int fu2 = Integer.parseInt(request.getParameter("fu2"));
        int fu3 = Integer.parseInt(request.getParameter("fu3"));
        int fu4 = Integer.parseInt(request.getParameter("fu4"));
        Date date_creation = Date.valueOf(request.getParameter("dateTransformation"));
        Double volumeResteSaisi = Double.parseDouble(request.getParameter("volume"));

        // Récupération des dimensions du bloc
        double longueur = Double.parseDouble(request.getParameter("longueurReste"));
        double largeur = Double.parseDouble(request.getParameter("largeurReste"));
        double hauteur = Double.parseDouble(request.getParameter("hauteurReste"));

        try (Connection connection = new UtilDB().GetConn("mystation", "mystation")) {

            double volumeTotal = getVolumeTotal(connection, idBloc);
            double volumeRestant = getVolumeRestant(connection, idBloc);

            // Calcul du nouveau volume
            double nouveauVolume = volumeResteSaisi;
            System.out.println(idBloc);
            System.out.println(volumeRestant);
            System.out.println(volumeTotal);
            System.out.println(nouveauVolume);

            double volumeProduit = 0.0;

            // Obtenir le volume pour chaque type de produit (fu1, fu2, fu3, fu4)
            String query = "SELECT volume FROM TYPEKIDORO WHERE idTypeKIDORO = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                System.out.println("Tato izy no niditra 1");
                // Traiter chaque FU
                for (int i = 1; i <= 4; i++) {
                    int fuQuantity = (i == 1) ? fu1 : (i == 2) ? fu2 : (i == 3) ? fu3 : fu4;

                    if (fuQuantity > 0) {
                        ps.setString(1, String.valueOf(i)); // Supposons que l'ID de produit correspond à 1, 2, 3, 4
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                double volumeProduitTemp = rs.getDouble("volume");
                                volumeProduit += volumeProduitTemp * fuQuantity; // Volume total pour ce produit
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Volume Produit Total : " + volumeProduit);
            double volumeSource = getVolumeSource(connection, idBloc);
            System.out.println(volumeSource);
            if (volumeResteSaisi + volumeProduit > volumeSource) {
                System.out.println("Le total des volumes saisis est plus grand que le volume source, impossible");
                request.setAttribute("errorMessage", "Le total des volumes saisis est plus grand que le volume source, impossible.");
                request.getRequestDispatcher("index.jsp?but=pages/transformation.jsp").forward(request, response);
                return;
            }
            double resteTheorique = volumeSource - volumeProduit;
            double ecartReste = resteTheorique - volumeResteSaisi;
            double rapportEcartResteSurVolume = ecartReste/volumeSource;
            double tetaReste = rapportEcartResteSurVolume * 100;
            System.out.println("Reste théorique: " + resteTheorique);
            System.out.println("Ecart reste: " + ecartReste);
            System.out.println("Rapport écart reste sur volume: " + rapportEcartResteSurVolume);
            System.out.println("Pourcentage écart: " + tetaReste);
            if (tetaReste > ecartAccepte) {
                request.setAttribute("errorMessage", "Un écart par rapport à l'écart accepté est constaté, vérifiez auprès de vos stocks");
                System.out.println("Un écart de " + tetaReste + "% est constaté, vous pouvez checker vos données");
                request.getRequestDispatcher("index.jsp?but=pages/transformation.jsp").forward(request, response);
                return;
            }

            System.out.println("Ecart Pourcentage : " + tetaReste);

/*            if (ecartPourcentage > 2) {
                request.setAttribute("confirmationMessage", "Un très grand écart a été constaté. Voulez-vous vraiment continuer?");
                request.setAttribute("idBloc", idBloc);
                request.setAttribute("nouveauVolume", nouveauVolume);
                request.getRequestDispatcher("index.jsp?but=pages/transformation.jsp").forward(request, response);
                return;
            }*/

            // Traiter chaque F.U (F.U1 à F.U4)
            processFU(connection, idBloc, "1", fu1, date_creation);
            processFU(connection, idBloc, "2", fu2, date_creation);
            processFU(connection, idBloc, "3", fu3, date_creation);
            processFU(connection, idBloc, "4", fu4, date_creation);

            // Insérer un nouveau bloc avec les dimensions récupérées
            String newIdBloc = insertNouveauBloc(connection, idBloc, volumeResteSaisi, longueur, largeur, hauteur);
            String idBlocSourceInitial = getOriginalIdBlocInitial(connection, idBloc);
            System.out.println(idBlocSourceInitial);

            //String idBlocSourceInitial = getOriginalIdBlocParent(connection, idBloc);
   /*         if (idBlocSourceInitial == null) {
                idBlocSourceInitial = idBloc;
            }*/

            insertSourceRelation(connection, newIdBloc,idBlocSourceInitial, idBloc);
            // Insérer les détails de la transformation et obtenir son ID
            String idTransformation = insertTransformation(connection, idBloc, date_creation, volumeTotal, volumeRestant);

            // Insérer les restes en utilisant l'ID de transformation
            insertRestes(connection, idTransformation, longueur, largeur, hauteur, volumeRestant, date_creation);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }

        // Rediriger vers une page de confirmation ou d'affichage
        response.sendRedirect("confirmation.jsp");
    }


    private void insertSourceRelation(Connection connection, String idBlocActuel, String idBlocSourceInitial, String idBlocParent) throws SQLException {
        String sql = "INSERT INTO Source (idBlocActuel, idBlocSourceInitial, idBlocParent) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idBlocActuel);         // Nouveau bloc créé
            stmt.setString(2, idBlocSourceInitial);  // Bloc source initial (ex : BLC001)
            stmt.setString(3, idBlocParent);         // Bloc parent (ex : BLC002)
            stmt.executeUpdate();
        }
    }

    public boolean isBlocTransformed(Connection connection, String idBlocInitial) throws SQLException {
        // Requête SQL pour compter le nombre de lignes où idBlocInitial correspond à l'ID passé en paramètre
        String sql = "SELECT COUNT(*) AS count FROM BLOCS WHERE idblocinitial = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Remplir le paramètre de la requête avec l'idBlocInitial
            ps.setString(1, idBlocInitial);

            // Exécuter la requête
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    // Si le nombre de lignes est supérieur à 1, alors le bloc est transformé
                    return count > 1;
                }
            }
        }

        // Si aucune ligne n'est trouvée, le bloc n'est pas transformé
        return false;
    }

    private double getVolumeSource(Connection connection, String idBloc) throws SQLException {
        String sql = "SELECT LONGUEUR, LARGEUR, HAUTEUR FROM BLOCS WHERE IDBLOC = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, idBloc);
            System.out.println(idBloc);
            System.out.println(sql);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double longueur = rs.getDouble("LONGUEUR");
                    double largeur = rs.getDouble("LARGEUR");
                    double hauteur = rs.getDouble("HAUTEUR");
                    return longueur * largeur * hauteur; // Volume = Longueur * Largeur * Hauteur
                }
            }
        }
        return 0; // Retourne 0 si le bloc n'est pas trouvé
    }

    private double getVolumeTotal(Connection connection, String idBloc) throws SQLException {
        String sql = "SELECT SUM(VOLUME_TOTAL) AS totalVolume FROM VOLUME_TOTAL_PAR_SOURCE WHERE IDSOURCE = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, idBloc);
            System.out.println(idBloc);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Double volume = rs.getDouble("totalVolume");
                return volume;
            }
        }
        return 0;
    }
    private double calculVolume(Connection connection, Double longueur, Double largeur, Double hauteur) {
        Double volume = longueur * largeur * hauteur;
        return volume;
    }
    private double getVolumeRestant(Connection connection, String idBloc) throws SQLException {
        String sql = "SELECT VOLUME AS volumereste FROM BLOCS WHERE IDBLOC = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, idBloc);
            System.out.println(idBloc);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("volumereste") - getVolumeTotal(connection, idBloc);
            }
        }
        return 0;
    }

    private String insertNouveauBloc(Connection connection, String idBloc, double volume, double longueur, double largeur, double hauteur) throws SQLException {
        String newIdBloc = getNewIdBloc(connection);
        String idBlocInitial = getOriginalIdBlocInitial(connection, idBloc);
        String idSource = getOriginalIdBlocParent(connection, idBloc);
        // Calculer le prix de revient proportionnel au volume
        double prixDeRevient = calculatePriceDeRevient(connection, idBloc, volume);

        String sql = "INSERT INTO BLOCS (IDBLOC, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIXDEREVIENT, IDBLOCINITIAL, DATE_CREATION_BLOC, IDSOURCE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newIdBloc);
            stmt.setDouble(2, longueur);
            stmt.setDouble(3, largeur);
            stmt.setDouble(4, hauteur);
            stmt.setDouble(5, volume);
            stmt.setDouble(6, prixDeRevient); // Utilisation du prix de revient calculé
            stmt.setString(7, idBlocInitial);
            stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            stmt.setString(9, idSource);
            stmt.executeUpdate();
        }
        return newIdBloc;
    }

    private double calculatePriceDeRevient(Connection connection, String idBloc, double nouveauVolume) throws SQLException {
        // Obtenir le prix de revient total pour le bloc d'origine
        String sql = "SELECT PRIXDEREVIENT, VOLUME FROM BLOCS WHERE IDBLOC = ?";
        double prixTotal = 0;
        double volumeTotal = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                prixTotal = rs.getDouble("PRIXDEREVIENT");
                volumeTotal = rs.getDouble("VOLUME");
            }
        }

        // Calculer le prix de revient par unité de volume
        double prixParVolume = prixTotal / volumeTotal;
        System.out.println(prixParVolume);
        // Calculer le prix de revient pour le nouveau volume
        return prixParVolume * nouveauVolume;
    }

    private String getOriginalIdBlocInitial(Connection connection, String idBloc) throws SQLException {
        String sql = "SELECT IDBLOCSOURCEINITIAL FROM MYSTATION.SOURCE WHERE idBlocActuel = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("idBlocSourceInitial"); // Renvoie le bloc source initial directement
            } else {
                return idBloc; // Renvoie l'ID actuel s'il n'y a pas de source initiale (ex : bloc autonome)
            }
        }
    }
    private String getOriginalIdBlocParent(Connection connection, String idBloc) throws SQLException {
        String sql = "SELECT IDBLOCPARENT FROM MYSTATION.SOURCE WHERE idBlocActuel = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("idBlocParent"); // Renvoie le bloc source initial directement
            } else {
                return idBloc; // Renvoie l'ID actuel s'il n'y a pas de source initiale (ex : bloc autonome)
            }
        }
    }




    private String getNewIdBloc(Connection connection) throws SQLException {
        String sql = "SELECT BLOCS_SEQ.NEXTVAL AS NEW_IDBLOC FROM DUAL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String idBloc = rs.getString("NEW_IDBLOC");
                return "BLC" + idBloc;
            }
        }
        return null;
    }

    private void processFU(Connection connection, String idBloc, String typeKidoro, int qte, Date date_creation) throws SQLException {
        String checkSql = "SELECT qte FROM KIDORO WHERE idSource = ? AND idTypeKidoro = ?";
        String insertSql = "INSERT INTO KIDORO (idKidoro, idSource, idTypeKidoro, qte, date_creation, prix_revient, idsourceinitial) VALUES (KR_KIDORO_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE KIDORO SET qte = qte + ? WHERE idSource = ? AND idTypeKidoro = ? AND date_creation = ?";
        String idSourceInitial = getOriginalIdBlocInitial(connection, idBloc);
        System.out.println("Id source Initial: " + idSourceInitial);

        String getVolumeSql = "SELECT volume FROM TypeKidoro WHERE idTypeKidoro = ?";
        Double verifiedVolume = 0.0;
        System.out.println(typeKidoro);

        try (PreparedStatement volumeStmt = connection.prepareStatement(getVolumeSql)) {
            volumeStmt.setString(1, typeKidoro);
            System.out.println(getVolumeSql);
            ResultSet volumeRs = volumeStmt.executeQuery();

            if (volumeRs.next()) {
                verifiedVolume = volumeRs.getDouble("volume");  // Récupérer le volume vérifié
                System.out.println("Volume récupéré de la FU: " + verifiedVolume);
            } else {
                throw new SQLException("Volume introuvable pour l'idTypeKidoro : " + typeKidoro);
            }
        }

        double volumeFinal = verifiedVolume * qte;
        System.out.println("Volume final: " + volumeFinal);
        System.out.println("Quantité: " + qte);

        // Utiliser la vue pour récupérer le prix de revient du fils
        String getPrixRevSql = "SELECT PRIX_REV_FILS FROM V_CALCUL_PRIX_REV WHERE IDBLOC_FILS = ?";
        double prixRev = 0.0;

        try (PreparedStatement prixRevStmt = connection.prepareStatement(getPrixRevSql)) {
            prixRevStmt.setString(1, idBloc);
            ResultSet prixRevRs = prixRevStmt.executeQuery();

            if (prixRevRs.next()) {
                prixRev = prixRevRs.getDouble("PRIX_REV_FILS");
                System.out.println("Prix de revient pour le bloc fils: " + prixRev);
            } else {
                throw new SQLException("Prix de revient introuvable pour le bloc avec ID : " + idBloc);
            }
        }

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, idBloc);
            checkStmt.setString(2, typeKidoro);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Si existe, mettre à jour la quantité
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, qte);
                    updateStmt.setString(2, idBloc);
                    updateStmt.setString(3, typeKidoro);
                    updateStmt.setDate(4, date_creation);
                    updateStmt.executeUpdate();
                }
            } else {
                // Si n'existe pas, insérer avec le prix de revient récupéré
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, idBloc);
                    insertStmt.setString(2, typeKidoro);
                    insertStmt.setInt(3, qte);
                    insertStmt.setDate(4, date_creation);
                    insertStmt.setDouble(5, prixRev); // Ajouter le prix de revient récupéré
                    insertStmt.setString(6, idSourceInitial);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private String insertTransformation(Connection connection, String idBloc, Date dateTransformation, double volumeUtilise, double volumeRestant) throws SQLException {
        String idTransformation = getNewIdTransformation(connection);
        String sql = "INSERT INTO TRANSFORMATION (IDTRANSFORMATION, IDBLOC, DATE_TRANSFORMATION, VOLUME_UTILISE, VOLUME_RESTANT) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idTransformation);
            stmt.setString(2, idBloc);
            stmt.setDate(3, dateTransformation);
            stmt.setDouble(4, volumeUtilise);
            stmt.setDouble(5, volumeRestant);
            stmt.executeUpdate();
        }
        return idTransformation;
    }

    private String getNewIdTransformation(Connection connection) throws SQLException {
        String sql = "SELECT TRANSFORMATION_SEQ.NEXTVAL AS NEW_IDTRANSFORMATION FROM DUAL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return "TRANS" + rs.getString("NEW_IDTRANSFORMATION");
            }
        }
        return null;
    }

    private void insertRestes(Connection connection, String idBloc, Double longueur, Double largeur, Double hauteur, double volumeRestant, Date dateCreation) throws SQLException {
        String sql = "INSERT INTO RESTES (IDRESTE, IDTRANSFORMATION, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, DATE_CREATION) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String idReste = getNewIdReste(connection);
            stmt.setString(1, idReste);
            stmt.setString(2, idBloc); // Remplacez par IDTRANSFORMATION si nécessaire
            stmt.setDouble(3, longueur); // Remplacez par les dimensions du reste si nécessaire
            stmt.setDouble(4, largeur);
            stmt.setDouble(5, hauteur);
            stmt.setDouble(6, volumeRestant);
            stmt.setDate(7, dateCreation);
            stmt.executeUpdate();
        }
    }

    private String getNewIdReste(Connection connection) throws SQLException {
        String sql = "SELECT RESTES_SEQ.NEXTVAL AS NEW_IDRESTE FROM DUAL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return "REST" + rs.getString("NEW_IDRESTE");
            }
        }
        return null;
    }
}
