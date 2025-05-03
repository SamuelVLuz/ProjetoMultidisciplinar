<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("termsTitle")}
    <#elseif section = "form">
        <style>
            .card-pf {
                max-width: 1000px;
            }

            iframe {
                width: 100%;
                height: 500px;
            }
        </style>

        <div id="kc-terms-text">
<#--            ${kcSanitize(msg("termsText"))?no_esc}-->
        </div>
        <div>
            <iframe src="${url.resourcesPath}/js/pdfjs/web/viewer.html?file=${url.resourcesPath}/politica-de-privacidade-idrc.pdf"></iframe>
            <p>${msg("termsPdfErrorMsg")} <a href="${msg("termsPdfUrl")}" target="_blank">${msg("termsPdfErrorMsg2")}</a></p>
        </div>
        <br>
        <form class="form-actions" action="${url.loginAction}" method="POST">
            <button type="submit" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="accept" id="kc-accept" type="submit" value="${msg("doAccept")}">${kcSanitize(msg("doAccept"))?no_esc}</button>
            <button type="submit" class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" name="cancel" id="kc-decline" type="submit" value="${msg("doDecline")}">${kcSanitize(msg("doDecline"))?no_esc}</button>
        </form>
        <div class="clearfix"></div>
    </#if>
</@layout.registrationLayout>
