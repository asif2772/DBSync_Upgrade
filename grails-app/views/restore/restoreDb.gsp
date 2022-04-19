<%@ page import="auth.User;" %>
<%@ page import="bv.BudgetViewDatabaseService; bv.UserLog; auth.User; org.springframework.security.core.context.SecurityContextHolder; org.springframework.security.core.Authentication; org.springframework.security.core.context.SecurityContext" %>

<%
    SecurityContext ctx = SecurityContextHolder.getContext()
    Authentication auth = ctx.getAuthentication()
    String username = auth.getName()
    User user = User.findByUsername(username)
    Integer authUserId = user.getAt('id')
%>

<html>
<head>
    <meta name="layout" content="main">
    <title>DB Restore</title>
</head>

<body>
%{--<%if(authUserId==1){%>--}%
<% def contextPath = request.getServletContext().getContextPath()%>

<div id="list-page-body-inner" class="content white-background scaffold-create" role="main" style="">
    %{--<div id="list-page">--}%
    <div class="searchField" style=" margin: 0 0 0 -2px;width: 1001px;">
        <label class="searchHeadLineLabel">DB Restore</label>
    </div>


        <g:uploadForm action="excuteFile" method="post" style="margin: -1px 1px 1px -1px;">
            <div class="boxouter boxouter-modified" style="float: left;padding: 10px 1px 0 21px; width: 97.7%;">
        %{--<g:uploadForm action="saveFile1" method="post">--}%

            <div class="fieldContainerLeft">
                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText borderRed">
                        <label for="payload">
                            <g:message code="bv.bankStatement.username.label" default="User Name: "/>
                        </label>
                        <input type="text" id="username" name="username" style="height:23px;"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="payload">
                            <g:message code="bv.bankStatement.file.label" default="File: "/>
                        </label>
                        <input type="file" id="payload" name="payload" style="height:23px;"/>
                    </div>
                </div>

            </div>

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="fieldContainer required fcInputPassword">
                        <label for="payload">
                            <g:message code="bv.bankStatement.password.label" default="password: " />
                        </label><br>
                        %{--<input type="password"  id="password" name="password"/>--}%
                        <input type="password" required="" id="password" name="password"/>
                    </div>
                </div>

                <div class="rowContainer po">
                    <div class="buttonClass_mr0">
                        <div class="buttonCLassInner" style="float: left;margin-top: 1px;">
                            %{--<div class="btnDivStyle btnDivPosition">--}%
                            <input class="" type="submit" value="${message(code: 'bv.dbSync.upload.label', default: 'Upload')}"/>
                        </div>
                    </div>
                </div>
            </div>
         </div>
        </g:uploadForm>


    <%println(params.finalResult)%>



</div> <!--content-->

</body>

</html>