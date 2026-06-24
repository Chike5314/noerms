-- =============================================================
-- NOERMS Complete Data Population Script
-- Run AFTER schema.sql
-- Cleans existing candidate data then repopulates fresh
-- =============================================================

DO $$
DECLARE
    v_user_id        BIGINT;
    v_school_id      BIGINT;
    v_center_id      BIGINT;
    v_session_id     BIGINT;
    v_examiner_id    BIGINT;
    v_subject_id     BIGINT;
    v_candidate_id   BIGINT;
    v_counter        INTEGER;
    v_candidate_rec  RECORD;
    v_score_rec      RECORD;

    v_users_count     INTEGER;
    v_cands_count     INTEGER;
    v_regs_count      INTEGER;
    v_subj_count      INTEGER;
    v_pay_count       INTEGER;
    v_scores_count    INTEGER;
    v_results_count   INTEGER;
BEGIN
    -- ============================
    -- STEP 0: CLEAN EXISTING DATA
    -- ============================
    RAISE NOTICE '==========================================';
    RAISE NOTICE 'CLEANING UP EXISTING DATA...';
    RAISE NOTICE '==========================================';

    DELETE FROM results;              RAISE NOTICE '  -> Deleted existing results';
    DELETE FROM scores;               RAISE NOTICE '  -> Deleted existing scores';
    DELETE FROM payments;             RAISE NOTICE '  -> Deleted existing payments';
    DELETE FROM subject_selections;   RAISE NOTICE '  -> Deleted existing subject selections';
    DELETE FROM center_assignments;   RAISE NOTICE '  -> Deleted existing center assignments';
    DELETE FROM attendance_records;   RAISE NOTICE '  -> Deleted existing attendance records';
    DELETE FROM registrations;        RAISE NOTICE '  -> Deleted existing registrations';
    DELETE FROM candidates;           RAISE NOTICE '  -> Deleted existing candidates';
    DELETE FROM users WHERE role IN ('CANDIDATE', 'EXAMINER') AND username LIKE 'candidate_%' OR username = 'examiner_01';
    RAISE NOTICE '  -> Deleted test candidate/examiner users';
    RAISE NOTICE 'CLEANUP COMPLETE!';
    RAISE NOTICE '';

    -- ============================
    -- STEP 1: REFERENCE DATA
    -- ============================
    RAISE NOTICE '==========================================';
    RAISE NOTICE 'STARTING FRESH DATA POPULATION';
    RAISE NOTICE '==========================================';

    INSERT INTO schools (school_code, name, region, active)
    VALUES ('BGHS001', 'Buea Government High School', 'South West', TRUE)
    ON CONFLICT (school_code) DO NOTHING;

    INSERT INTO examination_centers (center_code, name, region, capacity, active)
    VALUES ('CENTER001', 'Buea Government High School', 'South West', 500, TRUE)
    ON CONFLICT (center_code) DO NOTHING;

    INSERT INTO subjects (code, name, examination_type, max_score) VALUES
        ('ENG',  'English Language',   'BOTH',    100),
        ('FREN', 'French Language',    'BOTH',    100),
        ('MATH', 'Mathematics',        'BOTH',    100),
        ('SCI',  'Integrated Science', 'O_LEVEL', 100),
        ('HIST', 'History',            'O_LEVEL', 100),
        ('GEOG', 'Geography',          'O_LEVEL', 100)
    ON CONFLICT (code) DO NOTHING;

    -- Get or create session
    SELECT id INTO v_session_id FROM examination_sessions WHERE session_year = 2024 LIMIT 1;
    IF v_session_id IS NULL THEN
        INSERT INTO examination_sessions (session_year, session_type, start_date, end_date, status)
        VALUES (2024, 'JUNE', '2024-06-01', '2024-06-20', 'REGISTRATION_OPEN')
        RETURNING id INTO v_session_id;
    END IF;

    SELECT id INTO v_school_id  FROM schools LIMIT 1;
    SELECT id INTO v_center_id  FROM examination_centers LIMIT 1;

    -- ============================
    -- STEP 2: CREATE EXAMINER
    -- ============================
    INSERT INTO users (username, email, password_hash, role, full_name, active)
    VALUES ('examiner_01', 'examiner@noerms.com',
            '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau',
            'EXAMINER', 'Chief Examiner', TRUE)
    ON CONFLICT (username) DO UPDATE SET role = 'EXAMINER'
    RETURNING id INTO v_examiner_id;

    IF v_examiner_id IS NULL THEN
        SELECT id INTO v_examiner_id FROM users WHERE role = 'EXAMINER' LIMIT 1;
    END IF;
    RAISE NOTICE 'Examiner ID: %', v_examiner_id;

    -- ============================
    -- STEP 3: 100 CANDIDATE USERS
    -- ============================
    RAISE NOTICE 'Step 1: Creating users...';
    FOR v_counter IN 1..100 LOOP
        INSERT INTO users (username, email, password_hash, role, full_name, phone_number, active)
        VALUES (
            'candidate_' || v_counter,
            'candidate_' || v_counter || '@test.com',
            '$2a$10$OQ6PVK49ySVMRCkxaaEpS.C86az6EPoC/oU6Vt7GDB9goyqmNboau',
            'CANDIDATE',
            'Candidate ' || v_counter,
            '+2376' || LPAD(v_counter::TEXT, 8, '0'),
            TRUE
        );
    END LOOP;
    SELECT COUNT(*) INTO v_users_count FROM users WHERE role = 'CANDIDATE';
    RAISE NOTICE '  -> % candidate users created', v_users_count;

    -- ============================
    -- STEP 4: CANDIDATES
    -- ============================
    RAISE NOTICE 'Step 2: Creating candidates...';
    FOR v_counter IN 1..100 LOOP
        SELECT id INTO v_user_id FROM users WHERE username = 'candidate_' || v_counter LIMIT 1;
        IF v_user_id IS NOT NULL THEN
            INSERT INTO candidates (
                user_id, school_id, examination_type,
                first_name, last_name, date_of_birth,
                gender, registration_status, candidate_number
            ) VALUES (
                v_user_id, v_school_id,
                CASE WHEN v_counter % 2 = 0 THEN 'O_LEVEL' ELSE 'A_LEVEL' END,
                'First_' || v_counter, 'Last_' || v_counter,
                DATE '1995-01-01' + (v_counter || ' days')::INTERVAL,
                CASE WHEN v_counter % 2 = 0 THEN 'M' ELSE 'F' END,
                'CONFIRMED',
                'CAN' || LPAD(v_counter::TEXT, 8, '0')
            );
        END IF;
    END LOOP;
    SELECT COUNT(*) INTO v_cands_count FROM candidates;
    RAISE NOTICE '  -> % candidates created', v_cands_count;

    -- ============================
    -- STEP 5: REGISTRATIONS
    -- ============================
    RAISE NOTICE 'Step 3: Creating registrations...';
    FOR v_candidate_rec IN SELECT id FROM candidates LOOP
        INSERT INTO registrations (candidate_id, examination_session_id, status, center_id, seat_number, confirmed_at)
        VALUES (v_candidate_rec.id, v_session_id, 'CONFIRMED', v_center_id,
                'SEAT-' || LPAD(v_candidate_rec.id::TEXT, 4, '0'), NOW());
    END LOOP;
    SELECT COUNT(*) INTO v_regs_count FROM registrations;
    RAISE NOTICE '  -> % registrations created', v_regs_count;

    -- ============================
    -- STEP 6: SUBJECT SELECTIONS
    -- ============================
    RAISE NOTICE 'Step 4: Creating subject selections...';
    FOR v_candidate_rec IN SELECT id FROM candidates LOOP
        FOR v_counter IN 1..(4 + (v_candidate_rec.id % 3)) LOOP
            SELECT id INTO v_subject_id
            FROM subjects
            WHERE id NOT IN (
                SELECT COALESCE(subject_id, 0) FROM subject_selections
                WHERE candidate_id = v_candidate_rec.id
            )
            LIMIT 1;
            IF v_subject_id IS NOT NULL THEN
                INSERT INTO subject_selections (candidate_id, subject_id, examination_session_id)
                VALUES (v_candidate_rec.id, v_subject_id, v_session_id);
            END IF;
        END LOOP;
    END LOOP;
    SELECT COUNT(*) INTO v_subj_count FROM subject_selections;
    RAISE NOTICE '  -> % subject selections created', v_subj_count;

    -- ============================
    -- STEP 7: PAYMENTS
    -- ============================
    RAISE NOTICE 'Step 5: Creating payments...';
    FOR v_candidate_rec IN SELECT id FROM candidates LOOP
        INSERT INTO payments (candidate_id, examination_session_id, amount, status, payment_method, paid_at)
        VALUES (v_candidate_rec.id, v_session_id, 25000.00, 'CONFIRMED', 'ONLINE',
                NOW() - (random() * INTERVAL '30 days'));
    END LOOP;
    SELECT COUNT(*) INTO v_pay_count FROM payments;
    RAISE NOTICE '  -> % payments created', v_pay_count;

    -- ============================
    -- STEP 8: SCORES
    -- ============================
    RAISE NOTICE 'Step 6: Creating scores...';
    FOR v_candidate_rec IN
        SELECT DISTINCT ss.candidate_id, ss.subject_id, ss.examination_session_id
        FROM subject_selections ss LIMIT 300
    LOOP
        INSERT INTO scores (
            candidate_id, subject_id, examination_session_id,
            raw_score, examiner_id, marked_at, moderation_status
        ) VALUES (
            v_candidate_rec.candidate_id,
            v_candidate_rec.subject_id,
            v_candidate_rec.examination_session_id,
            40 + (random() * 60)::INTEGER,
            v_examiner_id,
            NOW() - (random() * INTERVAL '15 days'),
            CASE WHEN random() > 0.3 THEN 'APPROVED' ELSE 'PENDING' END
        );
    END LOOP;
    SELECT COUNT(*) INTO v_scores_count FROM scores;
    RAISE NOTICE '  -> % scores created', v_scores_count;

    -- ============================
    -- STEP 9: RESULTS
    -- ============================
    RAISE NOTICE 'Step 7: Creating results...';
    FOR v_score_rec IN
        SELECT candidate_id, subject_id, examination_session_id, raw_score
        FROM scores WHERE moderation_status = 'APPROVED' LIMIT 200
    LOOP
        INSERT INTO results (
            candidate_id, subject_id, examination_session_id, grade, status, computed_at
        ) VALUES (
            v_score_rec.candidate_id,
            v_score_rec.subject_id,
            v_score_rec.examination_session_id,
            CASE
                WHEN v_score_rec.raw_score >= 75 THEN 'A'
                WHEN v_score_rec.raw_score >= 65 THEN 'B'
                WHEN v_score_rec.raw_score >= 55 THEN 'C'
                WHEN v_score_rec.raw_score >= 45 THEN 'D'
                WHEN v_score_rec.raw_score >= 35 THEN 'E'
                WHEN v_score_rec.raw_score >= 25 THEN 'O'
                ELSE 'F'
            END,
            'APPROVED',
            NOW()
        );
    END LOOP;
    SELECT COUNT(*) INTO v_results_count FROM results;
    RAISE NOTICE '  -> % results created', v_results_count;

    -- ============================
    -- SUMMARY
    -- ============================
    RAISE NOTICE '';
    RAISE NOTICE '==========================================';
    RAISE NOTICE 'DATA POPULATION COMPLETED SUCCESSFULLY!';
    RAISE NOTICE '==========================================';
    RAISE NOTICE 'Candidate Users  : %', v_users_count;
    RAISE NOTICE 'Candidates       : %', v_cands_count;
    RAISE NOTICE 'Registrations    : %', v_regs_count;
    RAISE NOTICE 'Subject Selections: %', v_subj_count;
    RAISE NOTICE 'Payments         : %', v_pay_count;
    RAISE NOTICE 'Scores           : %', v_scores_count;
    RAISE NOTICE 'Results          : %', v_results_count;
    RAISE NOTICE '==========================================';
