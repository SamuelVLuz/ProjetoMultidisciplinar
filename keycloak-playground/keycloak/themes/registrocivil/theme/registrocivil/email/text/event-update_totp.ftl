<#ftl output_format="plainText">
<#setting time_zone="America/Sao_Paulo">
${msg("eventUpdateTotpBody",(event.date?date?string('dd/MM/yyyy') +', às '+ event.date?time?string('HH:mm:ss')), event.ipAddress)}