(() => {
  const setMessage = (type, text) => {
    const el = document.getElementById('profile-message');
    if (!el) return;
    el.classList.remove('hidden');
    el.textContent = text;
    if (type === 'success') {
      el.className = 'rounded-lg border px-4 py-3 text-sm border-green-200 bg-green-50 text-green-800';
    } else {
      el.className = 'rounded-lg border px-4 py-3 text-sm border-red-200 bg-red-50 text-red-800';
    }
  };

  const setText = (id, value) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = value == null || String(value).trim() === '' ? '—' : String(value);
  };

  const load = async () => {
    try {
      const res = await fetch('/auth/me', { method: 'GET' });
      if (res.status === 401 || res.status === 403) {
        window.location.href = '/login';
        return;
      }
      if (!res.ok) {
        const text = await res.text();
        setMessage('error', text || 'Could not load profile.');
        return;
      }

      const data = await res.json();
      setText('profile-username', data.username);
      setText('profile-displayName', data.displayName);
      setText('profile-numChildren', data.numChildren);
      setText('profile-childNames', data.childNames);
      setText('profile-childAges', data.childAges);
    } catch (e) {
      setMessage('error', 'Network error. Please refresh and try again.');
    }
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', load);
  } else {
    load();
  }
})();


