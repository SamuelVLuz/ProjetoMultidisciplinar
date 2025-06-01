document.addEventListener('DOMContentLoaded', function () {
  const passwordField = document.getElementById('password');
  const passwordEntropy = document.getElementById('passwordEntropy');
  
  function computeEntropy(password) {
    if (password.length === 0) return 0; // Handle empty input
    const uniqueChars = new Set(password).size;
    const entropyPerChar = Math.log2(uniqueChars);
    const entropy = password.length * entropyPerChar;
   return entropy;
  }
  
  if (passwordField && passwordEntropy) {
    passwordField.addEventListener('input', function () {
      const entropy = computeEntropy(passwordField.value);
      passwordEntropy.textContent = entropy.toFixed(2); // Show 2 decimal places
    });
  }
});