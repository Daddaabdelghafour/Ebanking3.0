# Database Setup Guide for User Service

## Overview

The user-service uses **PostgreSQL** as its database. The database is configured to run in Docker containers for local development.

## How Database Setup Works

### 1. **Docker Compose Configuration**

The database is defined in the root `docker-compose.yml` file:

```yaml
postgres-user:
  image: postgres:15-alpine
  environment:
    POSTGRES_DB: user_db
    POSTGRES_USER: user_user
    POSTGRES_PASSWORD: user_pass
  ports:
    - "5433:5432" # Host port 5433 maps to container port 5432
  volumes:
    - postgres-user-data:/var/lib/postgresql/data
```

**Key Points:**

- Database name: `user_db`
- Username: `user_user`
- Password: `user_pass`
- Host port: `5433` (accessible from your machine)
- Container port: `5432` (internal PostgreSQL port)

### 2. **Application Configuration**

The `application.yml` file connects to this database:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/user_db
    username: user_user
    password: user_pass
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update # Automatically creates/updates tables
    show-sql: true # Shows SQL queries in console
```

**Important Settings:**

- `ddl-auto: update` - Hibernate automatically creates/updates the database schema based on your entities
- `show-sql: true` - Prints all SQL queries to console (useful for debugging)

### 3. **How Tables Are Created**

When you start the application:

1. Spring Boot connects to PostgreSQL
2. Hibernate scans your `@Entity` classes (like `Customer`)
3. Hibernate compares the entity structure with the database
4. If tables don't exist, they're created automatically
5. If tables exist but columns are missing/changed, they're updated

**The `Customer` entity creates a table named `utilisateur`** (as specified in `@Table(name = "utilisateur")`)

## Step-by-Step Testing Guide

### Step 1: Start the Database

From the **root directory** of your project (`ebank3.0`):

```bash
# Start only the user-service database
docker-compose up -d postgres-user

# OR start all databases (if you need other services too)
docker-compose up -d
```

**Verify it's running:**

```bash
docker ps
# You should see postgres-user container running
```

### Step 2: Verify Database Connection

You can connect to the database using any PostgreSQL client:

**Using psql (if installed):**

```bash
psql -h localhost -p 5433 -U user_user -d user_db
# Password: user_pass
```

**Using Docker:**

```bash
docker exec -it <container-name> psql -U user_user -d user_db
```

**Using a GUI tool:**

- Host: `localhost`
- Port: `5433`
- Database: `user_db`
- Username: `user_user`
- Password: `user_pass`

### Step 3: Start the User Service

```bash
cd user-service
mvn spring-boot:run
```

**What happens:**

1. Application connects to PostgreSQL on `localhost:5433`
2. Hibernate creates the `utilisateur` table automatically
3. You'll see SQL CREATE TABLE statements in the console (because `show-sql: true`)

### Step 4: Test the API

**Create a customer:**

```bash
curl -X POST http://localhost:8082/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "cinOrPassport": "AB123456",
    "nationality": "Moroccan",
    "firstName": "John",
    "lastName": "Doe",
    "gender": "MALE",
    "email": "john.doe@example.com",
    "phone": "+212612345678",
    "address": "123 Main St",
    "city": "Casablanca",
    "birthDate": "1990-01-15",
    "country": "Morocco"
  }'
```

**Expected Response:**

```json
"Customer created successfully"
```

### Step 5: Verify Data in Database

**Check the table:**

```sql
SELECT * FROM utilisateur;
```

You should see:

- `user_id` (UUID, auto-generated)
- `cin_or_passport`
- `nationality`
- `first_name`, `last_name`
- `birth_date`
- `gender`
- `email`, `phone`
- `address`, `city`, `country`
- `customer_status` (default: `PENDING_VERIFICATION`)
- `is_verified` (default: `false`)
- `created_at`, `updated_at` (auto-generated timestamps)

## Database Schema Details

### Table: `utilisateur`

| Column          | Type         | Constraints      | Description                                 |
| --------------- | ------------ | ---------------- | ------------------------------------------- |
| user_id         | UUID         | PRIMARY KEY      | Auto-generated UUID                         |
| cin_or_passport | VARCHAR      | NOT NULL, UNIQUE | CIN or Passport number                      |
| nationality     | VARCHAR      | NOT NULL         | Nationality                                 |
| first_name      | VARCHAR      | NOT NULL         | First name                                  |
| last_name       | VARCHAR      | NOT NULL         | Last name                                   |
| birth_date      | DATE         | NOT NULL         | Date of birth                               |
| gender          | VARCHAR      | NOT NULL         | MALE or FEMALE                              |
| profession      | VARCHAR(100) |                  | Profession (optional)                       |
| email           | VARCHAR      | NOT NULL, UNIQUE | Email address                               |
| phone           | VARCHAR(20)  | NOT NULL         | Phone number                                |
| address         | VARCHAR      |                  | Address (optional)                          |
| city            | VARCHAR      |                  | City (optional)                             |
| country         | VARCHAR      |                  | Country (optional)                          |
| customer_status | VARCHAR      |                  | Status enum (default: PENDING_VERIFICATION) |
| is_verified     | BOOLEAN      |                  | Verification status (default: false)        |
| created_at      | DATE         |                  | Auto-generated creation timestamp           |
| updated_at      | DATE         |                  | Auto-generated update timestamp             |

### Indexes

- `userId` - Unique index for fast lookups

## Troubleshooting

### Database Connection Failed

**Error:** `Connection refused` or `Connection timeout`

**Solutions:**

1. Check if Docker container is running:

   ```bash
   docker ps | grep postgres-user
   ```

2. Check container logs:

   ```bash
   docker logs <container-name>
   ```

3. Verify port 5433 is not in use:

   ```bash
   netstat -an | grep 5433
   ```

4. Restart the container:
   ```bash
   docker-compose restart postgres-user
   ```

### Table Not Created

**Error:** Table `utilisateur` doesn't exist

**Solutions:**

1. Check `ddl-auto` is set to `update` (not `none` or `validate`)
2. Check application logs for Hibernate errors
3. Verify database connection is successful
4. Manually create table if needed (not recommended)

### Port Already in Use

**Error:** Port 5433 is already in use

**Solutions:**

1. Find what's using the port:

   ```bash
   netstat -ano | findstr :5433
   ```

2. Change the port in `docker-compose.yml`:

   ```yaml
   ports:
     - "5434:5432" # Use 5434 instead
   ```

3. Update `application.yml`:
   ```yaml
   url: jdbc:postgresql://localhost:5434/user_db
   ```

## Environment Variables

You can override database settings using environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5433/user_db
export DB_USERNAME=user_user
export DB_PASSWORD=user_pass
```

Or in PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5433/user_db"
$env:DB_USERNAME="user_user"
$env:DB_PASSWORD="user_pass"
```

## Production vs Development

### Development (`dev` profile - default)

- `ddl-auto: update` - Auto-creates/updates schema
- `show-sql: true` - Shows SQL queries
- Uses localhost database

### Production (`prod` profile)

- `ddl-auto: validate` - Only validates schema (doesn't modify)
- `show-sql: false` - Hides SQL queries
- Uses environment variables for database connection
- Requires manual schema migration (Flyway/Liquibase recommended)

## Next Steps

1. **Start the database:** `docker-compose up -d postgres-user`
2. **Run the service:** `mvn spring-boot:run`
3. **Test the API:** Use curl or Postman
4. **Check the data:** Query the database directly

## Additional Resources

- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Hibernate: https://hibernate.org/
