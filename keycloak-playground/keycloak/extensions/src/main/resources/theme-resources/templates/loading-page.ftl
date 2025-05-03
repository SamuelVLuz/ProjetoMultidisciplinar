<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
	<#if section = "header">
		${kcSanitize(msg('loadingPageTitle'))?no_esc}
	<#elseif section = "form">
        <style>
            .loading {
                display: block;
                margin-left: auto;
                margin-right: auto;
            }
            #kc-select-try-another-way-form {
                display: none !important;
            }
        </style>
        
        <script type="text/javascript">
            window.addEventListener('load', function () {
                document.getElementById('loading-page-form').submit();
            });
        </script>

		<form id="loading-page-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<div class="${properties.kcFormGroupClass!}">
				<div class="${properties.kcInputWrapperClass!}">
					<img class="loading" alt="Carregando" src="${url.resourcesPath}/img/loading.gif">
				</div>
			</div>

            <input type="hidden" id="loadingData" name="loadingData" value="loadingData"/>
		</form>
	<#elseif section = "info" >
		${msg("loadingPageInstruction")}
	</#if>
</@layout.registrationLayout>
