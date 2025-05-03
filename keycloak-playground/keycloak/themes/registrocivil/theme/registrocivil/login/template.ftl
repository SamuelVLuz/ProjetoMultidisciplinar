<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="apple-touch-icon" sizes="180x180" href="${url.resourcesPath}/img/favicon/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${url.resourcesPath}/img/favicon/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${url.resourcesPath}/img/favicon/favicon-16x16.png">
    <link rel="manifest" href="${url.resourcesPath}/img/favicon/site.webmanifest">
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>

    <script type="text/javascript">
        function submitTryAnotherAuthenticator(authExecId) {
            document.getElementById('authexec-hidden-input').value = authExecId;
            document.getElementById('kc-select-try-another-way-form').submit();
        }
    </script>

</head>
<body class="${properties.kcBodyClass!}">
<div class="${properties.kcLoginClass!}">
    <div id="rc-header" class="${properties.kcHeaderClass!}">
        <a class="rc-navbar-brand">
            <img src="${url.resourcesPath}/img/registro-civil.svg" alt="Sistema de Autenticação Eletrônica do Registro Civil – IdRC" />
        </a>
    </div>
    <div id="kc-header" class="${properties.kcHeaderClass!}">
        <div id="kc-header-wrapper"
             class="${properties.kcHeaderWrapperClass!}">${kcSanitize(msg("loginTitleHtml",(realm.displayNameHtml!'')))?no_esc}</div>
    </div>
    <div id="rc-page">
        <div class="${properties.kcFormCardClass!}">
            <header class="${properties.kcFormHeaderClass!}">
                <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
                    <div class="${properties.kcLocaleMainClass!}" id="kc-locale">
                        <div id="kc-locale-wrapper" class="${properties.kcLocaleWrapperClass!}">
                            <div id="kc-locale-dropdown" class="${properties.kcLocaleDropDownClass!}">
                                <a href="#" id="kc-current-locale-link">${locale.current}</a>
                                <ul class="${properties.kcLocaleListClass!}">
                                    <#list locale.supported as l>
                                        <li class="${properties.kcLocaleListItemClass!}">
                                            <a class="${properties.kcLocaleItemClass!}" href="${l.url}">${l.label}</a>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                        </div>
                    </div>
                </#if>
                <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
                    <#if displayRequiredFields>
                        <div class="${properties.kcContentWrapperClass!}">
                            <div class="${properties.kcLabelWrapperClass!} subtitle">
                                <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                            </div>
                            <div class="col-md-10">
                                <h1 id="kc-page-title"><#nested "header"></h1>
                            </div>
                        </div>
                    <#else>
                        <h1 id="kc-page-title"><#nested "header"></h1>
                    </#if>
                <#else>
                    <#if displayRequiredFields>
                        <div class="${properties.kcContentWrapperClass!}">
                            <div class="${properties.kcLabelWrapperClass!} subtitle">
                                <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                            </div>
                            <div class="col-md-10">
                                <#nested "show-username">
                                <div id="kc-username" class="${properties.kcFormGroupClass!}">
                                    <label id="kc-attempted-username" class="text-uppercase"> ${userFullName} </label>
                                    <a id="reset-login" href="${url.loginRestartFlowUrl}" aria-label="${msg("restartLoginTooltip")}">
                                        <div class="kc-login-tooltip">
                                            <i class="${properties.kcResetFlowIcon!}"></i>
                                            <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    <#else>
                        <#nested "show-username">
                        <div id="kc-username" class="${properties.kcFormGroupClass!}">
                            <label id="kc-attempted-username" class="text-uppercase"> ${userFullName} </label>
                            <a id="reset-login" href="${url.loginRestartFlowUrl}" aria-label="${msg("restartLoginTooltip")}">
                                <div class="kc-login-tooltip">
                                    <i class="${properties.kcResetFlowIcon!}"></i>
                                    <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                                </div>
                            </a>
                        </div>
                    </#if>
                </#if>
            </header>
            <div id="kc-content">
                <div id="kc-content-wrapper">

                    <#-- App-initiated actions should not see warning messages about the need to complete the action -->
                    <#-- during login.                                                                               -->
                    <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                        <div class="alert-${message.type} ${properties.kcAlertClass!} pf-m-<#if message.type = 'error'>danger<#else>${message.type}</#if>">
                            <div class="pf-c-alert__icon">
                                <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>
                                <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                                <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>
                                <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                            </div>
                            <span class="${properties.kcAlertTitleClass!}">${kcSanitize(message.summary)?no_esc}</span>
                        </div>
                    </#if>

                    <#nested "form">

                    <#nested "socialProviders">

                    <#if displayInfo>
                        <div id="kc-info" class="${properties.kcSignUpClass!}">
                            <div id="kc-info-wrapper" class="${properties.kcInfoAreaWrapperClass!}">
                                <#nested "info">
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
        <#-- Se o autenticador atual for opcional e não for o único disponível, mostramos o botão de "tentar outro método". Ao clicar nesse botão, o usuário é redirecionado a uma página de seleção de um autenticador, onde todas as opções disponíveis são mostradas -->
        <#if auth?has_content && auth.showTryAnotherWayLink() && !displayTryAnother?has_content>
            <#assign tryAnotherRedirectDisplayName = "try-another-redirect-display-name" >
            <#list auth.authenticationSelections as selection>
                <#if selection.displayName == tryAnotherRedirectDisplayName>
                    <#assign tryAnotherRedirectId = selection.authExecId>
                    <#break>
                </#if>
            </#list>
            <#if tryAnotherRedirectId?? && tryAnotherAuthenticators != 1>
                <form id="kc-select-try-another-way-form" class="${properties.kcFormClass!} form-alternative-link" action="${url.loginAction}" method="post">
                    <div class="${properties.kcFormGroupClass!}" style="margin-left: 0px; margin-right: 0px;">
                        <input type="hidden" id="authexec-hidden-input" name="authenticationExecution" />

                        <a href="#" id="try-another-way" class="alternative-link"
                            onclick="submitTryAnotherAuthenticator('${tryAnotherRedirectId}')">
                                <i class="fa fa-angle-left fa-lg"></i>
                            <#if flowPath == "registration">
                                <span>${msg("doTryAnotherWayRegistration")}</span>
                            <#else>
                                <span>${msg("doTryAnotherWay")}</span>
                            </#if>
                        </a>
                    </div>
                </form>
            </#if>
        </#if>
    </div>
    <footer id="rc-footer">
        <div class="rc-social-contacts">
            <div class="rc-social-contacts__social">
                <a target="_blank" href="https://www.facebook.com/registrocivilorg/"
                ><img src="${url.resourcesPath}/img/footersvg/iconmonstr-facebook-3.svg" alt="Logo Facebook">
                </a>
                <a target="_blank" href="https://www.instagram.com/registrocivilorg/"
                ><img src="${url.resourcesPath}/img/footersvg/iconmonstr-instagram-11.svg" alt="Logo Instagram">
                </a>
                <a target="_blank" href="https://twitter.com/reg_civil"
                ><img src="${url.resourcesPath}/img/footersvg/Twitter-logo.svg" alt="Logo Twitter">
                </a>
                <a target="_blank" href="https://www.youtube.com/channel/UC8zER4S5eYSSVsFfhuuFOzw"
                ><img src="${url.resourcesPath}/img/footersvg/youtube-svgrepo-com.svg" alt="Logo Youtube">
                </a>
            </div>

            <ul>
                <li>
                    <a href="tel:+551155559372">Fone: (11) 5555-9372</a>
                </li>
                <li>
                    <a href="mailto:suporte.cliente@registrocivil.org.br">
                        E-mail: suporte.cliente@registrocivil.org.br
                    </a>
                </li>
                <li>
                    ® 2022 - Todos os direitos reservados
                </li>
                <li>
                    Para uma melhor experiência utilize o Google Chrome
                </li>
            </ul>

        </div>

        <div class="rc-sustained">
            <div class="rc-sustained__arpen rc-sustainer">
                <span>Mantido por</span>
                <a target="_blank" href="http://www.arpenbrasil.org.br/"><img src="${url.resourcesPath}/img/footersvg/arpen-br.svg" alt="Arpen Brasil"></a>
            </div>
            <span></span>
            <div class="rc-sustained__cnj rc-sustainer">
                <span>Regulamentado provimento Nº46</span>
                <a target="_blank" href="https://atos.cnj.jus.br/atos/detalhar/2509"><img src="${url.resourcesPath}/img/footersvg/cnj-white.svg" alt="Conselho Nacional de Justiça"></a>
            </div>
        </div>
    </footer>
</div>
</body>
</html>
</#macro>
