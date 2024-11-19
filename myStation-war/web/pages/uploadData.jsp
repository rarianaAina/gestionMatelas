<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Importer CSV</title>
</head>
<body>
<h1>Importer un fichier CSV</h1>
<form action="${pageContext.request.contextPath}/UploadCSVServlet" method="post" enctype="multipart/form-data">
    <label for="fileInput">Sélectionner un fichier CSV :</label>
    <input type="file" id="fileInput" name="file" accept=".csv" required>
    <input type="submit" value="Importer">
</form>
</body>
</html>
