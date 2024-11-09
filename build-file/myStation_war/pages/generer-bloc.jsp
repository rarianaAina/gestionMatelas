<%@ page import="itu.station.kidoro.Blocs" %>
<%@ page import="bean.CGenUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Générer blocs</title>
    <script>
        function calculerVolume() {
            // Récupérer les valeurs des champs de saisie
            var longueur = parseFloat(document.getElementById("longueur").value) || 0;
            var largeur = parseFloat(document.getElementById("largeur").value) || 0;
            var hauteur = parseFloat(document.getElementById("hauteur").value) || 0;

            // Calculer le volume
            var volume = longueur * largeur * hauteur;

            // Afficher le volume dans le texte correspondant
            document.getElementById("volume").value = volume.toFixed(2);
            document.getElementById("volumeAffichage").innerText = "Volume: " + volume.toFixed(2) + " m³"; // Fixe à deux décimales
        }
    </script>
</head>
<body>
<h2>Génération Blocs</h2>

<form action="${pageContext.request.contextPath}/GenerationServlet" method="post">

    <div>
        <label for="dateGenerationBloc">Date de Génération :</label>
        <input type="date" id="dateGenerationBloc" name="dateGenerationBloc" required>
    </div>

    <label for="longueur">Longueur :</label>
    <input type="number" id="longueur" name="longueur" step="0.01" required oninput="calculerVolume()">
    <br><br>

    <label for="largeur">Largeur :</label>
    <input type="number" id="largeur" name="largeur" step="0.01" required oninput="calculerVolume()">
    <br><br>

    <label for="hauteur">Hauteur :</label>
    <input type="number" id="hauteur" name="hauteur" step="0.01" required oninput="calculerVolume()">
    <br><br>

    <label for="prixRevient">Prix de Revient :</label>
    <input type="number" id="prixRevient" name="prixRevient" step="0.01" required>
    <br><br>

    <p id="volumeAffichage" style="font-weight: bold;">Volume: 0.00 m³</p> <!-- Affichage du volume ici -->
    <input type="hidden" id="volume" name="volume" value="0.00">

    <button type="submit">Générer</button>
</form>
</body>
</html>
