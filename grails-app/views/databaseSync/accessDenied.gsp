<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="auth.User;" %>
<%@ page import="bv.BudgetViewDatabaseService; bv.UserLog; auth.User; org.springframework.security.core.context.SecurityContextHolder; org.springframework.security.core.Authentication; org.springframework.security.core.context.SecurityContext" %>


<html>
<head>
    <meta name="layout" content="main">
    <title>Access Denied</title>
</head>

<body>
<div id="list-page-body-inner" class="content white-background scaffold-create" role="main" style="">

    <div class="searchField" style=" margin: 0 0 0 -2px;width: 1001px;">
    </div>

    <div class="boxouter boxouter-modified" style="float: left;width: 99.8%;">
        <div class="dataTablAcsDeny">
            <p>Access Denied.</p>
        </div>
    </div>  <!--boxouter-->
</div> <!--content-->
</body>
</html>