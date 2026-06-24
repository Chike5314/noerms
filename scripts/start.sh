#!/bin/bash
# =============================================================
# NOERMS Start Script — Linux / macOS
# Starts backend + frontend in background
# =============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_JAR="$SCRIPT_DIR/../backend/target/noerms-system.jar"
FRONTEND_DIR="$SCRIPT_DIR/../frontend"

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║   🍏 Starting NOERMS                                      ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# ── Check JAR exists ──────────────────────────────────
if [ ! -f "$BACKEND_JAR" ]; then
    echo "❌ Backend JAR not found. Run setup first:"
    echo "   ./scripts/setup.sh"
    exit 1
fi

# ── Start Backend ─────────────────────────────────────
echo "Starting backend..."
mkdir -p "$SCRIPT_DIR/../backend/logs"
nohup java -jar "$BACKEND_JAR" \
    > "$SCRIPT_DIR/../backend/logs/noerms.log" 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > /tmp/noerms_backend.pid
echo "  ✅ Backend started (PID $BACKEND_PID)"
echo "  ℹ️  Logs: backend/logs/noerms.log"

# ── Wait for backend ──────────────────────────────────
echo ""
echo "  Waiting for backend to start..."
for i in $(seq 1 30); do
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        echo "  ✅ Backend is ready!"
        break
    fi
    sleep 2
    printf "  Waiting... (%ds)\r" $((i*2))
done
echo ""

# ── Start Frontend ────────────────────────────────────
echo "Starting frontend..."
# Try multiple web servers in order of preference
if command -v python3 &>/dev/null; then
    cd "$FRONTEND_DIR" && nohup python3 -m http.server 8000 > /tmp/noerms_frontend.log 2>&1 &
    echo "  ✅ Frontend on http://localhost:8000 (python3)"
elif command -v python &>/dev/null; then
    cd "$FRONTEND_DIR" && nohup python -m http.server 8000 > /tmp/noerms_frontend.log 2>&1 &
    echo "  ✅ Frontend on http://localhost:8000 (python)"
elif command -v npx &>/dev/null; then
    cd "$FRONTEND_DIR" && nohup npx serve -p 8000 . > /tmp/noerms_frontend.log 2>&1 &
    echo "  ✅ Frontend on http://localhost:8000 (npx serve)"
elif command -v php &>/dev/null; then
    cd "$FRONTEND_DIR" && nohup php -S localhost:8000 > /tmp/noerms_frontend.log 2>&1 &
    echo "  ✅ Frontend on http://localhost:8000 (php)"
else
    echo "  ⚠️  No web server found. Open frontend/index.html manually"
    echo "     Or install: python3 / node / php"
fi

FRONTEND_PID=$!
echo $FRONTEND_PID > /tmp/noerms_frontend.pid
echo ""

# ── Open browser ──────────────────────────────────────
echo "Attempting to open browser..."
URL="http://localhost:8000"
if command -v xdg-open &>/dev/null; then
    xdg-open "$URL" 2>/dev/null &
elif command -v open &>/dev/null; then
    open "$URL" 2>/dev/null &
fi

echo "╔══════════════════════════════════════════════════════════╗"
echo "║   ✅ NOERMS is running!                                   ║"
echo "║                                                          ║"
echo "║   Frontend: http://localhost:8000                        ║"
echo "║   Backend:  http://localhost:8080/api                    ║"
echo "║                                                          ║"
echo "║   Login: admin@noerms.cm / Password@123                  ║"
echo "║                                                          ║"
echo "║   To stop: ./scripts/stop.sh                             ║"
echo "╚══════════════════════════════════════════════════════════╝"
