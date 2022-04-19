
<%@ page import="bv.SoulLogs" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulLogs.label', default: 'SoulLogs')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-soulLogs" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-soulLogs" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list soulLogs">
			
				<g:if test="${soulLogsInstance?.endTime}">
				<li class="fieldcontain">
					<span id="endTime-label" class="property-label"><g:message code="soulLogs.endTime.label" default="End Time" /></span>
					
						<span class="property-value" aria-labelledby="endTime-label"><g:formatDate date="${soulLogsInstance?.endTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulLogsInstance?.ipAddress}">
				<li class="fieldcontain">
					<span id="ipAddress-label" class="property-label"><g:message code="soulLogs.ipAddress.label" default="Ip Address" /></span>
					
						<span class="property-value" aria-labelledby="ipAddress-label"><g:fieldValue bean="${soulLogsInstance}" field="ipAddress"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulLogsInstance?.loginAttempt}">
				<li class="fieldcontain">
					<span id="loginAttempt-label" class="property-label"><g:message code="soulLogs.loginAttempt.label" default="Login Attempt" /></span>
					
						<span class="property-value" aria-labelledby="loginAttempt-label"><g:fieldValue bean="${soulLogsInstance}" field="loginAttempt"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulLogsInstance?.soulId}">
				<li class="fieldcontain">
					<span id="soulId-label" class="property-label"><g:message code="soulLogs.soulId.label" default="Soul Id" /></span>
					
						<span class="property-value" aria-labelledby="soulId-label"><g:fieldValue bean="${soulLogsInstance}" field="soulId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulLogsInstance?.startTime}">
				<li class="fieldcontain">
					<span id="startTime-label" class="property-label"><g:message code="soulLogs.startTime.label" default="Start Time" /></span>
					
						<span class="property-value" aria-labelledby="startTime-label"><g:formatDate date="${soulLogsInstance?.startTime}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${soulLogsInstance?.id}" />
					<g:link class="edit" action="edit" id="${soulLogsInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
