-- =============================================================
-- NOERMS Database Schema — PostgreSQL
-- University of Buea | CEF476 | Dr. Hugues Marie Kamdjou
-- Compatible with: application.properties (spring.jpa.ddl-auto=validate)
-- =============================================================

-- Clean up (run only if starting fresh)
-- DROP SCHEMA public CASCADE; CREATE SCHEMA public;

-- =====================
-- ROLES TABLE
-- =====================
CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT NOT NULL DEFAULT 0
);

INSERT INTO roles (role_name, description) VALUES
    ('CANDIDATE',        'Examination candidate'),
    ('SCHOOL_ADMIN',     'School administrator for bulk registration'),
    ('INVIGILATOR',      'Examination center invigilator'),
    ('EXAMINER',         'Script examiner and marker'),
    ('NATIONAL_ADMIN',   'National examination board administrator'),
    ('MINISTRY_OFFICIAL','Ministry of Higher Education official'),
    ('SYSTEM_ADMIN',     'Technical system administrator')
ON CONFLICT (role_name) DO NOTHING;

-- =====================
-- USERS TABLE
-- =====================
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    full_name       VARCHAR(200),
    phone_number    VARCHAR(20),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    mfa_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
    last_login      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    version         BIGINT NOT NULL DEFAULT 0
);

