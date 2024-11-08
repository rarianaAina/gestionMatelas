package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Blocs extends ClassMAPTable {

    private String idBloc;
    private double longueur;
    private double largeur;
    private double hauteur;
    private double volume;
    private double prixDeRevient;



    private Date date_creation_bloc;
    private String idBLocInitial;



    private String idSource;

    public Blocs(String idBloc, double longueur, double largeur, double hauteur, double volume, double prixDeRevient, String idBLocInitial, Date date_creation_bloc, String idSource) {
        this.idBloc = idBloc;
        this.longueur = longueur;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.volume = volume;
        this.prixDeRevient = prixDeRevient;
        this.idBLocInitial = idBLocInitial;
        this.date_creation_bloc = date_creation_bloc;
        this.idSource = idSource;
    }

    public Blocs() {
        this.setNomTable("BLOCS");
    }

    @Override
    public String getTuppleID() {
        return idBloc;
    }

    @Override
    public String getAttributIDName() {
        return "idBloc";
    }

    // Getters et setters

    public String getIdBloc() {
        return idBloc;
    }

    public void setIdBloc(String idBloc) {
        this.idBloc = idBloc;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur() {
        return hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public Date getDate_creation_bloc() {
        return date_creation_bloc;
    }

    public void setDate_creation_bloc(Date date_creation_bloc) {
        this.date_creation_bloc = date_creation_bloc;
    }

    public String getIdBLocInitial() {
        return idBLocInitial;
    }

    public void setIdBLocInitial(String idBLocInitial) {
        this.idBLocInitial = idBLocInitial;
    }
    public double getPrixDeRevient() {
        return prixDeRevient;
    }

    public void setPrixDeRevient(double prixDeRevient) {
        this.prixDeRevient = prixDeRevient;
    }

    public String getIdSource() {
        return idSource;
    }

    public void setIdSource(String idSource) {
        this.idSource = idSource;
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("BLC", "GET_BLOC_SEQ");
        this.setIdBloc(makePK(c));
    }
    public Blocs[] getAllBlocs(Connection c) throws Exception {

        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }
        // Récupérer tous les blocs en utilisant une condition vide
        return (Blocs[]) CGenUtil.rechercher(new Blocs(), null, null, c, "");
    }

    public Blocs[] getAllBlocsNonTransformes(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }

        // Préparez la requête pour récupérer les blocs non transformés
        String query = "SELECT * FROM BLOCS b WHERE b.idBloc NOT IN (SELECT idBloc FROM TRANSFORMATION)";

        List<Blocs> blocsNonTransformes = new ArrayList<>();
        try (PreparedStatement stmt = c.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Blocs bloc = new Blocs();
                bloc.setIdBloc(rs.getString("idBloc"));
                bloc.setLongueur(rs.getDouble("longueur"));
                bloc.setLargeur(rs.getDouble("largeur"));
                bloc.setHauteur(rs.getDouble("hauteur"));
                bloc.setVolume(rs.getDouble("volume"));
                bloc.setPrixDeRevient(rs.getDouble("prixDeRevient"));
                bloc.setIdBLocInitial(rs.getString("idblocinitial"));
                bloc.setDate_creation_bloc(rs.getDate("date_creation_bloc"));

                blocsNonTransformes.add(bloc);
            }
        }

        // Convertir la liste en tableau
        return blocsNonTransformes.toArray(new Blocs[0]);
    }

    public void insererSource(Connection connection, String newIdBloc, String idBlocSourceInitial, String idBlocParent) throws SQLException {
        // Récupération des paramètres nécessaires à l'insertion dans la table Source

        // Définir la requête SQL pour l'insertion
        String insertQuery = "INSERT INTO Source (IDBLOCACTUEL, IDBLOCSOURCEINITIAL, IDBLOCPARENT) VALUES (?, ?, ?)";

        // Préparer la requête SQL
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            // Remplir les paramètres de la requête
            ps.setString(1, newIdBloc);  // Insertion du nouvel ID du bloc
            ps.setString(2, idBlocSourceInitial);  // Insertion de l'ID du bloc source initial
            ps.setString(3, idBlocParent);  // Insertion de l'ID du bloc parent ou de référence

            // Exécuter l'insertion
            ps.executeUpdate();
            System.out.println("Insertion dans la table Source réussie.");
        } catch (SQLException e) {
            // Gérer les exceptions et erreurs SQL
            e.printStackTrace();
            throw new SQLException("Erreur lors de l'insertion dans la table Source", e);
        }
    }

    public void insererBloc(Connection connection) throws SQLException {
        // Obtenez le prochain numéro de la séquence
        String sqlSeq = "SELECT BLOCS_SEQ.NEXTVAL AS nextId FROM dual";
        try (PreparedStatement seqStatement = connection.prepareStatement(sqlSeq);
             ResultSet seqResultSet = seqStatement.executeQuery()) {
            if (seqResultSet.next()) {
                int nextId = seqResultSet.getInt("nextId");
                this.idBloc = String.format("BLC%03d", nextId); // Formatez l'ID avec trois chiffres
                this.idBLocInitial = this.idBloc; // Assignez la même valeur pour idBlocInitial
                this.idSource = this.idBloc;
            }
        }

        // Log les valeurs pour débogage
        System.out.println("idBloc: " + this.idBloc);
        System.out.println("idBlocInitial: " + this.idBLocInitial);

        // Insérez le bloc
        String sql = "INSERT INTO BLOCS (idBloc, longueur, largeur, hauteur, volume, prixDeRevient, idblocinitial, date_creation_bloc, idsource) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.idBloc);
            preparedStatement.setDouble(2, this.longueur);
            preparedStatement.setDouble(3, this.largeur);
            preparedStatement.setDouble(4, this.hauteur);
            preparedStatement.setDouble(5, this.volume);
            preparedStatement.setDouble(6, this.prixDeRevient);
            preparedStatement.setString(7, this.idBLocInitial); // Utilisez idBlocInitial ici
            preparedStatement.setDate(8, this.date_creation_bloc); // Assurez-vous que la date est de type Date
            preparedStatement.setString(9, this.idSource);
            preparedStatement.executeUpdate();
        }
        insererSource(connection, this.idBloc, this.idBloc, this.idBloc);
    }




}
