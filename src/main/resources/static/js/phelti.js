'use strict';
document.addEventListener('DOMContentLoaded', () => {

  const path = location.pathname;
  const links = [...document.querySelectorAll('.sidebar a')];
  const active = links.filter(a => path === a.pathname || (a.pathname !== '/' && path.startsWith(a.pathname + '/'))).sort((a,b) => b.pathname.length-a.pathname.length)[0];
  if (active) active.setAttribute('aria-current','page');
  function colombiaNow() {
    const parts = new Intl.DateTimeFormat('en-CA', {timeZone:'America/Bogota', year:'numeric', month:'2-digit', day:'2-digit',hour:'2-digit',minute:'2-digit',hourCycle:'h23'}).formatToParts(new Date(Date.now() + 60000));
    const values = Object.fromEntries(parts.map(p => [p.type,p.value]));
    const today = `${values.year}-${values.month}-${values.day}`;
    return {today, minute: `${values.hour}:${values.minute}`};
  }
  function updateDates() {
    const {today, minute} = colombiaNow();
    const date = document.querySelector('#activityDate');
    const time = document.querySelector('#activityTime');
    if (date && time) { date.min = today; time.min = date.value === today ? minute : '00:00'; }
  }
  updateDates();
  window.setInterval(updateDates, 15000);
  document.querySelector('#activityDate')?.addEventListener('change', updateDates);

  document.querySelectorAll('[data-tabs]').forEach(nav => {
    const anchors = [...nav.querySelectorAll('a[href^="#"]')];
    if (!anchors.length) return;
    nav.setAttribute('role','tablist');
    function show() {
      const selected = anchors.find(a => a.hash === location.hash) || anchors[0];
      anchors.forEach(a => {
        const active = a === selected;
        a.setAttribute('role','tab'); a.setAttribute('aria-selected', String(active));
        a.classList.toggle('selected', active);
        const panel = document.getElementById(a.hash.slice(1));
        if (panel) { panel.hidden = !active; panel.setAttribute('role','tabpanel'); }
      });
    }
    anchors.forEach(a => a.addEventListener('click', event => {
      event.preventDefault(); history.replaceState(null, '', a.hash); show();
    }));
    window.addEventListener('hashchange', show); show();
  });
  document.querySelectorAll('[data-status-filter]').forEach(nav => {
    const buttons = [...nav.querySelectorAll('[data-status]')];
    const rows = [...document.querySelectorAll('[data-activity-status]')];
    if (!rows.length) { nav.hidden = true; return; }
    function filter(value) {
      let count = 0;
      rows.forEach(row => { row.hidden = value !== 'all' && row.dataset.activityStatus !== value; if (!row.hidden) count++; });
      buttons.forEach(btn => { btn.classList.toggle('selected', btn.dataset.status === value); btn.setAttribute('aria-pressed', String(btn.dataset.status === value)); });
      document.querySelector('[data-filter-empty]').hidden = count > 0;
    }
    buttons.forEach(btn => btn.addEventListener('click', () => filter(btn.dataset.status)));
    filter('all');
  });
  document.querySelectorAll('[data-account-search]').forEach(input => input.addEventListener('input', () => {
    const query = input.value.trim().toLocaleLowerCase('es-CO');
    input.closest('.account-panel').querySelectorAll('[data-account-option]').forEach(form => { form.hidden = !form.textContent.toLocaleLowerCase('es-CO').includes(query); });
  }));
  const warning = document.querySelector('[data-balance-warning]');
  const type = document.querySelector('#publicationType');
  const amount = document.querySelector('#pheltAmount');
  if (warning && type && amount) {
    const validate = () => {
      const insufficient = type.value === 'NEEDS_SUPPORT' && amount.value !== '' && Number(amount.value) > Number(warning.dataset.balance);
      warning.hidden = !insufficient;
      amount.setCustomValidity(insufficient ? 'No tienes saldo suficiente para esta solicitud.' : '');
    };
    type.addEventListener('change', validate); amount.addEventListener('input', validate); validate();
  }
  const proposalWarning = document.querySelector('[data-proposal-balance]');
  if (proposalWarning) {
    const proposed = document.querySelector('#proposedAmount');
    const submit = proposalWarning.closest('form').querySelector('button[type=submit]');
    const validate = () => {
      const price = proposed && proposed.value !== '' ? Number(proposed.value) : Number(proposalWarning.dataset.price);
      const insufficient = price > Number(proposalWarning.dataset.balance);
      proposalWarning.hidden = !insufficient;
      submit.disabled = insufficient;
    };
    proposed?.addEventListener('input', validate); validate();
  }
  document.querySelectorAll('form[method="post"]').forEach(form => {
    form.addEventListener('submit', event => {
      updateDates();
      if (!form.checkValidity()) { event.preventDefault(); form.reportValidity(); }
      if (form.dataset.sending === 'true') event.preventDefault();
      if (!event.defaultPrevented && !form.hasAttribute('data-confirm')) form.dataset.sending = 'true';
    });
  });

  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', event => {
      if (event.defaultPrevented) return;
      if (!window.confirm(form.dataset.confirm)) event.preventDefault();
      else form.dataset.sending = 'true';
    });
  });

  document.querySelectorAll('[data-copy-target]').forEach(button => {
    button.addEventListener('click', async () => {
      const node = document.getElementById(button.dataset.copyTarget);
      if (!node) return;
      const code = node.textContent.trim();
      try {
        await navigator.clipboard.writeText(code);
        const original = button.textContent;
        button.textContent = 'Copiado';
        window.setTimeout(() => { button.textContent = original; }, 1600);
      } catch {
        button.textContent = 'Selecciona el código';
      }
    });
  });
});
