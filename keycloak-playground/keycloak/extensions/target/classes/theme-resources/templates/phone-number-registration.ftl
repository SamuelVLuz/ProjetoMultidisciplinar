<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError(mobileNumberField); section>		
	<#if section = "header">
		${msg("updateMobileTitle")}
	<#elseif section = "form">
		<style>
			.iti {
				--iti-path-flags-1x: url('${url.resourcesPath}/img/flags.webp');
				--iti-path-flags-2x: url('${url.resourcesPath}/img/flags@2x.webp');
				--iti-path-globe-1x: url('${url.resourcesPath}/img/globe.webp');
				--iti-path-globe-2x: url('${url.resourcesPath}/img/globe@2x.webp');
			}
		</style>

		<form id="kc-mobile-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<div class="${properties.kcFormGroupClass!}">
				<#if updateMobileText??>
					<div class="${properties.kcLabelWrapperClass!}">
						<p>
							${kcSanitize(msg(updateMobileText))?no_esc}
						</p>
					</div>
				</#if>
				<div class="${properties.kcLabelWrapperClass!}">
					<label for="mobileNumberField"class="${properties.kcLabelClass!}">${msg("updateMobileFieldLabel")}</label>
				</div>
				<div class="${properties.kcInputWrapperClass!}">
					<input type="tel" id="mobileNumberField" name="mobileNumberField" class="${properties.kcInputClass!}"
						value="${mobileNumberField}" <#if canSkip??>${canSkip?then('', 'required')}<#else></#if> aria-invalid="<#if messagesPerField.existsError(mobileNumberField)>true</#if>"/>
					<input id="hiddenMobileNumberField" type="hidden" name="full_number">
					<input id="hiddenMobileNumberCountry" type="hidden" name="country">
					<#if messagesPerField.existsError(mobileNumberField)>
						<span id="input-error-mobile-number" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
							${kcSanitize(messagesPerField.get(mobileNumberField))?no_esc}
						</span>
					</#if>
				</div>
			</div>
			
			<div class="${properties.kcFormGroupClass!}">
				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}" style="display: flex; gap: 1.5rem; margin-top: 9px;">
					<#if canSkip?? && canSkip>
						<button type="submit" class="pf-m-secondary ${properties.kcButtonClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="skipRegister" value="true">${kcSanitize(msg("doCancel"))?no_esc}</button>
					</#if>
					<button type="submit" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="submit">${kcSanitize(msg("doSubmit"))?no_esc}</button>
				</div>
			</div>
		</form>

		<link href="${url.resourcesPath}/css/intlTelInput.min.css" rel="stylesheet">
		<script src="${url.resourcesPath}/js/intlTelInputWithUtils.min.js" type="text/javascript"></script>
		<script src="${url.resourcesPath}/js/mask_country.js" type="text/javascript"></script>
	</#if>
</@layout.registrationLayout>
