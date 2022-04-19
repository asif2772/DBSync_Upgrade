
<%@ page import="bv.Souls" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'souls.label', default: 'Souls')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-souls" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-souls" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list souls">
			
				<g:if test="${soulsInstance?.activationCode}">
				<li class="fieldcontain">
					<span id="activationCode-label" class="property-label"><g:message code="souls.activationCode.label" default="Activation Code" /></span>
					
						<span class="property-value" aria-labelledby="activationCode-label"><g:fieldValue bean="${soulsInstance}" field="activationCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label"><g:message code="souls.email.label" default="Email" /></span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${soulsInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.failAttempt}">
				<li class="fieldcontain">
					<span id="failAttempt-label" class="property-label"><g:message code="souls.failAttempt.label" default="Fail Attempt" /></span>
					
						<span class="property-value" aria-labelledby="failAttempt-label"><g:fieldValue bean="${soulsInstance}" field="failAttempt"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.isBlock}">
				<li class="fieldcontain">
					<span id="isBlock-label" class="property-label"><g:message code="souls.isBlock.label" default="Is Block" /></span>
					
						<span class="property-value" aria-labelledby="isBlock-label"><g:fieldValue bean="${soulsInstance}" field="isBlock"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.isSendEmail}">
				<li class="fieldcontain">
					<span id="isSendEmail-label" class="property-label"><g:message code="souls.isSendEmail.label" default="Is Send Email" /></span>
					
						<span class="property-value" aria-labelledby="isSendEmail-label"><g:fieldValue bean="${soulsInstance}" field="isSendEmail"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.lastvisitDate}">
				<li class="fieldcontain">
					<span id="lastvisitDate-label" class="property-label"><g:message code="souls.lastvisitDate.label" default="Lastvisit Date" /></span>
					
						<span class="property-value" aria-labelledby="lastvisitDate-label"><g:formatDate date="${soulsInstance?.lastvisitDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="souls.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${soulsInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.password}">
				<li class="fieldcontain">
					<span id="password-label" class="property-label"><g:message code="souls.password.label" default="Password" /></span>
					
						<span class="property-value" aria-labelledby="password-label"><g:fieldValue bean="${soulsInstance}" field="password"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.registerDate}">
				<li class="fieldcontain">
					<span id="registerDate-label" class="property-label"><g:message code="souls.registerDate.label" default="Register Date" /></span>
					
						<span class="property-value" aria-labelledby="registerDate-label"><g:formatDate date="${soulsInstance?.registerDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.securityAnswer}">
				<li class="fieldcontain">
					<span id="securityAnswer-label" class="property-label"><g:message code="souls.securityAnswer.label" default="Security Answer" /></span>
					
						<span class="property-value" aria-labelledby="securityAnswer-label"><g:fieldValue bean="${soulsInstance}" field="securityAnswer"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.securityQuestion}">
				<li class="fieldcontain">
					<span id="securityQuestion-label" class="property-label"><g:message code="souls.securityQuestion.label" default="Security Question" /></span>
					
						<span class="property-value" aria-labelledby="securityQuestion-label"><g:fieldValue bean="${soulsInstance}" field="securityQuestion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.sendAwareNotification}">
				<li class="fieldcontain">
					<span id="sendAwareNotification-label" class="property-label"><g:message code="souls.sendAwareNotification.label" default="Send Aware Notification" /></span>
					
						<span class="property-value" aria-labelledby="sendAwareNotification-label"><g:fieldValue bean="${soulsInstance}" field="sendAwareNotification"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.soulGroupId}">
				<li class="fieldcontain">
					<span id="soulGroupId-label" class="property-label"><g:message code="souls.soulGroupId.label" default="Soul Group Id" /></span>
					
						<span class="property-value" aria-labelledby="soulGroupId-label"><g:fieldValue bean="${soulsInstance}" field="soulGroupId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="souls.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${soulsInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.transactionCode}">
				<li class="fieldcontain">
					<span id="transactionCode-label" class="property-label"><g:message code="souls.transactionCode.label" default="Transaction Code" /></span>
					
						<span class="property-value" aria-labelledby="transactionCode-label"><g:fieldValue bean="${soulsInstance}" field="transactionCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulsInstance?.username}">
				<li class="fieldcontain">
					<span id="username-label" class="property-label"><g:message code="souls.username.label" default="Username" /></span>
					
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${soulsInstance}" field="username"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${soulsInstance?.id}" />
					<g:link class="edit" action="edit" id="${soulsInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