-- Seed demo users (password = Password@123, BCrypt 12 rounds)
INSERT INTO users (username, email, password_hash, role, full_name, active) VALUES
('jane_doe',        'jane.doe@email.com',      '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'CANDIDATE',         'Jane Doe',          TRUE),
('john_smith',      'john.smith@school.cm',    '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'EXAMINER',          'John Smith',        TRUE),
('mary_jones',      'mary.jones@center.cm',    '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'INVIGILATOR',       'Mary Jones',        TRUE),
('noerms_admin',    'admin@noerms.cm',         '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'NATIONAL_ADMIN',    'National Admin',    TRUE),
('ministry_off',    'official@minesec.cm',     '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'MINISTRY_OFFICIAL', 'Ministry Official', TRUE),
('sys_admin',       'sysadmin@noerms.cm',      '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'SYSTEM_ADMIN',      'System Admin',      TRUE),
('school_adm',      'schooladmin@bghs.cm',     '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'SCHOOL_ADMIN',      'School Admin BGHS', TRUE),
('examiner_01',     'examiner@noerms.com',     '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau', 'EXAMINER',          'Chief Examiner',    TRUE)
ON CONFLICT (username) DO NOTHING;

-- =====================
-- AUDIT LOGS
-- =====================
CREATE TABLE IF NOT EXISTS audit_logs (
    id                  BIGSERIAL PRIMARY KEY,
    actor_id            BIGINT REFERENCES users(id),
    action_type         VARCHAR(100) NOT NULL,
    entity_type         VARCHAR(50),
    entity_id           BIGINT,
    change_description  TEXT,
    ip_address          VARCHAR(45),
    timestamp           TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_audit_actor    ON audit_logs(actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp DESC);

-- =====================
-- SCHOOLS
-- =====================
CREATE TABLE IF NOT EXISTS schools (
    id          BIGSERIAL PRIMARY KEY,
    school_code VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    region      VARCHAR(100),
    address     TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT NOT NULL DEFAULT 0
);

INSERT INTO schools (school_code, name, region, active)
VALUES ('BGHS001', 'Buea Government High School', 'South West', TRUE)
ON CONFLICT (school_code) DO NOTHING;

-- =====================
-- EXAMINATION CENTERS
-- =====================
CREATE TABLE IF NOT EXISTS examination_centers (
    id          BIGSERIAL PRIMARY KEY,
    center_code VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL,
    region      VARCHAR(100),
    capacity    INTEGER,
    address     TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT NOT NULL DEFAULT 0
);

INSERT INTO examination_centers (center_code, name, region, capacity, active)
VALUES ('CENTER001', 'Buea Government High School', 'South West', 500, TRUE)
ON CONFLICT (center_code) DO NOTHING;

-- =====================
-- SUBJECTS
-- =====================
CREATE TABLE IF NOT EXISTS subjects (
    id               BIGSERIAL PRIMARY KEY,
    code             VARCHAR(20) NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL,
    examination_type VARCHAR(20) NOT NULL,  -- O_LEVEL, A_LEVEL, BOTH
    max_score        INTEGER DEFAULT 100,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    version          BIGINT NOT NULL DEFAULT 0
);

INSERT INTO subjects (code, name, examination_type, max_score) VALUES
    ('ENG',  'English Language',    'BOTH',    100),
    ('FREN', 'French Language',     'BOTH',    100),
    ('MATH', 'Mathematics',         'BOTH',    100),
    ('SCI',  'Integrated Science',  'O_LEVEL', 100),
    ('HIST', 'History',             'O_LEVEL', 100),
    ('GEOG', 'Geography',           'O_LEVEL', 100)
ON CONFLICT (code) DO NOTHING;

-- =====================
-- EXAMINATION SESSIONS
-- =====================
CREATE TABLE IF NOT EXISTS examination_sessions (
    id           BIGSERIAL PRIMARY KEY,
    session_year INTEGER NOT NULL,
    session_type VARCHAR(20) NOT NULL,  -- JUNE, DECEMBER
    start_date   DATE,
    end_date     DATE,
    status       VARCHAR(50) DEFAULT 'PLANNING',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    version      BIGINT NOT NULL DEFAULT 0
);

INSERT INTO examination_sessions (session_year, session_type, start_date, end_date, status)
VALUES (2024, 'JUNE', '2024-06-01', '2024-06-20', 'REGISTRATION_OPEN')
ON CONFLICT DO NOTHING;

-- =====================
-- CANDIDATES
-- =====================
CREATE TABLE IF NOT EXISTS candidates (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    candidate_number    VARCHAR(20) UNIQUE,
    school_id           BIGINT NOT NULL REFERENCES schools(id),
    examination_type    VARCHAR(20) NOT NULL,
    registration_status VARCHAR(50) DEFAULT 'PENDING',
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    middle_name         VARCHAR(100),
    date_of_birth       DATE,
    national_id_number  VARCHAR(20),
    gender              VARCHAR(10),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_candidates_user_id   ON candidates(user_id);
CREATE INDEX IF NOT EXISTS idx_candidates_school_id ON candidates(school_id);
CREATE INDEX IF NOT EXISTS idx_candidates_status    ON candidates(registration_status);

-- =====================
-- REGISTRATIONS
-- =====================
CREATE TABLE IF NOT EXISTS registrations (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL UNIQUE REFERENCES candidates(id) ON DELETE CASCADE,
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    status                  VARCHAR(50) DEFAULT 'INCOMPLETE',
    center_id               BIGINT REFERENCES examination_centers(id),
    seat_number             VARCHAR(10),
    registered_at           TIMESTAMP,
    confirmed_at            TIMESTAMP,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reg_candidate ON registrations(candidate_id);
CREATE INDEX IF NOT EXISTS idx_reg_session   ON registrations(examination_session_id);

-- =====================
-- SUBJECT SELECTIONS
-- =====================
CREATE TABLE IF NOT EXISTS subject_selections (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    subject_id              BIGINT NOT NULL REFERENCES subjects(id),
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    selected_at             TIMESTAMP DEFAULT NOW(),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_subj_sel_candidate ON subject_selections(candidate_id);

-- =====================
-- PAYMENTS
-- =====================
CREATE TABLE IF NOT EXISTS payments (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    amount                  DECIMAL(10,2) NOT NULL,
    status                  VARCHAR(50) DEFAULT 'PENDING',
    payment_method          VARCHAR(50),
    transaction_id          VARCHAR(100),
    paid_at                 TIMESTAMP,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_payments_candidate ON payments(candidate_id);
CREATE INDEX IF NOT EXISTS idx_payments_status    ON payments(status);

-- =====================
-- TIMETABLES
-- =====================
CREATE TABLE IF NOT EXISTS timetables (
    id                      BIGSERIAL PRIMARY KEY,
    session_code            VARCHAR(20) NOT NULL,
    subject_name            VARCHAR(100) NOT NULL,
    subject_code            VARCHAR(20),
    exam_date               TIMESTAMP NOT NULL,
    start_time              VARCHAR(20),
    end_time                VARCHAR(20),
    center_code             VARCHAR(50) NOT NULL,
    center_name             VARCHAR(100),
    duration                INTEGER,
    status                  VARCHAR(50) DEFAULT 'DRAFT',
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

-- =====================
-- CENTER ASSIGNMENTS
-- =====================
CREATE TABLE IF NOT EXISTS center_assignments (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id),
    center_id               BIGINT NOT NULL REFERENCES examination_centers(id),
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    seat_number             VARCHAR(10),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

-- =====================
-- ATTENDANCE RECORDS
-- =====================
CREATE TABLE IF NOT EXISTS attendance_records (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id),
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    center_id               BIGINT NOT NULL REFERENCES examination_centers(id),
    subject_id              BIGINT NOT NULL REFERENCES subjects(id),
    attendance_status       VARCHAR(20) NOT NULL,
    marked_by_id            BIGINT NOT NULL REFERENCES users(id),
    marked_at               TIMESTAMP DEFAULT NOW(),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_att_candidate ON attendance_records(candidate_id);
CREATE INDEX IF NOT EXISTS idx_att_center    ON attendance_records(center_id, examination_session_id);

-- =====================
-- MALPRACTICE REPORTS
-- =====================
CREATE TABLE IF NOT EXISTS malpractice_reports (
    id               BIGSERIAL PRIMARY KEY,
    candidate_id     BIGINT NOT NULL REFERENCES candidates(id),
    center_id        BIGINT NOT NULL REFERENCES examination_centers(id),
    invigilator_id   BIGINT NOT NULL REFERENCES users(id),
    description      TEXT NOT NULL,
    status           VARCHAR(50) DEFAULT 'FILED',
    filed_at         TIMESTAMP DEFAULT NOW(),
    resolved_at      TIMESTAMP,
    resolution_notes TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    version          BIGINT NOT NULL DEFAULT 0
);

-- =====================
-- SCORES
-- =====================
CREATE TABLE IF NOT EXISTS scores (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id),
    subject_id              BIGINT NOT NULL REFERENCES subjects(id),
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    raw_score               INTEGER NOT NULL CHECK (raw_score >= 0 AND raw_score <= 100),
    examiner_id             BIGINT NOT NULL REFERENCES users(id),
    marked_at               TIMESTAMP,
    moderated_by_id         BIGINT REFERENCES users(id),
    moderation_status       VARCHAR(50) DEFAULT 'PENDING',
    moderation_notes        TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_scores_candidate ON scores(candidate_id);
CREATE INDEX IF NOT EXISTS idx_scores_subject   ON scores(subject_id);
CREATE INDEX IF NOT EXISTS idx_scores_examiner  ON scores(examiner_id);

-- =====================
-- RESULTS
-- =====================
CREATE TABLE IF NOT EXISTS results (
    id                      BIGSERIAL PRIMARY KEY,
    candidate_id            BIGINT NOT NULL REFERENCES candidates(id),
    subject_id              BIGINT NOT NULL REFERENCES subjects(id),
    examination_session_id  BIGINT NOT NULL REFERENCES examination_sessions(id),
    grade                   VARCHAR(2) NOT NULL,
    status                  VARCHAR(50) DEFAULT 'PENDING_APPROVAL',
    computed_at             TIMESTAMP,
    ministry_approved_at    TIMESTAMP,
    published_at            TIMESTAMP,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_results_candidate ON results(candidate_id);
CREATE INDEX IF NOT EXISTS idx_results_status    ON results(status);

-- =====================
-- NOTIFICATION LOGS
-- =====================
CREATE TABLE IF NOT EXISTS notification_logs (
    id                  BIGSERIAL PRIMARY KEY,
    recipient_id        BIGINT NOT NULL REFERENCES users(id),
    event_type          VARCHAR(100) NOT NULL,
    channel             VARCHAR(20) NOT NULL,
    recipient_address   VARCHAR(255) NOT NULL,
    message_body        TEXT,
    status              VARCHAR(50) DEFAULT 'PENDING',
    retry_count         INTEGER DEFAULT 0,
    error_message       TEXT,
    sent_at             TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0
);

-- =====================
-- BACKUP RECORDS
-- =====================
CREATE TABLE IF NOT EXISTS backup_records (
    id               BIGSERIAL PRIMARY KEY,
    backup_name      VARCHAR(255) NOT NULL UNIQUE,
    backup_type      VARCHAR(50) NOT NULL,
    backup_path      VARCHAR(500),
    file_size_mb     INTEGER,
    status           VARCHAR(50) DEFAULT 'PENDING',
    initiated_by_id  BIGINT REFERENCES users(id),
    started_at       TIMESTAMP,
    completed_at     TIMESTAMP,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    version          BIGINT NOT NULL DEFAULT 0
);

-- =============================================================
-- ALL DONE
-- =============================================================
SELECT 'NOERMS Schema created successfully!' AS message;

-- =============================================================
-- RBAC — Permissions, Role-Permissions, User-Roles
-- Allows multi-role users and fine-grained permission control
-- =============================================================

CREATE TABLE IF NOT EXISTS permissions (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    resource    VARCHAR(50)  NOT NULL,
    action      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT NOT NULL DEFAULT 0
);

INSERT INTO permissions (name, resource, action, description) VALUES
    ('VIEW_CANDIDATES',       'candidates',    'READ',   'View candidate records'),
    ('MANAGE_CANDIDATES',     'candidates',    'WRITE',  'Create/edit candidate records'),
    ('ENTER_SCORES',          'scores',        'WRITE',  'Enter and edit scores'),
    ('APPROVE_SCORES',        'scores',        'APPROVE','Moderate and approve scores'),
    ('VIEW_RESULTS',          'results',       'READ',   'View published results'),
    ('APPROVE_RESULTS',       'results',       'APPROVE','Approve results for publication'),
    ('PUBLISH_RESULTS',       'results',       'PUBLISH','Publish results nationally'),
    ('MARK_ATTENDANCE',       'attendance',    'WRITE',  'Mark candidate attendance'),
    ('REPORT_MALPRACTICE',    'malpractice',   'WRITE',  'File malpractice reports'),
    ('MANAGE_USERS',          'users',         'WRITE',  'Create/edit system users'),
    ('VIEW_AUDIT_LOGS',       'audit_logs',    'READ',   'View audit trail'),
    ('RUN_BACKUP',            'system',        'WRITE',  'Trigger database backups'),
    ('VIEW_ANALYTICS',        'analytics',     'READ',   'View national statistics'),
    ('MANAGE_TIMETABLES',     'timetables',    'WRITE',  'Create and publish timetables'),
    ('REGISTER_CANDIDATES',   'candidates',    'CREATE', 'Register new candidates')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS role_permissions (
    id            BIGSERIAL PRIMARY KEY,
    role_name     VARCHAR(50)  NOT NULL REFERENCES roles(role_name) ON DELETE CASCADE,
    permission_id BIGINT       NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    version       BIGINT NOT NULL DEFAULT 0,
    UNIQUE (role_name, permission_id)
);

-- Assign permissions to roles
INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'CANDIDATE', id FROM permissions WHERE name IN ('VIEW_RESULTS') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'EXAMINER', id FROM permissions WHERE name IN ('ENTER_SCORES','APPROVE_SCORES','VIEW_CANDIDATES') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'INVIGILATOR', id FROM permissions WHERE name IN ('MARK_ATTENDANCE','REPORT_MALPRACTICE','VIEW_CANDIDATES') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'SCHOOL_ADMIN', id FROM permissions WHERE name IN ('REGISTER_CANDIDATES','VIEW_CANDIDATES','MANAGE_CANDIDATES') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'NATIONAL_ADMIN', id FROM permissions WHERE name IN 
('VIEW_CANDIDATES','MANAGE_CANDIDATES','APPROVE_SCORES','VIEW_RESULTS','APPROVE_RESULTS',
 'VIEW_AUDIT_LOGS','RUN_BACKUP','VIEW_ANALYTICS','MANAGE_TIMETABLES','MANAGE_USERS') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'MINISTRY_OFFICIAL', id FROM permissions WHERE name IN 
('VIEW_RESULTS','APPROVE_RESULTS','PUBLISH_RESULTS','VIEW_ANALYTICS') ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_name, permission_id) 
SELECT 'SYSTEM_ADMIN', id FROM permissions 
ON CONFLICT DO NOTHING;  -- System admin gets ALL permissions

-- user_roles: supports multi-role users
CREATE TABLE IF NOT EXISTS user_roles (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_name  VARCHAR(50) NOT NULL REFERENCES roles(role_name) ON DELETE CASCADE,
    assigned_by BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP DEFAULT NOW(),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    version     BIGINT NOT NULL DEFAULT 0,
    UNIQUE (user_id, role_name)
);
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON user_roles(user_id);

-- Seed user_roles from existing users.role column
INSERT INTO user_roles (user_id, role_name)
SELECT id, role FROM users
ON CONFLICT (user_id, role_name) DO NOTHING;


-- =====================================================
-- Schema additions for full Form G3 support
-- Run after initial schema.sql if upgrading
-- =====================================================
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS middle_name VARCHAR(100);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS place_of_birth VARCHAR(100);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS parent_guardian_name VARCHAR(200);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS parent_guardian_phone VARCHAR(20);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS residential_address VARCHAR(500);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS region VARCHAR(100);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS division VARCHAR(100);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS school_name VARCHAR(200);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS series VARCHAR(20);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS birth_certificate_path VARCHAR(500);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS passport_photo_path VARCHAR(500);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS previous_results_path VARCHAR(500);
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS school_letter_path VARCHAR(500);
