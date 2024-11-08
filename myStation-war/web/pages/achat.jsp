<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>GeAnaFront - Form</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        /* Style général du formulaire */
        form {
            max-width: 450px;
            margin: 40px auto;
            padding: 25px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            font-family: Arial, sans-serif;
        }

        /* Style des labels */
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: 600;
            font-size: 14px;
        }

        /* Style des champs de saisie et de sélection */
        input[type="number"],
        input[type="date"],
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s ease;
        }

        input[type="number"]:focus,
        input[type="date"]:focus,
        select:focus {
            border-color: #4CAF50;
            outline: none;
        }

        /* Style du bouton de soumission */
        input[type="submit"] {
            width: 100%;
            padding: 12px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }

        /* Style pour la div d'emballage des éléments */
        .form-group {
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<%@page import="itu.station.tools.Cuve"%>
<%@page import="java.sql.*"%>
<%@page import="bean.*"%>
<%@page import="utilitaire.*"%>
<%
    Connection connection = new UtilDB().GetConn();
    Cuve[] cuves = (Cuve[]) CGenUtil.rechercher(new Cuve(), null, null, connection, " ");
    connection.close();
%>
<form action="${pageContext.request.contextPath}/achat" method="post">
    <div class="form-group">
        <label>Quantité</label>
        <input type="number" name="qte" placeholder="Entrez la quantité" required>
    </div>

    <div class="form-group">
        <label>Cuve</label>
        <select name="idCuve" id="Pompiste" required>
            <%
                for(int i = 0; i < cuves.length; i++) {
            %>
            <option value="<%=cuves[i].getId()%>"><%=cuves[i].getLabel()%></option>
            <%
                }
            %>
        </select>
    </div>

    <div class="form-group">
        <label>Date</label>
        <input type="date" name="daty" required>
    </div>

    <div class="form-group">
        <input type="submit" value="Envoyer">
    </div>
</form>
</body>
</html>
