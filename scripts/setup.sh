# !/bin/bash
# =============================================================
# NOERMS Setup Script — Linux / macOS
# =============================================================
set -e

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║   🍏 NOERMS Setup Script — Linux/macOS                   ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# ── 1. Check Java ──────────────────────────────────────
echo "Checking Java..."
if ! command -v java &>/dev/null; then
    echo "❌ Java not found. Install Java 17+:"
    echo "   macOS:  brew install openjdk@17"
    echo "   Ubuntu: sudo apt install openjdk-17-jdk"
    echo "   SDKMAN: sdk install java 17-open"
    exit 1
fi
JAVA_VER=$(java -version 2>&1 | grep -oP '(?<=version ")[\d]+' | head -1)
if [ "$JAVA_VER" -lt "17" ] 2>/dev/null; then
    echo "❌ Java $JAVA_VER found, but Java 17+ is required."
    exit 1
fi
echo "  ✅ Java $JAVA_VER"

# ── 2. Check PostgreSQL ────────────────────────────────
echo "Checking PostgreSQL..."
if ! command -v psql &>/dev/null; then
    echo "❌ PostgreSQL not found. Install it:"
    echo "   macOS:  brew install postgresql && brew services start postgresql"
    echo "   Ubuntu: sudo apt install postgresql && sudo service postgresql start"
    exit 1
fi
echo "  ✅ PostgreSQL found"

# ── 3. Read DB config ──────────────────────────────────
echo ""
echo "Database Configuration"
echo "─────────────────────────────────────────────────────────"
read -p "  PostgreSQL host     [localhost]: " DB_HOST
DB_HOST=${DB_HOST:-localhost}
read -p "  PostgreSQL port     [5432]: " DB_PORT
DB_PORT=${DB_PORT:-5432}
read -p "  PostgreSQL username [postgres]: " DB_USER
DB_USER=${DB_USER:-postgres}
read -s -p "  PostgreSQL password: " DB_PASSWORD
echo ""
read -p "  Database name       [noerms]: " DB_NAME
DB_NAME=${DB_NAME:-noerms}

# ── 4. Create database ─────────────────────────────────
echo ""
echo "Setting up database..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME;" 2>/dev/null && \
    echo "  ✅ Database '$DB_NAME' created" || echo "  ℹ️  Database already exists"

echo "  Running schema..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -f "$(dirname "$0")/../database/schema.sql" -q
echo "  ✅ Schema applied"

echo "  Populating test data..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -f "$(dirname "$0")/../database/data_population.sql" -q
echo "  ✅ Data populated (100 candidates)"

# ── 5. Write application.properties ───────────────────
PROPS="$(dirname "$0")/../backend/src/main/resources/application.properties"
cat > "$PROPS" << PROPS_EOF
server.port=8080
spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false
spring.cache.type=none
app.jwt.secret=noerms-secret-key-university-buea-cef476-2024-production-minimum-64-chars
app.jwt.expiration=86400000
logging.level.root=WARN
logging.level.com.noerms=INFO
PROPS_EOF
echo "  ✅ application.properties configured"

# ── 6. Build backend ───────────────────────────────────
echo ""
echo "Building backend..."
BACKEND_DIR="$(dirname "$0")/../backend"
cd "$BACKEND_DIR"

if command -v mvn &>/dev/null; then
    mvn clean package -DskipTests -q
else
    chmod +x mvnw
    ./mvnw clean package -DskipTests -q
fi
echo "  ✅ Build successful → target/noerms-system.jar"

# ── 7. Done ────────────────────────────────────────────
echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║   ✅ NOERMS Setup Complete!                               ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""
echo "  To start NOERMS, run:"
echo ""
echo "    ./scripts/start.sh"
echo ""
