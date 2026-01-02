(() => {
  const form = document.getElementById('register-form');
  const messageEl = document.getElementById('form-message');

  if (!form) return;

  const setMessage = (type, text) => {
    // type: 'success' | 'error'
    messageEl.classList.remove('hidden');
    messageEl.textContent = text;
    if (type === 'success') {
      messageEl.className = 'rounded-lg border px-4 py-3 text-sm border-green-200 bg-green-50 text-green-800';
    } else {
      messageEl.className = 'rounded-lg border px-4 py-3 text-sm border-red-200 bg-red-50 text-red-800';
    }
  };

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const displayName = document.getElementById('displayName').value;
    const password = document.getElementById('password').value;

    // quick client-side checks (server also validates)
    if (!username || username.trim().length < 3) return setMessage('error', 'Username must be at least 3 characters.');
    if (!displayName || !displayName.trim()) return setMessage('error', 'Display name is required.');
    if (!password || password.length < 8) return setMessage('error', 'Password must be at least 8 characters.');

    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.classList.add('opacity-75', 'cursor-not-allowed');

    try {
      const res = await fetch('/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: username.trim(),
          displayName: displayName.trim(),
          password,
        }),
      });

      if (res.ok) {
        setMessage('success', 'Account created! Redirecting to login...');
        setTimeout(() => {
          window.location.href = '/login';
        }, 900);
        return;
      }

      const text = await res.text();
      // Backend returns plain text messages for errors
      setMessage('error', text || 'Registration failed.');
    } catch (err) {
      setMessage('error', 'Network error. Please try again.');
    } finally {
      submitBtn.disabled = false;
      submitBtn.classList.remove('opacity-75', 'cursor-not-allowed');
    }
  });
})();


