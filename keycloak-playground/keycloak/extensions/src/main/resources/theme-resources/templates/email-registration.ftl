<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError(emailField); section>
	<#if section = "header">
		${msg("updateEMailTitle")}
	<#elseif section = "form">		
		<form id="kc-email-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<div class="${properties.kcFormGroupClass!}">
				<#if updateEmailText??>
					<div class="${properties.kcLabelWrapperClass!}">
						<p>
							${kcSanitize(msg(updateEmailText))?no_esc}
						</p>
					</div>
				</#if>
				<div class="${properties.kcLabelWrapperClass!}">
					<label for="emailField"class="${properties.kcLabelClass!}">${msg("updateEMailFieldLabel")}</label>
				</div>
				<div class="${properties.kcInputWrapperClass!}">
					<input type="email" id="emailField" name="emailField" class="${properties.kcInputClass!}"
						value="${emailField}" aria-invalid="<#if messagesPerField.existsError(emailField)>true</#if>"/>
					<#if messagesPerField.existsError(emailField)>
						<span id="input-error-email-number" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
							${kcSanitize(messagesPerField.get(emailField))?no_esc}
						</span>
					</#if>
				</div>
			</div>
			
			<div class="${properties.kcFormGroupClass!}">
				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}" style="display: flex; gap: 1.5rem; margin-top: 9px;">
					<button class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="submit" type="submit">${kcSanitize(msg("doSubmit"))?no_esc}</button>
				</div>
			</div>
		</form>
	</#if>
</@layout.registrationLayout>
