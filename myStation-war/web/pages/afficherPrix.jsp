<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Afficher les prix de revient des machines</title>
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
<h1>Prix de revient des machines</h1>

<!-- Formulaire pour appeler la servlet -->
<form action="${pageContext.request.contextPath}/PriceServlet" method="GET">
    <input type="submit" value="Afficher les prix de revient des machines" />
</form>

<%
    // Récupérer la liste des machines et leurs prix de revient
    List<Map<String, Object>> prixMachines = (List<Map<String, Object>>) request.getAttribute("prixMachines");

    if (prixMachines != null && !prixMachines.isEmpty()) {
%>
<table>
    <thead>
    <tr>
        <th>Rang</th>
        <th>ID Machine</th>
        <th>Prix de revient théorique</th>
        <th>Prix de revient pratique</th>
        <th>Ecart</th>
    </tr>
    </thead>
    <tbody>
    <%
        // Affichage des prix des machines
        int rang = 1;  // Initialisation du rang
        for (Map<String, Object> machineData : prixMachines) {
            String idMachine = (String) machineData.get("idMachine");
            double prixTotal = (Double) machineData.get("prixTotal");
            double prixPratique = (Double) machineData.get("prixPratique");
            double ecart = (Double) machineData.get("ecart");
    %>
    <tr>
        <td><%= rang++ %></td> <!-- Affichage du rang -->
        <td><%= idMachine %></td>
        <td><%= prixTotal %></td>
        <td><%= prixPratique %></td>
        <td><%= ecart %></td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>
<%
} else {
%>
<p>Aucune donnée disponible pour afficher.</p>
<%
    }
%>

</body>
</html>
