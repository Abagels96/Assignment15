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

  const setValue = (id, value) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.value = value == null ? '' : String(value);
  };

  const clearValue = (id) => setValue(id, '');

  const toggleEditMode = (isEditing) => {
    const editBtn = document.getElementById('profile-edit-btn');
    const saveBtn = document.getElementById('profile-save-btn');
    const cancelBtn = document.getElementById('profile-cancel-btn');

    const inputs = [
      'profile-username-input',
      'profile-displayName-input',
      'profile-numChildren-input',
      'profile-childNames-input',
      'profile-childAges-input',
    ].map((id) => document.getElementById(id)).filter(Boolean);

    if (editBtn) editBtn.classList.toggle('hidden', isEditing);
    if (saveBtn) saveBtn.classList.toggle('hidden', !isEditing);
    if (cancelBtn) cancelBtn.classList.toggle('hidden', !isEditing);

    inputs.forEach((input) => {
      input.classList.toggle('hidden', !isEditing);
    });
  };

  let currentProfile = null;

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
      currentProfile = data;
      setText('profile-username', data.username);
      setText('profile-displayName', data.displayName);
      setText('profile-numChildren', data.numChildren);
      setText('profile-childNames', data.childNames);
      setText('profile-childAges', data.childAges);

      setValue('profile-username-input', data.username);
      setValue('profile-displayName-input', data.displayName);
      setValue('profile-numChildren-input', data.numChildren);
      setValue('profile-childNames-input', data.childNames);
      setValue('profile-childAges-input', data.childAges);
    } catch (e) {
      setMessage('error', 'Network error. Please refresh and try again.');
    }
  };

  const wireUpButtons = () => {
    const editBtn = document.getElementById('profile-edit-btn');
    const saveBtn = document.getElementById('profile-save-btn');
    const cancelBtn = document.getElementById('profile-cancel-btn');

    const passwordToggleBtn = document.getElementById('password-toggle-btn');
    const passwordForm = document.getElementById('password-form');
    const passwordCancelBtn = document.getElementById('password-cancel-btn');
    const passwordSaveBtn = document.getElementById('password-save-btn');

    const togglePasswordForm = (show) => {
      if (!passwordForm) return;
      passwordForm.classList.toggle('hidden', !show);
      if (passwordToggleBtn) passwordToggleBtn.textContent = show ? 'Hide' : 'Change password';
      if (!show) {
        clearValue('currentPassword');
        clearValue('newPassword');
        clearValue('confirmNewPassword');
      }
    };

    if (editBtn) {
      editBtn.addEventListener('click', () => {
        if (!currentProfile) return;
        toggleEditMode(true);
      });
    }

    if (cancelBtn) {
      cancelBtn.addEventListener('click', () => {
        if (currentProfile) {
          setValue('profile-username-input', currentProfile.username);
          setValue('profile-displayName-input', currentProfile.displayName);
          setValue('profile-numChildren-input', currentProfile.numChildren);
          setValue('profile-childNames-input', currentProfile.childNames);
          setValue('profile-childAges-input', currentProfile.childAges);
        }
        toggleEditMode(false);
      });
    }

    if (saveBtn) {
      saveBtn.addEventListener('click', async () => {
        const username = (document.getElementById('profile-username-input')?.value || '').trim();
        const displayName = (document.getElementById('profile-displayName-input')?.value || '').trim();
        const numChildrenRaw = (document.getElementById('profile-numChildren-input')?.value || '').trim();
        const childNames = (document.getElementById('profile-childNames-input')?.value || '').trim();
        const childAges = (document.getElementById('profile-childAges-input')?.value || '').trim();

        if (!username || username.length < 3) return setMessage('error', 'Username must be at least 3 characters.');
        if (!displayName) return setMessage('error', 'Display name is required.');
        const numChildren = Number.parseInt(numChildrenRaw, 10);
        if (!Number.isFinite(numChildren) || Number.isNaN(numChildren) || numChildren < 1) {
          return setMessage('error', 'Number of children must be 1 or greater.');
        }
        if (!childAges) return setMessage('error', 'Children’s ages are required.');

        saveBtn.disabled = true;
        saveBtn.classList.add('opacity-75', 'cursor-not-allowed');

        try {
          const res = await fetch('/auth/me', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              username,
              displayName,
              numChildren,
              childNames: childNames || null,
              childAges,
            }),
          });

          if (res.status === 401 || res.status === 403) {
            window.location.href = '/login';
            return;
          }

          if (!res.ok) {
            const text = await res.text();
            setMessage('error', text || 'Could not save profile.');
            return;
          }

          const updated = await res.json();
          currentProfile = updated;
          setText('profile-username', updated.username);
          setText('profile-displayName', updated.displayName);
          setText('profile-numChildren', updated.numChildren);
          setText('profile-childNames', updated.childNames);
          setText('profile-childAges', updated.childAges);

          setValue('profile-username-input', updated.username);
          setValue('profile-displayName-input', updated.displayName);
          setValue('profile-numChildren-input', updated.numChildren);
          setValue('profile-childNames-input', updated.childNames);
          setValue('profile-childAges-input', updated.childAges);

          toggleEditMode(false);
          setMessage('success', 'Profile updated.');
        } catch (e) {
          setMessage('error', 'Network error. Please try again.');
        } finally {
          saveBtn.disabled = false;
          saveBtn.classList.remove('opacity-75', 'cursor-not-allowed');
        }
      });
    }

    if (passwordToggleBtn) {
      passwordToggleBtn.addEventListener('click', () => {
        const isHidden = passwordForm?.classList.contains('hidden');
        togglePasswordForm(Boolean(isHidden));
      });
    }

    if (passwordCancelBtn) {
      passwordCancelBtn.addEventListener('click', () => {
        togglePasswordForm(false);
      });
    }

    if (passwordSaveBtn) {
      passwordSaveBtn.addEventListener('click', async () => {
        const currentPassword = document.getElementById('currentPassword')?.value || '';
        const newPassword = document.getElementById('newPassword')?.value || '';
        const confirmNewPassword = document.getElementById('confirmNewPassword')?.value || '';

        if (!currentPassword) return setMessage('error', 'Current password is required.');
        if (!newPassword || newPassword.length < 8) return setMessage('error', 'New password must be at least 8 characters.');
        if (newPassword !== confirmNewPassword) return setMessage('error', 'New passwords do not match.');

        passwordSaveBtn.disabled = true;
        passwordSaveBtn.classList.add('opacity-75', 'cursor-not-allowed');

        try {
          const res = await fetch('/auth/me/password', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ currentPassword, newPassword }),
          });

          if (res.status === 401 || res.status === 403) {
            window.location.href = '/login';
            return;
          }

          if (!res.ok) {
            const text = await res.text();
            setMessage('error', text || 'Could not update password.');
            return;
          }

          setMessage('success', 'Password updated.');
          togglePasswordForm(false);
        } catch (e) {
          setMessage('error', 'Network error. Please try again.');
        } finally {
          passwordSaveBtn.disabled = false;
          passwordSaveBtn.classList.remove('opacity-75', 'cursor-not-allowed');
        }
      });
    }
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
      wireUpButtons();
      toggleEditMode(false);
      load();
    });
  } else {
    wireUpButtons();
    toggleEditMode(false);
    load();
  }
})();


