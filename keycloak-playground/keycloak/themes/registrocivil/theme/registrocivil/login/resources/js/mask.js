$( document ).ready(function() {
  $("#kc-info-message p:contains('Sua conta foi atualizada.')").html($("#kc-info-message p").html() + "<br><br><a href='https://idrc.registrocivil.org.br/'>Clique aqui para redirecionar ao IdRC</a>.")

  $("#kc-form-login #username").keypress(function() {
    $(this).mask('000.000.000-00', {clearIfNotMatch: true});
  });

  $("#kc-form-login #username").on('keyup keypress blur change paste', function() {
    $(this).mask('000.000.000-00', {clearIfNotMatch: true});
  });

  $("#kc-form-login").submit(function() {
    $("#username").unmask();
  });
});
