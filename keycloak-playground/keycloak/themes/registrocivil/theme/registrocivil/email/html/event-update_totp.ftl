<#import "template.ftl" as layout>
<#setting time_zone="America/Sao_Paulo">
<@layout.emailLayout>
${kcSanitize(msg("eventUpdateTotpBodyHtml",(event.date?date?string('dd/MM/yyyy') +', às '+ event.date?time?string('HH:mm:ss')), event.ipAddress))?no_esc}
</@layout.emailLayout>
