<#import "template.ftl" as layout>
<@layout.emailLayout>
${kcSanitize(bodyText)?no_esc}
</@layout.emailLayout>