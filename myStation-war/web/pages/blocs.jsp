<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="itu.station.kidoro.Blocs"%>
<%@ page import="java.sql.*"%>
<%@ page import="utilitaire.*"%>

<%
  Blocs[] blocsArray = null;
  Blocs[] blocsNonTransformes = null;
  Connection connection = null;
  try {
    // Connexion à la base de données
    connection = new UtilDB().GetConn("mystation", "mystation");

    // Création d'une instance de Blocs
    Blocs blocs = new Blocs();

    // Récupérer tous les blocs
    blocsArray = blocs.getAllBlocs(connection);

    // Récupérer les blocs non transformés
    blocsNonTransformes = blocs.getAllBlocsNonTransformes(connection);

    // Ajouter les blocs à la portée de requête
    request.setAttribute("blocs", blocsArray);
    request.setAttribute("blocsNonTransformes", blocsNonTransformes);

  } catch (Exception e) {
    e.printStackTrace();
%>
<script>alert('Erreur lors de la récupération des blocs.');</script>
<%
  } finally {
    if (connection != null) {
      try {
        connection.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Liste des blocs</title>
</head>
<body>
<h1>Liste des Blocs</h1>

<!-- Table pour tous les blocs -->
<h2>Tous les Blocs</h2>
<table border="1">
  <thead>
  <tr>
    <th>ID</th>
    <th>Volume</th>
    <th>Bloc mère</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="bloc" items="${blocs}">
    <tr>
      <td><a>${bloc.idBloc}</a></td>
      <td><a>${bloc.volume}</a></td>
      <td>${bloc.idBLocInitial}</td>
    </tr>
  </c:forEach>

  <c:if test="${empty blocs}">
    <tr>
      <td colspan="6">Aucun bloc disponible.</td>
    </tr>
  </c:if>

  </tbody>
</table>

<!-- Table pour les blocs non transformés -->
<h2>Blocs Non Transformés</h2>
<table border="1">
  <thead>
  <tr>
    <th>ID</th>
    <th>Volume</th>
    <th>Type Bloc</th>
    <th>Bloc mère</th>
    <th>Estimation par Logique volume</th>
    <th>Estimation via PV/Volume</th>
    <th>Minimum de Reste</th> <!-- Nouvelle colonne -->
  </tr>
  </thead>
  <tbody>
  <c:forEach var="bloc" items="${blocsNonTransformes}">
    <tr>
      <td><a>${bloc.idBloc}</a></td>
      <td><a>${bloc.volume}</a></td>
      <td>Transformable (Reste)</td>
      <td>${bloc.idBLocInitial}</td>

      <!-- Bouton pour l'estimation par logique volume -->
      <td>
        <form action="${pageContext.request.contextPath}/EstimationServlet" method="post">
          <input type="hidden" name="idBloc" value="${bloc.idBloc}" />
          <input type="hidden" name="action" value="logicVolume" />
          <button type="submit">Estimation par Logique volume</button>
        </form>
      </td>

      <!-- Bouton pour l'estimation via PV/Volume -->
      <td>
        <form action="${pageContext.request.contextPath}/EstimationServlet" method="post">
          <input type="hidden" name="idBloc" value="${bloc.idBloc}" />
          <input type="hidden" name="action" value="pvVolume" />
          <button type="submit">Estimation via PV/Volume</button>
        </form>
      </td>

      <!-- Bouton pour le calcul du Minimum de Reste -->
      <td>
        <form action="${pageContext.request.contextPath}/EstimationServlet" method="post">
          <input type="hidden" name="idBloc" value="${bloc.idBloc}" />
          <input type="hidden" name="action" value="minimumReste" />
          <button type="submit">Minimum de Reste</button>
        </form>
      </td>
    </tr>
  </c:forEach>

  <c:if test="${empty blocsNonTransformes}">
    <tr>
      <td colspan="7">Aucun bloc non transformé disponible.</td>
    </tr>
  </c:if>

  </tbody>
</table>
</body>
</html>
