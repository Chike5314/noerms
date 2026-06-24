#!/bin/bash
echo "Stopping NOERMS..."
[ -f /tmp/noerms_backend.pid ]  && kill $(cat /tmp/noerms_backend.pid)  2>/dev/null && echo "  ✅ Backend stopped"
[ -f /tmp/noerms_frontend.pid ] && kill $(cat /tmp/noerms_frontend.pid) 2>/dev/null && echo "  ✅ Frontend stopped"
pkill -f "noerms-system.jar" 2>/dev/null; pkill -f "http.server 8000" 2>/dev/null
echo "Done."
