<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=(isRegistrationOptional?? && isRegistrationOptional); section>
    <#if section = "header" || section = "show-username">
        <script type="text/javascript">
            function fillAndSubmit(authExecId) {
                document.getElementById('authexec-hidden-input').value = authExecId;
                document.getElementById('kc-select-credential-form').submit();
            }
        </script>
        <#if section = "header">
            <#--  Se um título foi especificado para a página  -->
            <#if tryAnotherTitle?? >
                ${kcSanitize(msg(tryAnotherTitle))?no_esc}
            <#else>
                ${kcSanitize(msg("loginChooseAuthenticator"))?no_esc}
            </#if>
        </#if>
    <#elseif section = "form">
        <form id="kc-select-credential-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcSelectAuthListClass!}">
                <#if tryAnotherText?? >
                    <p style="text-align: center; margin-bottom: 30px;">
                        ${kcSanitize(msg(tryAnotherText))?no_esc}
                    </p>
                </#if>

                <#list auth.authenticationSelections as authenticationSelection>
                    <div class="${properties.kcSelectAuthListItemClass!}" onclick="fillAndSubmit('${authenticationSelection.authExecId}')">

                        <div class="${properties.kcSelectAuthListItemIconClass!}">
                            <#assign authenticator = authenticationSelection?word_list?last>
                            <#if properties['${authenticator}']??>
                                <i class="${properties['${authenticator}']} ${properties.kcSelectAuthListItemIconPropertyClass!}"></i>
                            <#else>
                                <i class="${properties['${authenticationSelection.iconCssClass}']!authenticationSelection.iconCssClass} ${properties.kcSelectAuthListItemIconPropertyClass!}"></i>
                            </#if>
                        </div>
                        <div class="${properties.kcSelectAuthListItemBodyClass!}">
                            <div class="${properties.kcSelectAuthListItemHeadingClass!}">
                                ${msg('${authenticationSelection.displayName}')}
                            </div>
                            <div class="${properties.kcSelectAuthListItemDescriptionClass!}">
                                <#if tryAnotherTitle?? && tryAnotherTitle == "registration2FAChooseAuthenticator">
                                    ${kcSanitize(msg("${authenticationSelection.helpText}-registration"))?no_esc}
                                <#else>
                                    ${kcSanitize(msg("${authenticationSelection.helpText}"))?no_esc}
                                </#if>
                            </div>
                        </div>
                        <div class="${properties.kcSelectAuthListItemFillClass!}"></div>
                        <div class="${properties.kcSelectAuthListItemArrowClass!}">
                            <i class="${properties.kcSelectAuthListItemArrowIconClass!}"></i>
                        </div>
                    </div>
                </#list>
                <input type="hidden" id="authexec-hidden-input" name="authenticationExecution" />
            </div>
        </form>

        <#if flowPath?? && flowPath == "reset-credentials">
            <script src="${url.resourcesPath}/js/try-another.js" type="text/javascript"></script>
        </#if>
    <#elseif section = "info" && isRegistrationOptional?? && isRegistrationOptional>
        <form id="kc-skip-2FA-register-form" class="${properties.kcFormClass!} form-alternative-link" style="position: absolute; width: 100%;" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}" style="margin-left: 0; margin-right: 0;">
                <input type="hidden" id="skipRegister" name="skipRegister" value="true" />
                
                <a href="#" id="form-link" class="alternative-link" onclick="document.getElementById('kc-skip-2FA-register-form').submit()">
                    <span>${kcSanitize(msg("registration2FASkipRegistration"))?no_esc}</span>
                    <i class="fa fa-angle-right fa-lg"></i>
                </a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>