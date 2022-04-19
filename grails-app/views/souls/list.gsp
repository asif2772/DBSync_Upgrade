
<%@ page import="bv.Souls" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'souls.label', default: 'Souls')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-souls" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-souls" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="activationCode" title="${message(code: 'souls.activationCode.label', default: 'Activation Code')}" />
					
						<g:sortableColumn property="email" title="${message(code: 'souls.email.label', default: 'Email')}" />
					
						<g:sortableColumn property="failAttempt" title="${message(code: 'souls.failAttempt.label', default: 'Fail Attempt')}" />
					
						<g:sortableColumn property="isBlock" title="${message(code: 'souls.isBlock.label', default: 'Is Block')}" />
					
						<g:sortableColumn property="isSendEmail" title="${message(code: 'souls.isSendEmail.label', default: 'Is Send Email')}" />
					
						<g:sortableColumn property="lastvisitDate" title="${message(code: 'souls.lastvisitDate.label', default: 'Lastvisit Date')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${soulsInstanceList}" status="i" var="soulsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${soulsInstance.id}">${fieldValue(bean: soulsInstance, field: "activationCode")}</g:link></td>
					
						<td>${fieldValue(bean: soulsInstance, field: "email")}</td>
					
						<td>${fieldValue(bean: soulsInstance, field: "failAttempt")}</td>
					
						<td>${fieldValue(bean: soulsInstance, field: "isBlock")}</td>
					
						<td>${fieldValue(bean: soulsInstance, field: "isSendEmail")}</td>
					
						<td><g:formatDate date="${soulsInstance.lastvisitDate}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${soulsInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
