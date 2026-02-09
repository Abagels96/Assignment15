# Railway Deployment Guide

This guide walks you through deploying the Assignment15 MomTracker application to Railway.

## Prerequisites

1. A Railway account (sign up at [railway.app](https://railway.app))
2. A Google Cloud project with OAuth 2.0 credentials configured
3. Git repository with your code (or deploy directly from Railway)

## Step 1: Create a Railway Project

1. Log in to [Railway](https://railway.app)
2. Click "New Project"
3. Select "Deploy from GitHub repo" (if your code is on GitHub) or "Empty Project" (to deploy manually)

## Step 2: Add PostgreSQL Database

1. In your Railway project, click "+ New"
2. Select "Database" → "Add PostgreSQL"
3. Railway will automatically create a PostgreSQL database and provide connection details

## Step 3: Configure Your Application Service

1. If deploying from GitHub, Railway will auto-detect your Spring Boot application
2. If deploying manually, click "+ New" → "GitHub Repo" or "Empty Service"
3. For manual deployment, you'll need to configure the build and start commands

### Build Configuration

- **Build Command**: `mvn -q -DskipTests package`
- **Start Command**: `java -jar target/Assignment15-0.0.1-SNAPSHOT.jar`

## Step 4: Set Environment Variables

In your Railway service settings, add the following environment variables:

### Database Variables (Auto-configured by Railway)

Railway automatically provides these when you add a PostgreSQL database:
- `JDBC_DATABASE_URL` - Automatically set by Railway (format: `jdbc:postgresql://host:port/database`)
- `JDBC_DATABASE_USERNAME` - Automatically set by Railway
- `JDBC_DATABASE_PASSWORD` - Automatically set by Railway

**Note**: Railway may also provide `DATABASE_URL` in PostgreSQL format. If so, you may need to convert it to JDBC format or Railway will provide `JDBC_DATABASE_URL` directly.

### OAuth2 Google Configuration

Set these in your Railway service environment variables:

- `GOOGLE_CLIENT_ID` - Your Google OAuth 2.0 Client ID
- `GOOGLE_CLIENT_SECRET` - Your Google OAuth 2.0 Client Secret
- `GOOGLE_REDIRECT_URI` - Must be set to: `https://your-app-name.up.railway.app/login/oauth2/code/google`
  - Replace `your-app-name` with your actual Railway app domain
  - You can find your domain in Railway service settings under "Settings" → "Domains"

### Optional Configuration Variables

These have defaults but can be overridden:

- `PORT` - Automatically set by Railway (usually don't need to set this)
- `SHOW_SQL` - Set to `false` in production (default: `true`)
- `FORMAT_SQL` - Set to `false` in production (default: `true`)
- `SECURITY_LOG_LEVEL` - Set to `INFO` or `WARN` in production (default: `DEBUG`)
- `SECURITY_USER_NAME` - Default Spring Security user (default: `user`)
- `SECURITY_USER_PASSWORD` - Default Spring Security password (default: `{noop}Password123!`)
- `DATABASE_PLATFORM` - Can be set to `org.hibernate.dialect.PostgreSQLDialect` if auto-detection fails

## Step 5: Update Google OAuth Redirect URI

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Navigate to "APIs & Services" → "Credentials"
3. Find your OAuth 2.0 Client ID
4. Add your Railway app URL to "Authorized redirect URIs":
   - `https://your-app-name.up.railway.app/login/oauth2/code/google`
5. Save the changes

## Step 6: Deploy

1. If using GitHub, push your code and Railway will automatically deploy
2. If deploying manually, Railway will build and start your application
3. Monitor the deployment logs in Railway dashboard

## Step 7: Verify Deployment

1. Once deployed, Railway will provide a public URL (e.g., `https://your-app-name.up.railway.app`)
2. Visit the URL in your browser
3. Test the application functionality:
   - User registration
   - Login (both form and Google OAuth)
   - Application features

## Troubleshooting

### Database Connection Issues

- Verify that `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, and `JDBC_DATABASE_PASSWORD` are set
- Check Railway database logs for connection errors
- Ensure the PostgreSQL service is running in Railway

### OAuth Issues

- Verify `GOOGLE_REDIRECT_URI` matches your Railway app URL exactly
- Ensure the redirect URI is added in Google Cloud Console
- Check that `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correct

### Build Failures

- Check Railway build logs for Maven errors
- Ensure Java 21 is available (Railway should auto-detect from `pom.xml`)
- Verify all dependencies are resolvable

### Port Issues

- Railway automatically sets the `PORT` environment variable
- The application is configured to use `${PORT:8080}`, so it should work automatically
- If issues persist, check Railway service logs

## Environment Variables Summary

### Required for Production

| Variable | Description | Example |
|----------|-------------|---------|
| `JDBC_DATABASE_URL` | PostgreSQL connection URL | Auto-set by Railway |
| `JDBC_DATABASE_USERNAME` | Database username | Auto-set by Railway |
| `JDBC_DATABASE_PASSWORD` | Database password | Auto-set by Railway |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID | `757667480872-...` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret | `GOCSPX-...` |
| `GOOGLE_REDIRECT_URI` | OAuth redirect URI | `https://app.up.railway.app/login/oauth2/code/google` |

### Recommended for Production

| Variable | Value | Reason |
|----------|-------|--------|
| `SHOW_SQL` | `false` | Reduces log noise |
| `FORMAT_SQL` | `false` | Reduces log noise |
| `SECURITY_LOG_LEVEL` | `INFO` or `WARN` | Reduces sensitive log output |

## Additional Notes

- The application uses Spring Session JDBC, which will automatically create session tables in your PostgreSQL database
- Hibernate will auto-create/update database schema on first startup (`spring.jpa.hibernate.ddl-auto=update`)
- For production, consider setting `spring.jpa.hibernate.ddl-auto=validate` after initial deployment to prevent accidental schema changes
- Railway provides automatic HTTPS, so your app will be accessible via HTTPS

## Support

For Railway-specific issues, consult [Railway Documentation](https://docs.railway.app).
For Spring Boot deployment issues, consult [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html).

