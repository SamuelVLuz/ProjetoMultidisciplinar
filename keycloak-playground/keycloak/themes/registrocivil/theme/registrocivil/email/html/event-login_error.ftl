<#import "template.ftl" as layout>
<#setting time_zone="America/Sao_Paulo">
<#setting locale="pt_BR">
<@layout.emailLayout>
${kcSanitize(msg("eventLoginErrorBodyHtml", location, deviceName, operatingSystem, browser, event.date?datetime?string("EEEE, dd")?capitalize +  " de " + event.date?datetime?string("LLLL 'às' HH:mm"),event.ipAddress,operatingSystem))?no_esc}
</@layout.emailLayout>
