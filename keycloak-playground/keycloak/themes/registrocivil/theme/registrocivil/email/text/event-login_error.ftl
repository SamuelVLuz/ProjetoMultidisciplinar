<#ftl output_format="plainText">
<#setting time_zone="America/Sao_Paulo">
<#setting locale="pt_BR">
${msg("eventLoginErrorBody", location, deviceName, operatingSystem, browser, event.date?datetime?string("EEEE, dd")?capitalize +  " de " + event.date?datetime?string("LLLL 'às' HH:mm"), event.ipAddress)}
