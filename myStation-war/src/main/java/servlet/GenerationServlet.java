package servlet;

import itu.station.kidoro.Blocs; // Importez la classe Blocs
import utilitaire.UtilDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

@WebServlet(name = "GenerationBlocs", urlPatterns = {"/GenerationServlet"})
public class GenerationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Récupération des paramètres depuis la requête
            Date date_creation = Date.valueOf(request.getParameter("dateGenerationBloc"));
            double longueur = Double.parseDouble(request.getParameter("longueur"));
            double largeur = Double.parseDouble(request.getParameter("largeur"));
            double hauteur = Double.parseDouble(request.getParameter("hauteur"));
            double prixRevient = Double.parseDouble(request.getParameter("prixRevient"));
            double volume = Double.parseDouble(request.getParameter("volume"));

            // Création d'un nouvel objet Blocs
            Blocs bloc = new Blocs();
            bloc.setLongueur(longueur);
            bloc.setLargeur(largeur);
            bloc.setHauteur(hauteur);
            bloc.setPrixDeRevient(prixRevient);
            bloc.setVolume(volume);
            bloc.setDate_creation_bloc(date_creation);



            // Établissement de la connexion à la base de données
            Connection connection = new UtilDB().GetConn("mystation", "mystation");

            // Insertion du bloc dans la base de données
            bloc.insererBloc(connection);

            // Engagement de la transaction
            connection.commit();
            response.sendRedirect("index.jsp?but=pages/generer-bloc.jsp");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
