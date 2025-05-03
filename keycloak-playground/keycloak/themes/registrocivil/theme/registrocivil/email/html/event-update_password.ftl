<#import "template.ftl" as layout>
<@layout.emailLayout>
${kcSanitize(msg("eventUpdatePasswordBodyHtml",event.date?datetime?string('HH:mm:ss dd/MM/yyyy'), event.ipAddress))?no_esc}
</@layout.emailLayout>
