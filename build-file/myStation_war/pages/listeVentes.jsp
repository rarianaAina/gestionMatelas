<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="itu.station.tools.Vente"%>
<%@ page import="java.sql.*"%>
<%@ page import="bean.*"%>
<%@ page import="utilitaire.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>

<%
    Connection connection = null;
    Vente[] ventes = null;

    String dateParam = request.getParameter("date");
    System.out.println("dateParam: " + dateParam);
    Date selectedDate = null;

    if (dateParam != null && !dateParam.isEmpty()) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            selectedDate = sdf.parse(dateParam);
        } catch (Exception e) {
            e.printStackTrace();
            selectedDate = null; // En cas d'erreur, aucune date sélectionnée
        }
    }

    try {
        // Connexion à la base de données
        connection = new UtilDB().GetConn("gallois", "gallois");
        System.out.println("Date sélectionnée : " + selectedDate);
        // Récupérer les ventes en fonction de la date sélectionnée
        Vente vente = new Vente();
        if (selectedDate == null) {
            System.out.println("Aucune date sélectionnée, récupération de toutes les ventes.");
            ventes = vente.getAllVentes(connection);
        } else {
            System.out.println("Date sélectionnée : " + selectedDate);
            ventes = vente.getVentesByDate(connection, new java.sql.Date(selectedDate.getTime()));
        }

        // Ajouter les ventes à la portée de requête
        request.setAttribute("ventes", ventes);

    } catch (Exception e) {
        e.printStackTrace();
%>
<script>alert('Erreur lors de la récupération des ventes.');</script>
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
<head>
    <title>Liste des Ventes</title>
</head>
<body>
<h1>Liste des Ventes</h1>

<!-- Formulaire pour sélectionner une date -->
<form method="get" action="${pageContext.request.contextPath}/index.jsp">
    <input type="hidden" name="but" value="pages/listeVentes.jsp" />
    <label for="date">Sélectionnez une date :</label>
    <input type="date" id="date" name="date" value="<c:out value='${param.date}'/>" />
    <input type="submit" value="Afficher les ventes" />
</form>

<table border="1">
    <thead>
    <tr>
        <th>ID</th>
        <th>Date</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="vente" items="${ventes}">
        <tr>
            <td><a href="${pageContext.request.contextPath}/index.jsp?but=pages/factureVente.jsp&idFacture=${vente.id}&daty=${vente.daty}">${vente.id}</a></td>
            <td>${vente.daty}</td>
        </tr>
    </c:forEach>

    <c:if test="${empty ventes}">
        <tr>
            <td colspan="2">Aucune vente disponible.</td>
        </tr>
    </c:if>
    </tbody>
</table>

</body>
</html>