END $$;

-- ============================
-- VERIFICATION
-- ============================
SELECT
    'Candidate Users'    AS table_name, COUNT(*) AS count, CASE WHEN COUNT(*) = 100 THEN '✅ PASS' ELSE '❌ FAIL' END AS status FROM users   WHERE role = 'CANDIDATE'
UNION ALL SELECT 'Candidates',          COUNT(*), CASE WHEN COUNT(*) = 100 THEN '✅ PASS' ELSE '❌ FAIL' END FROM candidates
UNION ALL SELECT 'Registrations',       COUNT(*), CASE WHEN COUNT(*) = 100 THEN '✅ PASS' ELSE '❌ FAIL' END FROM registrations
UNION ALL SELECT 'Subject Selections',  COUNT(*), CASE WHEN COUNT(*) >= 400 THEN '✅ PASS' ELSE '❌ FAIL' END FROM subject_selections
UNION ALL SELECT 'Payments',            COUNT(*), CASE WHEN COUNT(*) = 100 THEN '✅ PASS' ELSE '❌ FAIL' END FROM payments
UNION ALL SELECT 'Scores',              COUNT(*), CASE WHEN COUNT(*) >= 200 THEN '✅ PASS' ELSE '❌ FAIL' END FROM scores
UNION ALL SELECT 'Results',             COUNT(*), CASE WHEN COUNT(*) >= 100 THEN '✅ PASS' ELSE '❌ FAIL' END FROM results;
