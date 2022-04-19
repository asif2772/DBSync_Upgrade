
<%@ page import="bv.SoulSession" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulSession.label', default: 'SoulSession')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-soulSession" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-soulSession" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="ipAddress" title="${message(code: 'soulSession.ipAddress.label', default: 'Ip Address')}" />
					
						<g:sortableColumn property="sessionCode" title="${message(code: 'soulSession.sessionCode.label', default: 'Session Code')}" />
					
						<g:sortableColumn property="soulId" title="${message(code: 'soulSession.soulId.label', default: 'Soul Id')}" />
					
						<g:sortableColumn property="time" title="${message(code: 'soulSession.time.label', default: 'Time')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${soulSessionInstanceList}" status="i" var="soulSessionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${soulSessionInstance.id}">${fieldValue(bean: soulSessionInstance, field: "ipAddress")}</g:link></td>
					
						<td>${fieldValue(bean: soulSessionInstance, field: "sessionCode")}</td>
					
						<td>${fieldValue(bean: soulSessionInstance, field: "soulId")}</td>
					
						<td>${fieldValue(bean: soulSessionInstance, field: "time")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${soulSessionInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
