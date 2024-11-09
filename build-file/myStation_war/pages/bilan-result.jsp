<%@page import="finance.*"%>
<%
    EtatDeFinance finance = (EtatDeFinance) session.getAttribute("etatFinancier");

%>
<div>
<h1>Résultat</h1>
<p>Bénéfices:<%= finance.getBenefice().getBenefMontant() %> </p>
<p>Dettes:<%= finance.getDettes().getAmount() %> </p>
<p>Créances:<%= finance.getCreances().getAmount() %> </p>
</div>
