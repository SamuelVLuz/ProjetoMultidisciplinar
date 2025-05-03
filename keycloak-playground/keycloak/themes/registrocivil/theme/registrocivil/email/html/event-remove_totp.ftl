<#import "template.ftl" as layout>
<@layout.emailLayout>
${kcSanitize(msg("eventRemoveTotpBodyHtml",event.date?datetime?string('HH:mm:ss dd/MM/yyyy'), event.ipAddress))?no_esc}
</@layout.emailLayout>
