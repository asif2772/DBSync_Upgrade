<%@ page import="auth.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title><g:message code="updatePassword" args="[entityName]" /></title>
</head>
<body>
<div id="edit-user" class="content scaffold-edit" role="main">
    <h1><g:message code="change.password.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:form method="post" action='updatePassword' id='${userId}'>
        <g:hiddenField name="id" value="${userInstance?.id}" />
        <g:hiddenField name="version" value="${userInstance?.version}" />
        <fieldset class="form">
                <div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
                    <label for="password">
                        <g:message code="user.newPassword.label" default="New Password" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:passwordField  name="newPassword" required="" value="" />
                </div>
                <div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password2', 'error')} required">
                    <label for="password">
                        <g:message code="user.confirmPassword.label" default="Confirm password" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:passwordField  name="confirmPassword" required="" value="" />
                </div>
                %{--<div class="fieldcontain" style="margin-top:0px">--}%
                    <div class="buttonClass" style="float:left">
                        <div class="buttonCLassInner">
                            <g:actionSubmit class="save" action="updatePassword" value="${message(code: 'user.updatePassword.button', default: 'Update Password')}" />
                        </div>
                    </div>
                %{--</div>--}%
        </fieldset>
    </g:form>
</div>
</body>
</html>
