package servlet;

import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/EstimationServlet")
public class EstimationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String idBloc = request.getParameter("idBloc");

        try (Connection connection = new UtilDB().GetConn("mystation", "mystation")) {
            if ("logicVolume".equals(action)) {
                String result = estimationParLogiqueVolume(connection, idBloc);
                response.getWriter().println(result);
            } else if ("pvVolume".equals(action)) {
                String result = estimationViaPVVolume(connection, idBloc);
                response.getWriter().println(result);
            } else if ("minimumReste".equals(action)) {
                String result = minimumDeReste(connection, idBloc);
                response.getWriter().println(result);
            }else if ("parBenefice".equals(action)){
                String result = formeUsuelleMaxBenefice(connection, idBloc);
                response.getWriter().println(result);
            } else {
                response.getWriter().println("Action non reconnue.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Erreur de traitement : " + e.getMessage());
        }
    }

    private String minimumDeReste(Connection connection, String idBloc) throws SQLException {
        // Récupérer le volume du bloc
        String getBlocVolumeQuery = "SELECT volume FROM Blocs WHERE idBloc = ?";
        double blocVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getBlocVolumeQuery)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                blocVolume = rs.getDouble("volume");
            }
        }

        // Trouver le volume le plus petit et différent de 0 dans la table TypeKidoro
        String getMinVolumeQuery = "SELECT MIN(VOLUME) AS minVolume FROM TypeKidoro WHERE VOLUME > 0";
        double minVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getMinVolumeQuery)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                minVolume = rs.getDouble("minVolume");
            }
        }

        // Calculer le nombre total de produits possibles
        int totalProducts = (int) (blocVolume / minVolume);
        double remainingVolume = blocVolume - (totalProducts * minVolume);

        // Résultat pour preuve
        StringBuilder result = new StringBuilder("Calcul Minimum de Reste:\n");
        result.append("Volume le plus petit : ").append(minVolume).append("\n");
        result.append("Nombre total de produits possibles : ").append(totalProducts).append("\n");
        result.append("Volume restant : ").append(remainingVolume).append("\n");

        return result.toString();
    }
    private String estimationParLogiqueVolume(Connection connection, String idBloc) throws SQLException {
        // Récupérer le volume du bloc
        String getBlocVolumeQuery = "SELECT volume FROM Blocs WHERE idBloc = ?";
        double blocVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getBlocVolumeQuery)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                blocVolume = rs.getDouble("volume");
            }
        }

        // Récupérer les volumes et prix de vente des FU, triés par volume décroissant
        String getTypeKidoroQuery = "SELECT VOLUME, PRIX_VENTE FROM TypeKidoro ORDER BY VOLUME DESC";
        Map<Double, Double> fuData = new LinkedHashMap<>(); // Map<volume, prixVente>

        try (PreparedStatement stmt = connection.prepareStatement(getTypeKidoroQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double volumeFU = rs.getDouble("VOLUME");
                double prixVente = Double.parseDouble(rs.getString("PRIX_VENTE"));
                fuData.put(volumeFU, prixVente);
            }
        }

        double totalEstimatedPrice = 0.0;
        int totalFUCount = 0; // Nombre total de formes usuelles
        StringBuilder result = new StringBuilder("Estimation par logique volume:\n");

        // Calculer le nombre de chaque FU utilisé et le prix total
        for (Map.Entry<Double, Double> entry : fuData.entrySet()) {
            double volumeFU = entry.getKey();
            double prixVenteFU = entry.getValue();

            // Calculer le nombre de formes usuelles que l'on peut obtenir
            int count = (int) (blocVolume / volumeFU); // Nombre de fois que le volume de FU peut être extrait
            blocVolume -= count * volumeFU; // Mise à jour du volume du bloc après extraction
            double subtotal = count * prixVenteFU;
            totalEstimatedPrice += subtotal;
            totalFUCount += count;

            // Ajouter les détails pour preuve
            result.append("FU (Volume: ").append(volumeFU)
                    .append(") utilisé ").append(count)
                    .append(" fois, Sous-total: ").append(subtotal)
                    .append(" Ar\n");
        }

        result.append("Prix estimé total: ").append(totalEstimatedPrice).append(" Ar\n");
        //result.append("Nombre total de formes usuelles possibles: ").append(totalFUCount).append("\n");

        return result.toString();
    }

    private String estimationViaPVVolume(Connection connection, String idBloc) throws SQLException {
        // Récupérer le volume du bloc
        String getBlocVolumeQuery = "SELECT volume FROM Blocs WHERE idBloc = ?";
        double blocVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getBlocVolumeQuery)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                blocVolume = rs.getDouble("volume");
            }
        }

        // Calculer le meilleur rapport PV/Volume
        String getTypeKidoroQuery = "SELECT VOLUME, PRIX_VENTE FROM TypeKidoro";
        double bestRatio = 0.0;
        double bestVolumeFU = 0.0;
        double bestPricePerVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getTypeKidoroQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double volumeFU = rs.getDouble("VOLUME");
                double prixVenteFU = Double.parseDouble(rs.getString("PRIX_VENTE"));
                double currentRatio = prixVenteFU / volumeFU;

                if (currentRatio > bestRatio) {
                    bestRatio = currentRatio;
                    bestVolumeFU = volumeFU;
                    bestPricePerVolume = prixVenteFU;
                }
            }
        }

        // Calculer le volume restant après l'extraction des formes usuelles
        double remainingVolume = blocVolume;
        int numberOfFU = 0;
        if (remainingVolume > bestVolumeFU) {
            numberOfFU = (int) (remainingVolume / bestVolumeFU); // Nombre de formes usuelles possibles
            remainingVolume -= numberOfFU * bestVolumeFU; // Calcul du volume restant après extraction des formes usuelles
        }

        // Appliquer le meilleur rapport PV/Volume pour estimer le prix du bloc
        double estimatedPrice = numberOfFU * bestPricePerVolume;

        // Résultat pour preuve
        StringBuilder result = new StringBuilder("Estimation via PV/Volume:\n");
        result.append("Meilleur rapport PV/Volume: ").append(bestRatio)
                .append(" (FU Volume: ").append(bestVolumeFU)
                .append(", Prix de vente FU: ").append(bestPricePerVolume).append(" Ar)\n");
        result.append("Prix estimé total pour le bloc (sur formes usuelles): ").append(estimatedPrice).append(" Ar\n");
        result.append("Nombre de formes usuelles possibles: ").append(numberOfFU).append("\n");

        return result.toString();
    }
    private String formeUsuelleMaxBenefice(Connection connection, String idBloc) throws SQLException {
        // Étape 1 : Récupérer le volume du bloc
        String getBlocVolumeQuery = "SELECT volume FROM Blocs WHERE idBloc = ?";
        double blocVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getBlocVolumeQuery)) {
            stmt.setString(1, idBloc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                blocVolume = rs.getDouble("volume");
            }
        }

        // Étape 2 : Trouver la forme usuelle avec le bénéfice maximum par unité de volume
        String getBeneficeQuery = "SELECT tk.VOLUME, tk.PRIX_VENTE, v.MOYENNE_PONDEREE_PRIX_REV FROM TypeKidoro tk JOIN V_MOYENNE_P_PRIX_REV_KIDORO v ON tk.IDTYPEKIDORO = v.IDTYPEKIDORO";


        double maxBeneficeParVolume = 0.0;
        double bestVolumeFU = 0.0;
        double bestPricePerVolume = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(getBeneficeQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double volumeFU = rs.getDouble("VOLUME");
                double prixVenteFU = rs.getDouble("PRIX_VENTE");
                double prixRevientFU = rs.getDouble("MOYENNE_PONDEREE_PRIX_REV");
                double beneficeParVolume = prixVenteFU - prixRevientFU;

                if (beneficeParVolume > maxBeneficeParVolume) {
                    maxBeneficeParVolume = beneficeParVolume;
                    bestVolumeFU = volumeFU;
                    bestPricePerVolume = prixVenteFU;
                }
            }
        }

        // Étape 3 : Calcul du volume restant après extraction des formes usuelles
        double remainingVolume = blocVolume;
        int numberOfFU = 0;
        if (remainingVolume > bestVolumeFU) {
            numberOfFU = (int) (remainingVolume / bestVolumeFU); // Nombre de formes usuelles possibles
            remainingVolume -= numberOfFU * bestVolumeFU; // Volume restant après extraction
        }

        // Calcul du prix estimé pour le bloc en fonction des formes usuelles
        double estimatedPrice = numberOfFU * bestPricePerVolume;

        // Résultat
        StringBuilder result = new StringBuilder("Estimation de bénéfice maximal:\n");
        result.append("Forme usuelle avec le bénéfice maximal: ").append(maxBeneficeParVolume)
                .append(" (Volume: ").append(bestVolumeFU)
                .append(", Prix de vente: ").append(bestPricePerVolume).append(" Ar)\n");
        result.append("Prix estimé total pour le bloc (sur formes usuelles): ").append(estimatedPrice).append(" Ar\n");
        result.append("Nombre de formes usuelles possibles: ").append(numberOfFU).append("\n");
        result.append("Volume restant du bloc: ").append(remainingVolume).append("\n");

        return result.toString();
    }

}
