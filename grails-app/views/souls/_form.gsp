<%@ page import="bv.Souls" %>
<calendar:resources lang="en" theme="tiger"/>


<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'activationCode', 'error')} ">
	<label for="activationCode">
		<g:message code="souls.activationCode.label" default="Activation Code" />
		
	</label>
	<g:textField name="activationCode" value="${soulsInstance?.activationCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'email', 'error')} ">
	<label for="email">
		<g:message code="souls.email.label" default="Email" />
		
	</label>
	<g:textField name="email" value="${soulsInstance?.email}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'failAttempt', 'error')} required">
	<label for="failAttempt">
		<g:message code="souls.failAttempt.label" default="Fail Attempt" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="failAttempt" type="number" value="${soulsInstance.failAttempt}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'isBlock', 'error')} required">
	<label for="isBlock">
		<g:message code="souls.isBlock.label" default="Is Block" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="isBlock" type="number" value="${soulsInstance.isBlock}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'isSendEmail', 'error')} required">
	<label for="isSendEmail">
		<g:message code="souls.isSendEmail.label" default="Is Send Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="isSendEmail" type="number" value="${soulsInstance.isSendEmail}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'lastvisitDate', 'error')} required">
	<label for="lastvisitDate">
		<g:message code="souls.lastvisitDate.label" default="Lastvisit Date" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:datePicker name="lastvisitDate" precision="day"  value="${soulsInstance?.lastvisitDate}"  />--}%
    <calendar:datePicker name="lastvisitDate" defaultValue="${new Date()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="souls.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${soulsInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'password', 'error')} ">
	<label for="password">
		<g:message code="souls.password.label" default="Password" />
		
	</label>
	<g:textField name="password" value="${soulsInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'registerDate', 'error')} required">
	<label for="registerDate">
		<g:message code="souls.registerDate.label" default="Register Date" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:datePicker name="registerDate" precision="day"  value="${soulsInstance?.registerDate}"  />--}%
    <calendar:datePicker name="registerDate" defaultValue="${new Date()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'securityAnswer', 'error')} ">
	<label for="securityAnswer">
		<g:message code="souls.securityAnswer.label" default="Security Answer" />
		
	</label>
	<g:textField name="securityAnswer" value="${soulsInstance?.securityAnswer}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'securityQuestion', 'error')} ">
	<label for="securityQuestion">
		<g:message code="souls.securityQuestion.label" default="Security Question" />
		
	</label>
	<g:textField name="securityQuestion" value="${soulsInstance?.securityQuestion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'sendAwareNotification', 'error')} ">
	<label for="sendAwareNotification">
		<g:message code="souls.sendAwareNotification.label" default="Send Aware Notification" />
		
	</label>
	<g:textField name="sendAwareNotification" value="${soulsInstance?.sendAwareNotification}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'soulGroupId', 'error')} required">
	<label for="soulGroupId">
		<g:message code="souls.soulGroupId.label" default="Soul Group Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="soulGroupId" type="number" value="${soulsInstance.soulGroupId}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'status', 'error')} required">
	<label for="status">
		<g:message code="souls.status.label" default="Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="status" type="number" value="${soulsInstance.status}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'transactionCode', 'error')} ">
	<label for="transactionCode">
		<g:message code="souls.transactionCode.label" default="Transaction Code" />
		
	</label>
	<g:textField name="transactionCode" value="${soulsInstance?.transactionCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulsInstance, field: 'username', 'error')} ">
	<label for="username">
		<g:message code="souls.username.label" default="Username" />
		
	</label>
	<g:textField name="username" value="${soulsInstance?.username}"/>
</div>

