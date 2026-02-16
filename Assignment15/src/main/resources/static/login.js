(() => {
  const params = new URLSearchParams(window.location.search);
  const hasError = params.get("error") === "true";
  const hasLogout = params.get("logout") === "true";

  const errorBanner = document.getElementById("login-error");
  if (errorBanner && hasError) {
    errorBanner.classList.remove("hidden");
  } else if (errorBanner) {
    errorBanner.classList.add("hidden");
  }

  if (hasLogout) {
    params.delete("logout");
    window.history.replaceState({}, document.title, `${window.location.pathname}${params.toString() ? `?${params.toString()}` : ""}`);
  }
})();

