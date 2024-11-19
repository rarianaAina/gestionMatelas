<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Prix des Machines</title>
    <style>
        table {
            width: 50%;
            margin: 20px auto;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: center;
        }
    </style>
</head>
<body>

<h2 style="text-align: center;">Liste des Prix de Revient des Machines</h2>

<!-- Affichage de la table -->
<table>
    <thead>
    <tr>
        <th>ID Machine</th>
        <th>Prix Total</th>
    </tr>
    </thead>
    <tbody>
    <!-- Boucle sur la liste des prix de machines -->
    <c:forEach var="machine" items="${prixMachines}">
        <tr>
            <td>${machine.idMachine}</td>
            <td>${machine.prixTotal}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
