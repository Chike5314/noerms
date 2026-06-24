# 🍏 NOERMS — National Online Examination Management System
**University of Buea | CEF476 | Dr. Hugues Marie Kamdjou | v1.0.2**

---

## ⚡ Quick Start — Recommended Path (No Docker)

### Prerequisites
| Tool | Version | Check |
|------|---------|-------|
| Java JDK | 17+ | `java -version` |
| PostgreSQL | 12+ | `psql --version` |
| Maven | 3.6+ (or use included `mvnw`) | `mvn -version` |
| Python | 3.x | `python --version` |

### Step 1 — Database
```bash
psql -U postgres -c "CREATE DATABASE noerms;"
psql -U postgres -d noerms -f database/schema.sql
psql -U postgres -d noerms -f database/data_population.sql
```

### Step 2 — Set your DB password
Edit `backend/src/main/resources/application.properties`, or simply export an env var before running:
```bash
export DB_PASSWORD=your_postgres_password
```

### Step 3 — Build & run backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/noerms-system.jar
```

### Step 4 — Serve frontend
```bash
cd frontend
python -m http.server 8000
```

### Step 5 — Open
`http://localhost:8000` → login with `admin@noerms.cm` / `Password@123`

---

## 🐋 Docker — Optional Alternative

Docker is **not required**. It's provided for convenience but the mvn/java path above is the primary, tested path. If you use Docker:

```bash
cp .env.example .env   # edit values if needed
docker compose up -d --build
```

### Docker troubleshooting (common Windows/Docker Desktop issues)

**`dial tcp: lookup registry-1.docker.io: no such host`**
This is a DNS/networking issue with Docker Desktop, not the project. Fix:
```bash
ipconfig /flushdns          # Windows — clear DNS cache
docker context use default  # ensure correct context
```
Then in Docker Desktop → Settings → Docker Engine, add:
```json
{ "dns": ["8.8.8.8", "1.1.1.1"] }
```
Apply & restart Docker Desktop, then retry.

**Build hangs at the same step for minutes ("stuck")**
```bash
docker compose down
docker builder prune -f
docker compose up -d --build --force-recreate
```
If it hangs at the *exact same step* again, run with full logs to see what's actually blocking:
```bash
docker compose build --progress=plain backend
```

**Port 5432 already in use**
You likely have a local PostgreSQL running. Either stop it, or change the host port in `.env`:
```bash
DB_PORT=5433
```
(Internally the containers still talk to each other on 5432 — only your host-machine access port changes.)

**Changing the database name**
If you change `DB_NAME` in `.env`, you do **not** need to touch the SQL files — `schema.sql` has no hardcoded database name, only `CREATE TABLE` statements. Docker creates the database automatically from the `DB_NAME` env var. After changing it, wipe old volumes first:
```bash
docker compose down -v
docker compose up -d --build
```

---

## 🖱️ Frontend Navigation — How It Works

Every dashboard (`candidate`, `examiner`, `invigilator`, `admin`, `ministry`, `school_admin`, plus `admin/analytics`) is a **single HTML page with multiple `<div id="sec-...">` sections**. Clicking a sidebar item calls `showSection('name')`, which:
1. Hides every section except the one matching `name` (via the `.hidden` CSS class)
2. Highlights the clicked sidebar item (via `.active` class, matched by `data-section` attribute)
3. Updates the page title
4. Triggers any data load needed for that section (e.g. `loadUsers()`, `loadAuditLogs()`)

**Fixed in v1.0.2:** an earlier edit pass had stripped the global `.hidden { display:none }` CSS rule (only `.modal-overlay.hidden` existed), so every section rendered simultaneously, stacked on top of each other — navigation looked "dead" even though the click handlers were firing correctly. Also fixed: 4 dashboards never re-applied the `.active` class to the clicked nav item after the first click. Both issues are now resolved across all 7 dashboards + the analytics page, verified by parsing every `showSection()` call against every `sec-*` id to confirm a 1:1 match.

---

## 🔑 Login Credentials
All passwords: **`Password@123`**

| Role | Email |
|------|-------|
| Candidate | jane.doe@email.com |
| Examiner | john.smith@school.cm |
| Invigilator | mary.jones@center.cm |
| National Admin | admin@noerms.cm |
| Ministry Official | official@minesec.cm |
| System Admin | sysadmin@noerms.cm |
| School Admin | schooladmin@bghs.cm |

---

## 🔌 Key API Endpoints

| Method | URL | Access |
|--------|-----|--------|
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | Auth |
| GET | `/api/candidate/profile` | CANDIDATE |
| POST | `/api/candidate/form-g3` | CANDIDATE |
| POST | `/api/candidate/subjects` | CANDIDATE |
| POST | `/api/candidate/documents/{type}` | CANDIDATE |
| POST | `/api/candidate/payment` | CANDIDATE |
| GET | `/api/candidate/results` | CANDIDATE |
| POST | `/api/results/scores` | EXAMINER |
| POST | `/api/attendance/mark` | INVIGILATOR |
| POST | `/api/attendance/malpractice` | INVIGILATOR |
| GET | `/api/admin/users` | ADMIN |
| GET | `/api/analytics/overview` | ADMIN/MINISTRY |

---

## 👥 Group — CEF476
| Name | Mat. No | Role |
|------|---------|------|
| Afuh Chike Chewakondi | FE23A005 | Coordinator, Auth, Security |
| Brianna Tebesick Tsamo | FE23A028 | Notifications |
| Enow John Eyong | FE23A046 | Results, Schema, Data |
| Essoh Roddick Essoh | FE23A048 | Architecture, ORM |
| Kelsey Njock-Awoh Njock Oben | FE23A073 | Class Diagrams, Entities |
| Nkongho Elizabeth Agbor-Manyo | FE23A122 | Admin Module |
| Otang Janice Otang | FE23A134 | Invigilation |
| Suinyuy Mathias Mahla | FE23A144 | Reporting |

---
*NOERMS v1.0.2 — Navigation Fixed ✅*
