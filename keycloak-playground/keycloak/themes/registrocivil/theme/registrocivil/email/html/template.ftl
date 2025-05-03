<#macro emailLayout>
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
            "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office"
          style="font-family:arial, 'helvetica neue', helvetica, sans-serif">
    <head>
        <meta charset="UTF-8">
        <meta content="width=device-width, initial-scale=1" name="viewport">
        <meta name="x-apple-disable-message-reformatting">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="telephone=no" name="format-detection">
        <title>Nova mensagem</title>
        <!--[if (mso 16)]>
        <style type="text/css">
            a {
                text-decoration: none;
            }
        </style>
        <![endif]--><!--[if gte mso 9]>
        <style>sup {
            font-size: 100% !important;
        }
        </style>
        <![endif]--><!--[if gte mso 9]>
        <xml>
            <o:OfficeDocumentSettings>
                <o:AllowPNG></o:AllowPNG>
                <o:PixelsPerInch>96</o:PixelsPerInch>
            </o:OfficeDocumentSettings>
        </xml>
        <![endif]--><!--[if !mso]><!-- -->
        <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,700,700i" rel="stylesheet">
        <!--<![endif]-->
        <style type="text/css">
            #outlook a {
                padding: 0;
            }
            .es-button {
                mso-style-priority: 100 !important;
                text-decoration: none !important;
            }
            a[x-apple-data-detectors] {
                color: inherit !important;
                text-decoration: none !important;
                font-size: inherit !important;
                font-family: inherit !important;
                font-weight: inherit !important;
                line-height: inherit !important;
            }
            .es-desk-hidden {
                display: none;
                float: left;
                overflow: hidden;
                width: 0;
                max-height: 0;
                line-height: 0;
                mso-hide: all;
            }
            [data-ogsb] .es-button {
                border-width: 0 !important;
                padding: 10px 20px 10px 20px !important;
            }
            .es-button-border:hover a.es-button, .es-button-border:hover button.es-button {
                background: #56d66b !important;
                border-color: #56d66b !important;
            }
            .es-button-border:hover {
                border-color: #42d159 #42d159 #42d159 #42d159 !important;
                background: #56d66b !important;
            }
            @media only screen and (max-width: 600px) {
                p, ul li, ol li, a {
                    line-height: 150% !important
                }
                h1, h2, h3, h1 a, h2 a, h3 a {
                    line-height: 120%
                }
                h1 {
                    font-size: 30px !important;
                    text-align: left
                }
                h2 {
                    font-size: 24px !important;
                    text-align: left
                }
                h3 {
                    font-size: 20px !important;
                    text-align: left
                }
                .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a {
                    font-size: 30px !important;
                    text-align: left
                }
                .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a {
                    font-size: 24px !important;
                    text-align: left
                }
                .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a {
                    font-size: 20px !important;
                    text-align: left
                }
                .es-menu td a {
                    font-size: 14px !important
                }
                .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a {
                    font-size: 14px !important
                }
                .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a {
                    font-size: 14px !important
                }
                .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a {
                    font-size: 14px !important
                }
                .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a {
                    font-size: 12px !important
                }
                *[class="gmail-fix"] {
                    display: none !important
                }
                .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 {
                    text-align: center !important
                }
                .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 {
                    text-align: right !important
                }
                .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 {
                    text-align: left !important
                }
                .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img {
                    display: inline !important
                }
                .es-button-border {
                    display: inline-block !important
                }
                a.es-button, button.es-button {
                    font-size: 18px !important;
                    display: inline-block !important
                }
                .es-adaptive table, .es-left, .es-right {
                    width: 100% !important
                }
                .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header {
                    width: 100% !important;
                    max-width: 600px !important
                }
                .es-adapt-td {
                    display: block !important;
                    width: 100% !important
                }
                .adapt-img {
                    width: 100% !important;
                    height: auto !important
                }
                .es-m-p0 {
                    padding: 0px !important
                }
                .es-m-p0r {
                    padding-right: 0px !important
                }
                .es-m-p0l {
                    padding-left: 0px !important
                }
                .es-m-p0t {
                    padding-top: 0px !important
                }
                .es-m-p0b {
                    padding-bottom: 0 !important
                }
                .es-m-p20b {
                    padding-bottom: 20px !important
                }
                .es-mobile-hidden, .es-hidden {
                    display: none !important
                }
                tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden {
                    width: auto !important;
                    overflow: visible !important;
                    float: none !important;
                    max-height: inherit !important;
                    line-height: inherit !important
                }
                tr.es-desk-hidden {
                    display: table-row !important
                }
                table.es-desk-hidden {
                    display: table !important
                }
                td.es-desk-menu-hidden {
                    display: table-cell !important
                }
                .es-menu td {
                    width: 1% !important
                }
                table.es-table-not-adapt, .esd-block-html table {
                    width: auto !important
                }
                table.es-social {
                    display: inline-block !important
                }
                table.es-social td {
                    display: inline-block !important
                }
                .es-desk-hidden {
                    display: table-row !important;
                    width: auto !important;
                    overflow: visible !important;
                    max-height: inherit !important
                }
            }
        </style>
    </head>
    <body style="width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0">
    <div class="es-wrapper-color" style="background-color:#F6F6F6">
        <!--[if gte mso 9]>
        <v:background xmlns:v="urn:schemas-microsoft-com:vml" fill="t">
            <v:fill type="tile" color="#f6f6f6"></v:fill>
        </v:background>
        <![endif]-->
        <table class="es-wrapper" width="100%" cellspacing="0" cellpadding="0"
               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#F6F6F6">
            <tr>
                <td valign="top" style="padding:0;Margin:0">
                    <table class="es-header" cellspacing="0" cellpadding="0" align="center"
                           style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top">
                        <tr>
                            <td align="center" style="padding:0;Margin:0">
                                <table class="es-header-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff"
                                       align="center"
                                       style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px">
                                    <tr>
                                        <td align="left"
                                            style="Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:560px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td align="center"
                                                                    style="padding:0;Margin:0;font-size:0px"><img
                                                                            class="adapt-img"
                                                                            src="https://arpen-imagens-publico.s3.amazonaws.com/registro-civil.png"
                                                                            alt
                                                                            style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"
                                                                            height="85" width="241"></td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" bgcolor="#7DCD52"
                                            style="Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;padding-top:25px;background-color:#7dcd52">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:560px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               bgcolor="#7DCD52"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#7dcd52"
                                                               role="presentation">
                                                            <tr>
                                                                <td align="center" style="padding:10px;Margin:0">
                                                                    <p
                                                                            style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:39px;color:#ffffff;font-size:26px">
                                                                        <strong>IdRC</strong>
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" style="padding:25px;padding-bottom:0;Margin:0">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:550px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0">
                                                                    <p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:27px;color:#6c757d;font-size:18px">
                                                                        <#if user??>
                                                                            Olá, ${kcSanitize(msg(user.firstName))?no_esc} ${kcSanitize(msg(user.lastName))?no_esc}!
                                                                        <#elseif username??>
                                                                            Olá, ${kcSanitize(username)?no_esc}!
                                                                        <#else>
                                                                            Olá!
                                                                        </#if>
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left"
                                            style="padding:0;Margin:0;padding-left:25px;padding-right:25px;padding-bottom:100px">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:550px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0">
                                                                    <div style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#6c757d;font-size:16px">
                                                                        <#nested>
                                                                    </div>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" bgcolor="#3ABBC6"
                                            style="Margin:0;padding-top:10px;padding-bottom:10px;padding-left:25px;padding-right:25px;background-color:#3abbc6">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:550px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               bgcolor="#3ABBC6"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#3abbc6"
                                                               role="presentation">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0">
                                                                    <p
                                                                            style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#ffffff;font-size:14px">
                                                                        <strong>Segurança e Privacidade</strong>
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" bgcolor="#3ABBC6"
                                            style="padding:0;Margin:0;padding-left:25px;padding-right:25px;background-color:#3abbc6">
                                            <table cellpadding="0" cellspacing="0" width="100%"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                <tr>
                                                    <td align="center" valign="top"
                                                        style="padding:0;Margin:0;width:550px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0">
                                                                    <p
                                                                            style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:15px;color:#ffffff;font-size:10px">
                                                                        Este é um e-mail automático, não é necessário
                                                                        respondê-lo.<br>Para a sua segurança, o Registro
                                                                        Civil reserva-se o direito de fazer uma análise
                                                                        de dados cadastrais. Por isso, mantenha seus
                                                                        dados sempre atualizados. Em caso de dúvidas ou
                                                                        sugestões, entre em contato conosco.
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" bgcolor="#3ABBC6"
                                            style="padding:0;Margin:0;padding-top:20px;padding-left:5px;padding-right:20px;background-color:#3abbc6">
                                            <!--[if mso]>
                                            <table style="width:550px" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="width:264px" valign="top">
                                            <![endif]-->
                                            <table cellpadding="0" cellspacing="0" class="es-left" align="left"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left">
                                                <tr>
                                                    <td class="es-m-p20b" align="left"
                                                        style="padding:0;Margin:0;width:264px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td align="right">
                                                                    <img
                                                                            class="adapt-img"
                                                                            src="https://arpen-imagens-publico.s3.amazonaws.com/tel.png"
                                                                            alt
                                                                            style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;margin-right:10px;"
                                                                            height="20" width="20">
                                                                </td>
                                                                <td align="left" style="padding:0;Margin:0"><a
                                                                            style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#ffffff;font-size:14px"
                                                                            href="tel:+551155559372">+55 (11)
                                                                        5555-9372</a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                            <!--[if mso]>
                                            </td>
                                            <td style="width:20px"></td>
                                            <td style="width:266px" valign="top">
                                            <![endif]-->
                                            <table cellpadding="0" cellspacing="0" class="es-right" align="right"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right">
                                                <tr>
                                                    <td align="left" style="padding:0;Margin:0;width:266px">
                                                        <table cellpadding="0" cellspacing="0" width="100%"
                                                               role="presentation"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                            <tr>
                                                                <td>
                                                                    <img
                                                                            class="adapt-img"
                                                                            src="https://arpen-imagens-publico.s3.amazonaws.com/email.png"
                                                                            alt
                                                                            style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;margin-right:10px;"
                                                                            height="16" width="20">
                                                                </td>
                                                                <td align="left" style="padding:0;Margin:0"><a
                                                                            style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#ffffff;font-size:14px"
                                                                            href="mailto:suporte.cliente@registrocivil.org.br">suporte.cliente@registrocivil.org.br</a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                            <!--[if mso]>
                                            </td>
                                            </tr>
                                            </table>
                                            <![endif]-->
                                        </td>
                                    </tr>
                                    <tr class="es-mobile-hidden">
                                        <td class="esdev-adapt-off" align="left" bgcolor="#3ABBC6"
                                            style="Margin:0;padding-bottom:10px;padding-top:20px;padding-left:20px;padding-right:20px;background-color:#3abbc6">
                                            <table cellpadding="0" cellspacing="0" class="esdev-mso-table"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:560px">
                                                <tr>
                                                    <td class="esdev-mso-td" valign="top" style="padding:0;Margin:0;border-right: solid white 1px;">
                                                        <table cellpadding="0" cellspacing="0" class="es-left"
                                                               align="left"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0;width:270px">
                                                                    <table cellpadding="0" cellspacing="0" width="100%"
                                                                           role="presentation"
                                                                           style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                                        <tr>
                                                                            <td align="right"
                                                                                style="padding:0;Margin:0">
                                                                                <a name="qa"
                                                                                   target="_blank"
                                                                                   href="${msg("termsPdfUrl")}"
                                                                                   style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#2CB543;font-size:14px">
                                                                                    <p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#ffffff;font-size:12px">
                                                                                        <u>Termo de uso</u>
                                                                                    </p>
                                                                                </a>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                    <td class="esdev-mso-td" valign="top" style="padding:0;Margin:0;">
                                                        <table cellpadding="0" cellspacing="0" class="es-right"
                                                               align="right"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0;width:270px">
                                                                    <table cellpadding="0" cellspacing="0" width="100%"
                                                                           role="presentation"
                                                                           style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                                        <tr>
                                                                            <td align="left" style="padding:0;Margin:0">
                                                                                <p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#ffffff;font-size:12px">
                                                                                    <u>Perguntas frequentes</u>
                                                                                </p>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <!--[if !mso]><!-- -->
                                    <tr class="es-desk-hidden"
                                        style="display:none;float:left;overflow:hidden;width:0;max-height:0;line-height:0;mso-hide:all">
                                        <td class="esdev-adapt-off" align="left" bgcolor="#3ABBC6"
                                            style="Margin:0;padding-bottom:10px;padding-top:20px;padding-left:20px;padding-right:20px;background-color:#3abbc6">
                                            <table cellpadding="0" cellspacing="0" class="esdev-mso-table"
                                                   style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:560px">
                                                <tr>
                                                    <td class="esdev-mso-td" valign="top" style="padding:0;Margin:0;border-right: solid white 1px;">
                                                        <table cellpadding="0" cellspacing="0" class="es-left"
                                                               align="left"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0;width:270px">
                                                                    <table cellpadding="0" cellspacing="0" width="100%"
                                                                           role="presentation"
                                                                           style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                                        <tr>
                                                                            <td align="center"
                                                                                style="padding:0;Margin:0">
                                                                                <a name="qa"
                                                                                   href=""
                                                                                   style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#2CB543;font-size:14px"></a>
                                                                                <p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#ffffff;font-size:12px">
                                                                                    <u>Termo de uso</u>
                                                                                </p>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                    <td style="padding:0;Margin:0;width:20px"></td>
                                                    <td class="esdev-mso-td" valign="top" style="padding:0;Margin:0">
                                                        <table cellpadding="0" cellspacing="0" class="es-right"
                                                               align="right"
                                                               style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right">
                                                            <tr>
                                                                <td align="left" style="padding:0;Margin:0;width:270px">
                                                                    <table cellpadding="0" cellspacing="0" width="100%"
                                                                           role="presentation"
                                                                           style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px">
                                                                        <tr>
                                                                            <td align="center"
                                                                                style="padding:0;Margin:0">
                                                                                <p
                                                                                        style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#ffffff;font-size:12px">
                                                                                    <u>Perguntas frequentes</u>
                                                                                </p>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <!--<![endif]-->
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    </body>
    </html>
</#macro>