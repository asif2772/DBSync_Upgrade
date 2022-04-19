<%@ page import="bv.SoulSession" %>



<div class="fieldcontain ${hasErrors(bean: soulSessionInstance, field: 'ipAddress', 'error')} ">
	<label for="ipAddress">
		<g:message code="soulSession.ipAddress.label" default="Ip Address" />
		
	</label>
	<g:textField name="ipAddress" value="${soulSessionInstance?.ipAddress}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulSessionInstance, field: 'sessionCode', 'error')} ">
	<label for="sessionCode">
		<g:message code="soulSession.sessionCode.label" default="Session Code" />
		
	</label>
	<g:textField name="sessionCode" value="${soulSessionInstance?.sessionCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulSessionInstance, field: 'soulId', 'error')} required">
	<label for="soulId">
		<g:message code="soulSession.soulId.label" default="Soul Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="soulId" type="number" value="${soulSessionInstance.soulId}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulSessionInstance, field: 'time', 'error')} ">
	<label for="time">
		<g:message code="soulSession.time.label" default="Time" />
		
	</label>
	<g:textField name="time" value="${soulSessionInstance?.time}"/>
</div>

