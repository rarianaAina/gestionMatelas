<%@ page import="itu.station.jauge.anomalie.AnomalieCumul" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Détection d'Anomalie</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        /* Style du conteneur principal */
        .container {
            max-width: 500px;
            margin: 20px auto;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            font-family: Arial, sans-serif;
        }

        /* Style des messages d'anomalie */
        .anomaly-message {
            font-size: 16px;
            margin: 10px 0;
            padding: 15px;
            border-radius: 5px;
        }

        /* Message d'anomalie détectée */
        .anomaly-detected {
            background-color: #ffcccc;
            color: #a94442;
            border: 1px solid #a94442;
        }

        /* Message aucune anomalie */
        .no-anomaly {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        /* Message du cumul d'anomalie */
        .cumulative-anomaly {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeeba;
        }
    </style>
</head>
<body>
<div class="container">
    <%
        double val = (double) request.getAttribute("anomalie");
        if (val != 0) {
    %>
    <p class="anomaly-message anomaly-detected">
        Anomalie détectée par rapport aux mesures d'hier : il y a un écart de <strong><%= val %></strong> litres.
    </p>
    <%
    } else {
    %>
    <p class="anomaly-message no-anomaly">
        Aucune anomalie détectée par rapport aux mesures d'hier.
    </p>
    <%
        }

        if (request.getAttribute("anomalieCumul") != null) {
            AnomalieCumul anom = (AnomalieCumul) request.getAttribute("anomalieCumul");
    %>
    <p class="anomaly-message cumulative-anomaly">
        Depuis la première jauge, le cumul des anomalies est de <strong><%= anom.getCumul() %></strong> litres.
    </p>
    <%
        }
    %>
</div>
</body>
</html>
