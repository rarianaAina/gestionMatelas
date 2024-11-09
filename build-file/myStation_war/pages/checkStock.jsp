<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Vérification Anomalie</title>
</head>
<body>
<h2>Vérification Anomalie</h2>

<form action="verifierStock.jsp" method="post">
    <label for="stockReel">Stock Réel :</label>
    <input type="number" id="stockReel" name="stockReel" step="0.01" required>
    <br><br>

    <button type="submit">Checker</button>
</form>
</body>
</html>
