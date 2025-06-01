document.addEventListener('DOMContentLoaded', function () {
  const passwordField = document.getElementById('password');
  const passwordEntropy = document.getElementById('passwordEntropy');

  function computeEntropy(password) {
    if (password.length === 0) return 0;
    const uniqueChars = new Set(password).size;
    const entropyPerChar = Math.log2(uniqueChars);
    return password.length * entropyPerChar;
  }

  async function getPercentile(entropy) {
    const response = await fetch('http://localhost:3000/percentile', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ entropy })
    });
    return response.json();
  }

  if (passwordField && passwordEntropy) {
    passwordField.addEventListener('input', async function () {
      const entropy = computeEntropy(passwordField.value);

      const result = await getPercentile(entropy);
      passwordEntropy.textContent = `Você está no top ${result.percentile}%`;
    });
  }
});
