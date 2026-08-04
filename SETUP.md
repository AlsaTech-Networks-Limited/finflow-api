# FinFlow API - Developer Setup Guide

## Prerequisites

- **Java 21** (Eclipse Adoptium recommended)
- **Maven 3.8+**
- **PostgreSQL 16**
- **Git**
- **GitHub Account** with access to `c-kiplimo/common` repository

## Initial Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd api
```

### 2. Configure GitHub Packages Access

This project uses a private library (`com.collicode:common`) hosted on GitHub Packages. You need to configure Maven to authenticate with GitHub.

#### Create GitHub Personal Access Token

1. Go to [GitHub Settings → Developer Settings → Personal Access Tokens → Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Maven GitHub Packages")
4. Select **only** the `read:packages` scope
5. Click "Generate token"
6. **Copy the token** (you won't be able to see it again)

#### Configure Maven Settings

**On Linux/Mac:**
```bash
mkdir -p ~/.m2
nano ~/.m2/settings.xml
```

**On Windows:**
```bash
mkdir C:\Users\<YourUsername>\.m2
notepad C:\Users\<YourUsername>\.m2\settings.xml
```

**Add this content** (replace with your GitHub username and token):

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**⚠️ Security Note:** Never commit `settings.xml` to version control - it contains your personal access token!

### 3. Configure Database

Create a PostgreSQL database:

```bash
psql -U postgres
CREATE DATABASE finflow;
\q
```

Update `src/main/resources/application.yml` with your database credentials if needed.

### 4. Build the Project

```bash
mvn clean install
```

Or if Maven is not in your PATH:

**Windows:**
```bash
C:\apache-maven-3.8.8\bin\mvn clean install
```

**Linux/Mac:**
```bash
/path/to/maven/bin/mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## Troubleshooting

### "Could not transfer artifact com.collicode:common"

- **Problem:** Maven cannot download the `com.collicode:common` dependency
- **Solution:**
  1. Verify your GitHub token has `read:packages` permission
  2. Ensure the `<server>` id in `settings.xml` matches the repository `<id>` in `pom.xml` (both should be "github")
  3. Verify you have access to the `c-kiplimo/common` repository
  4. Try: `mvn dependency:purge-local-repository -DmanualInclude=com.collicode:common`

### Flyway Migration Errors

- **Problem:** Database migration fails
- **Solution:** Ensure PostgreSQL is running and the database exists

### Port Already in Use

- **Problem:** Port 8080 is already in use
- **Solution:** Stop the process using port 8080 or change the port in `application.yml`:
  ```yaml
  server:
    port: 8081
  ```

## IDE Setup

### IntelliJ IDEA

1. Open the project folder
2. IntelliJ should auto-detect it as a Maven project
3. Go to: **File → Settings → Build, Execution, Deployment → Build Tools → Maven**
4. Verify "User settings file" points to your `settings.xml`
5. Click "Reload All Maven Projects" in the Maven tool window

### VS Code

1. Install "Extension Pack for Java"
2. Install "Spring Boot Extension Pack"
3. Open the project folder
4. VS Code should auto-detect Maven configuration

## Project Structure

See [GUIDE.md](GUIDE.md) for detailed architecture and contribution guidelines.

## Need Help?

- Read [GUIDE.md](GUIDE.md) for architecture overview
- Read [BACKEND-README.md](../BACKEND-README.md) for API documentation
- Check existing code patterns before implementing new features