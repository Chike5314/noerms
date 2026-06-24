// =====================================================
// NOERMS Frontend Configuration
// API_BASE auto-detects the backend — works on any machine
// =====================================================

// Auto-detect backend: same host as frontend, port 8080
// Works on localhost, 192.168.x.x, deployed servers — no editing needed
const _host = window.location.hostname;          // e.g. localhost, 192.168.1.5
const _port = '8080';
const API_BASE = `http://${_host}:${_port}/api`;

// Role → dashboard page mapping
const ROLE_DASHBOARDS = {
    CANDIDATE:        'pages/candidate/dashboard.html',
    EXAMINER:         'pages/examiner/dashboard.html',
    INVIGILATOR:      'pages/invigilator/dashboard.html',
    NATIONAL_ADMIN:   'pages/admin/dashboard.html',
    MINISTRY_OFFICIAL:'pages/ministry/dashboard.html',
    SYSTEM_ADMIN:     'pages/admin/dashboard.html',
    SCHOOL_ADMIN:     'pages/school_admin/dashboard.html'
};

// ─── API Client ────────────────────────────────────
const API = {
    token() { return localStorage.getItem('noerms_token'); },
    user()  { return JSON.parse(localStorage.getItem('noerms_user') || 'null'); },

    headers() {
        const h = { 'Content-Type': 'application/json' };
        if (this.token()) h['Authorization'] = `Bearer ${this.token()}`;
        return h;
    },

    async post(path, data) {
        const r = await fetch(API_BASE + path, {
            method: 'POST',
            headers: this.headers(),
            body: JSON.stringify(data)
        });
        return r.json();
    },

    async get(path) {
        const r = await fetch(API_BASE + path, {
            method: 'GET',
            headers: this.headers()
        });
        if (r.status === 401) { this.logout(); return null; }
        return r.json();
    },

    async put(path, data) {
        const r = await fetch(API_BASE + path, {
            method: 'PUT',
            headers: this.headers(),
            body: JSON.stringify(data)
        });
        return r.json();
    },

    async patch(path, data) {
        const r = await fetch(API_BASE + path, {
            method: 'PATCH',
            headers: this.headers(),
            body: JSON.stringify(data || {})
        });
        return r.json();
    },

    async delete(path) {
        const r = await fetch(API_BASE + path, {
            method: 'DELETE',
            headers: this.headers()
        });
        return r.ok;
    },

    logout() {
        localStorage.removeItem('noerms_token');
        localStorage.removeItem('noerms_user');
        // Navigate to root login page
        const depth = window.location.pathname.split('/').filter(Boolean).length;
        const prefix = depth > 1 ? '../'.repeat(depth - 1) : '';
        window.location.href = prefix + 'index.html';
    }
};

// ─── Auth Guard ─────────────────────────────────────
function requireAuth(allowedRoles) {
    if (!API.token()) {
        API.logout();
        return false;
    }
    if (allowedRoles && allowedRoles.length > 0) {
        const user = API.user();
        if (!user || !allowedRoles.includes(user.role)) {
            API.logout();
            return false;
        }
    }
    return true;
}

// ─── Toast Notifications ────────────────────────────
function showToast(msg, type = 'success') {
    let c = document.getElementById('toast-container');
    if (!c) {
        c = document.createElement('div');
        c.id = 'toast-container';
        c.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:8px;';
        document.body.appendChild(c);
    }
    const colors = { success:'#22c55e', error:'#ef4444', info:'#3b82f6', warning:'#f59e0b' };
    const t = document.createElement('div');
    t.style.cssText = `background:${colors[type]||colors.info};color:white;padding:12px 20px;border-radius:8px;font-size:14px;font-weight:500;box-shadow:0 4px 12px rgba(0,0,0,0.15);animation:slideIn 0.3s ease;max-width:320px;word-break:break-word;`;
    t.textContent = msg;
    c.appendChild(t);
    setTimeout(() => { t.style.opacity='0'; t.style.transition='opacity 0.3s'; setTimeout(()=>t.remove(),300); }, 3500);
}

// ─── Render sidebar user info ────────────────────────
function renderUserInfo() {
    const user = API.user();
    if (!user) return;
    const nameEl = document.getElementById('user-name');
    const roleEl = document.getElementById('user-role');
    if (nameEl) nameEl.textContent = user.fullName || user.email;
    if (roleEl) roleEl.textContent = (user.role || '').replace(/_/g, ' ');
}
