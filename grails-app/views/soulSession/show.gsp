
<%@ page import="bv.SoulSession" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulSession.label', default: 'SoulSession')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-soulSession" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-soulSession" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list soulSession">
			
				<g:if test="${soulSessionInstance?.ipAddress}">
				<li class="fieldcontain">
					<span id="ipAddress-label" class="property-label"><g:message code="soulSession.ipAddress.label" default="Ip Address" /></span>
					
						<span class="property-value" aria-labelledby="ipAddress-label"><g:fieldValue bean="${soulSessionInstance}" field="ipAddress"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulSessionInstance?.sessionCode}">
				<li class="fieldcontain">
					<span id="sessionCode-label" class="property-label"><g:message code="soulSession.sessionCode.label" default="Session Code" /></span>
					
						<span class="property-value" aria-labelledby="sessionCode-label"><g:fieldValue bean="${soulSessionInstance}" field="sessionCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulSessionInstance?.soulId}">
				<li class="fieldcontain">
					<span id="soulId-label" class="property-label"><g:message code="soulSession.soulId.label" default="Soul Id" /></span>
					
						<span class="property-value" aria-labelledby="soulId-label"><g:fieldValue bean="${soulSessionInstance}" field="soulId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulSessionInstance?.time}">
				<li class="fieldcontain">
					<span id="time-label" class="property-label"><g:message code="soulSession.time.label" default="Time" /></span>
					
						<span class="property-value" aria-labelledby="time-label"><g:fieldValue bean="${soulSessionInstance}" field="time"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${soulSessionInstance?.id}" />
					<g:link class="edit" action="edit" id="${soulSessionInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
