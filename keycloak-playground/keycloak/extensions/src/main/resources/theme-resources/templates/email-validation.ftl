<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
	<#if section = "header">
		${msg("emailAuthTitle")}
	<#elseif section = "form">
		<script>
			function sendAgain() {
				var form = document.getElementById('kc-email-code-login-form');
				var input = document.createElement('input');
				input.setAttribute('name', 'sendAgain');
				input.setAttribute('value', 'sendAgain');
				input.setAttribute('type', 'hidden');

				form.appendChild(input);
				form.submit();
			}

			function redefineEmail() {
				var form = document.getElementById('kc-email-code-login-form');
				var input = document.createElement('input');
				input.setAttribute('name', 'redefineEmail');
				input.setAttribute('value', 'redefineEmail');
				input.setAttribute('type', 'hidden');

				form.appendChild(input);
				form.submit();
			}
		</script>

		<form id="kc-email-code-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<#if emailAddress?? && emailAddress?is_string>
				<div class="${properties.kcFormGroupClass!}">
					<div class="${properties.kcLabelWrapperClass!}">
						<p style="margin-bottom: 0">${kcSanitize(msg('emailAuthDescription'))?no_esc} <b>${kcSanitize(emailAddress)?no_esc}</b></p>
						<#if redefinitionAllowed?? && redefinitionAllowed>
							<p>
								${kcSanitize(msg('emailAuthRedefinitionDescription'))?no_esc}
								<a href="#" onClick="redefineEmail();">
									${kcSanitize(msg('emailAuthRedefinitionLink'))?no_esc}
								</a>
							</p>
						</#if>
					</div>
				</div>
			</#if>
			<div class="${properties.kcFormGroupClass!}">
				<div class="${properties.kcLabelWrapperClass!}">
					<label for="code" class="${properties.kcLabelClass!}">${msg("emailAuthLabel")}</label>
				</div>
				<div class="${properties.kcInputWrapperClass!}">
					<input type="tel" id="code" name="code" autocomplete="off" class="${properties.kcInputClass!}" autofocus/>
				</div>
				<div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
					<div class="${properties.kcFormOptionsWrapperClass!}">
						<span style="display: flex; justify-content: end;">
							<a onclick="sendAgain();" href="#">${kcSanitize(msg('sendAgain'))?no_esc}</a>
						</span>
					</div>
				</div>
			</div>
			<div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
					<button class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit">${kcSanitize(msg('doSubmit'))?no_esc}</button>
				</div>
			</div>
		</form>
	</#if>
</@layout.registrationLayout>
