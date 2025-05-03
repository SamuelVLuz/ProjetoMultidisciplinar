    <#import "template.ftl" as layout>
    <@layout.registrationLayout; section>
    <#if section = "title">
        title
    <#elseif section = "header">
        <span class="${properties.kcWebAuthnKeyIcon}"></span>
        ${kcSanitize(msg("webauthn-registration-title"))?no_esc}
    <#elseif section = "form">

        <form id="register" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <input type="hidden" id="clientDataJSON" name="clientDataJSON"/>
                <input type="hidden" id="attestationObject" name="attestationObject"/>
                <input type="hidden" id="publicKeyCredentialId" name="publicKeyCredentialId"/>
                <input type="hidden" id="authenticatorLabel" name="authenticatorLabel"/>
                <input type="hidden" id="transports" name="transports"/>
                <input type="hidden" id="error" name="error"/>
            </div>
        </form>

        <script type="text/javascript" src="${url.resourcesCommonPath}/node_modules/jquery/dist/jquery.min.js"></script>
        <script type="text/javascript" src="${url.resourcesPath}/js/base64url.js"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
        <script type="text/javascript">

        function waitForUserInput() {
            return new Promise(resolve => {
                var inputContainer = document.getElementById("inputContainer");
                var userInput = document.getElementById("userInput");
                var submitButton = document.getElementById("submitButton");
                var registerWebAuthn = document.getElementById("registerWebAuthn");

                inputContainer.style.display = "block";
                registerWebAuthn.style.display = "none";

                function resolveInput() {
                    let inputValue = userInput.value;

                    if (inputValue === null || inputValue === "") {
                        inputValue = "${kcSanitize(msg("customLabelRegisterWebauthn"))?no_esc}";
                        console.log("Sem input detectado. Novo input sera:" + inputValue);
                    }
                    resolve(inputValue);
                    inputContainer.style.display = "none";
                    registerWebAuthn.style.display = "block";
                }
                
                userInput.addEventListener("keyup", function(event) {
                    if (event.key === "Enter") {
                        resolveInput();
                    }
                });

                submitButton.addEventListener("click", function() {
                    resolveInput();
                });
            });
        }

            function registerSecurityKey() {

                // Check if WebAuthn is supported by this browser
                if (!window.PublicKeyCredential) {
                    $("#error").val("${msg("webauthn-unsupported-browser-text")?no_esc}");
                    $("#register").submit();
                    return;
                }

                // mandatory parameters
                let challenge = "${challenge}";
                let userid = "${userid}";
                let username = "${username}";

                let signatureAlgorithms = "${signatureAlgorithms}";
                let pubKeyCredParams = getPubKeyCredParams(signatureAlgorithms);

                let rpEntityName = "${rpEntityName}";
                let rp = {name: rpEntityName};

                let publicKey = {
                    challenge: base64url.decode(challenge, {loose: true}),
                    rp: rp,
                    user: {
                        id: base64url.decode(userid, {loose: true}),
                        name: username,
                        displayName: username
                    },
                    pubKeyCredParams: pubKeyCredParams,
                };

                // optional parameters
                let rpId = "${rpId}";
                publicKey.rp.id = rpId;

                let attestationConveyancePreference = "${attestationConveyancePreference}";
                if (attestationConveyancePreference !== 'not specified') publicKey.attestation = attestationConveyancePreference;

                let authenticatorSelection = {};
                let isAuthenticatorSelectionSpecified = false;

                let authenticatorAttachment = "${authenticatorAttachment}";
                if (authenticatorAttachment !== 'not specified') {
                    authenticatorSelection.authenticatorAttachment = authenticatorAttachment;
                    isAuthenticatorSelectionSpecified = true;
                }

                let requireResidentKey = "${requireResidentKey}";
                if (requireResidentKey !== 'not specified') {
                    if (requireResidentKey === 'Yes')
                        authenticatorSelection.requireResidentKey = true;
                    else
                        authenticatorSelection.requireResidentKey = false;
                    isAuthenticatorSelectionSpecified = true;
                }

                let userVerificationRequirement = "${userVerificationRequirement}";
                if (userVerificationRequirement !== 'not specified') {
                    authenticatorSelection.userVerification = userVerificationRequirement;
                    isAuthenticatorSelectionSpecified = true;
                }

                if (isAuthenticatorSelectionSpecified) publicKey.authenticatorSelection = authenticatorSelection;

                let createTimeout = ${createTimeout};
                if (createTimeout !== 0) publicKey.timeout = createTimeout * 1000;

                let excludeCredentialIds = "${excludeCredentialIds}";
                let excludeCredentials = getExcludeCredentials(excludeCredentialIds);
                if (excludeCredentials.length > 0) publicKey.excludeCredentials = excludeCredentials;

                navigator.credentials.create({publicKey})
                    .then(async function (result) {
                        window.result = result;
                        let clientDataJSON = result.response.clientDataJSON;
                        let attestationObject = result.response.attestationObject;
                        let publicKeyCredentialId = result.rawId;

                        $("#clientDataJSON").val(base64url.encode(new Uint8Array(clientDataJSON), {pad: false}));
                        $("#attestationObject").val(base64url.encode(new Uint8Array(attestationObject), {pad: false}));
                        $("#publicKeyCredentialId").val(base64url.encode(new Uint8Array(publicKeyCredentialId), {pad: false}));

                        if (typeof result.response.getTransports === "function") {
                            let transports = result.response.getTransports();
                            if (transports) {
                                $("#transports").val(getTransportsAsString(transports));
                            }
                        } else {
                            console.log("Your browser is not able to recognize supported transport media for the authenticator.");
                        }

                        try {
                            let labelResult = await waitForUserInput();
                            $("#authenticatorLabel").val(labelResult);
                            $("#register").submit();
                        } catch (err) {
                            $("#error").val(err);
                            $("#register").submit();
                        }

                        // Old way to get name input for WebAuthn using navigator prompt
                        /*
                        let initLabel = "${kcSanitize(msg("customLabelRegisterWebauthn"))?no_esc}";
                        let labelResult = window.prompt("${kcSanitize(msg("customMessageRegisterWebauthn"))?no_esc}", initLabel);
                        if (labelResult === null) labelResult = initLabel;
                        $("#authenticatorLabel").val(labelResult);

                        $("#register").submit();
                        */
                    })
                    .catch(function (err) {
                        $("#error").val(err);
                        $("#register").submit();

                    });
            }

            function getPubKeyCredParams(signatureAlgorithms) {
                let pubKeyCredParams = [];
                if (signatureAlgorithms === "") {
                    pubKeyCredParams.push({type: "public-key", alg: -7});
                    return pubKeyCredParams;
                }
                let signatureAlgorithmsList = signatureAlgorithms.split(',');

                for (let i = 0; i < signatureAlgorithmsList.length; i++) {
                    pubKeyCredParams.push({
                        type: "public-key",
                        alg: signatureAlgorithmsList[i]
                    });
                }
                return pubKeyCredParams;
            }

            function getExcludeCredentials(excludeCredentialIds) {
                let excludeCredentials = [];
                if (excludeCredentialIds === "") return excludeCredentials;

                let excludeCredentialIdsList = excludeCredentialIds.split(',');

                for (let i = 0; i < excludeCredentialIdsList.length; i++) {
                    excludeCredentials.push({
                        type: "public-key",
                        id: base64url.decode(excludeCredentialIdsList[i],
                        {loose: true})
                    });
                }
                return excludeCredentials;
            }

            function getTransportsAsString(transportsList) {
                if (transportsList === '' || transportsList.constructor !== Array) return "";

                let transportsString = "";

                for (let i = 0; i < transportsList.length; i++) {
                    transportsString += transportsList[i] + ",";
                }

                return transportsString.slice(0, -1);
            }

        </script>

        <div id="inputContainer" style="display: none;">
            <label class="${properties.kcLabelClass!}">
                ${kcSanitize(msg("customMessageRegisterWebauthn"))?no_esc}
            </label>
            <input class="${properties.kcInputClass!}" 
                type="text" id="userInput" 
                value="${kcSanitize(msg("customLabelRegisterWebauthn"))?no_esc}">
            <button class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                id="submitButton"
                style="margin-top: 10px !important;">${msg("doSubmit")}</button>
        </div>

        <button type="submit"
               class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
               id="registerWebAuthn" value="${msg("doRegister")}" onclick="registerSecurityKey()">${msg("doRegister")}
        </button>

        <#if !isSetRetry?has_content && isAppInitiatedAction?has_content>
            <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-webauthn-settings-form"
                  method="post">
                <button type="submit"
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                        id="cancelWebAuthnAIA" name="cancel-aia" value="true">${msg("doCancel")}
                </button>
            </form>
        </#if>

    </#if>
    </@layout.registrationLayout>