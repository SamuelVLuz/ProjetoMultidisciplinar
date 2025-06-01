document.addEventListener('DOMContentLoaded', function () {
  const passwordField = document.getElementById('password');
  const passwordPreview = document.getElementById('passwordEntropy');

  if (passwordField && passwordPreview) {
    passwordField.addEventListener('input', function () {
      passwordPreview.textContent = passwordField.value;
    });
  }
});
