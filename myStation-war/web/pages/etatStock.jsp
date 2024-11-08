<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="itu.station.kidoro.Blocs"%>
<%@ page import="java.sql.*"%>
<%@ page import="utilitaire.*"%>
<%@ page import="itu.station.kidoro.Blocs" %>
<%@ page import="utilitaire.UtilDB" %>
<%@ page import="java.sql.Connection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  // Connexion à la base de données
  Connection connection = null;
  Blocs[] blocsNonTransformes = null;

  try {
    connection = new UtilDB().GetConn("mystation", "mystation");
    Blocs blocs = new Blocs();

    // Récupérer les blocs non transformés
    blocsNonTransformes = blocs.getAllBlocsNonTransformes(connection);
  } catch (Exception e) {
    e.printStackTrace();
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
<html>
<head>
  <meta charset="UTF-8">
  <title>Etat de Stock</title>
  <style>
    table {
      width: 100%;
      border-collapse: collapse;
    }
    table, th, td {
      border: 1px solid black;
      text-align: center;
      padding: 8px;
    }
    th {
      background-color: #f2f2f2;
    }
  </style>
</head>
<body>
<h2>Etat de Stock</h2>

<!-- Formulaire pour appeler la servlet -->
<form action="${pageContext.request.contextPath}/EtatStockServlet" method="GET">
  <input type="submit" value="Afficher le stock" />
</form>

<!-- Table des données de stock -->
<table>
  <thead>
  <tr>
    <th>Type</th>
    <th>Qté</th>
    <th>Volume Unitaire</th>
    <th>Prix de Vente Unitaire</th>
    <th>Prix de Revient Moyen</th>
    <th>Volume Total</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="entry" items="${stockData}">
    <tr>
      <td>${entry.key}</td>
      <td>${entry.value.qte}</td>
      <td>${entry.value.volume}</td>
      <td>${entry.value.prixVente}</td>
      <td>${entry.value.prixRevientMoyen}</td>
      <td><c:out value="${entry.value.qte * entry.value.volume}" /></td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<!-- Ligne ajoutée pour afficher les blocs non transformés "Bloc reste" -->
<h3>Bloc reste</h3>
<table border="1">
  <thead>
  <tr>
    <th>ID</th>
    <th>Volume</th>
    <th>Type Bloc</th>
    <th>Bloc mère</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="bloc" items="${blocsNonTransformes}">
    <tr>
      <td><a>${bloc.idBloc}</a></td>
      <td><a>${bloc.volume}</a></td>
      <td>Transformable (Reste)</td>
      <td>${bloc.idBLocInitial}</td>
  </c:forEach>

  <c:if test="${empty blocsNonTransformes}">
    <tr>
      <td colspan="7">Aucun bloc non transformé disponible.</td>
    </tr>
  </c:if>

  </tbody>
</table>
<!-- Moyenne pondérée des prix de revient des Kidoro par type -->
<h2>Moyenne pondérée des prix de revient des Kidoro par type</h2>
<table>
  <thead>
  <tr>
    <th>Type de Kidoro</th>
    <th>Moyenne pondérée du prix de revient</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="entry" items="${stockData}">
    <tr>
      <td>${entry.key}</td>
      <td>${entry.value.prixRevientMoyen}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>

</body>
</html>
