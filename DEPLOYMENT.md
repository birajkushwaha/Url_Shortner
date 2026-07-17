# Free Production Deployment Guide

This guide explains how to deploy your **URL Shortener Backend** to production for **100% free** using a combination of **Neon** (for managed PostgreSQL) and **Render** (for hosting the Docker container).

---

## Prerequisites

1. A **GitHub** account.
2. This project pushed to a repository on your GitHub account.

---

## Step 1: Create a Free PostgreSQL Database on Neon

[Neon](https://neon.tech) offers a generous free tier of managed serverless PostgreSQL that requires **no credit card**.

1. Go to [neon.tech](https://neon.tech) and sign up or sign in.
2. Click **Create Project**.
3. Name your project (e.g., `url-shortener-db`) and select the region closest to you (e.g., US East, Europe, etc.).
4. Click **Create Project**.
5. Once created, copy the connection details from the **Connection Details** box.
   - Choose the connection string dropdown and copy the components:
     - **Host**: e.g., `ep-cool-breeze-12345.us-east-2.aws.neon.tech`
     - **Database**: `neondb`
     - **Username**: `neondb_owner`
     - **Password**: *(The password shown)*
   - Or copy the JDBC connection URL directly, which looks like:
     ```text
     jdbc:postgresql://ep-cool-breeze-12345.us-east-2.aws.neon.tech/neondb?sslmode=require
     ```

---

## Step 2: Deploy the Application on Render

[Render](https://render.com) offers a free tier for hosting containerized applications.

1. Go to [render.com](https://render.com) and sign in.
2. Click **New +** in the top right and select **Web Service**.
3. Select **Build and deploy from a Git repository**, connect your GitHub account, and select your URL Shortener repository.
4. Fill in the configuration fields:
   - **Name**: Choose a name (e.g., `my-url-shortener`).
   - **Region**: Select the **same region** where you created your Neon database (to keep database requests extremely fast).
   - **Branch**: `main` (or whichever branch contains your Dockerfile).
   - **Runtime**: **Docker** (Render will automatically detect your `Dockerfile`).
   - **Instance Type**: Select **Free** ($0/month).
5. Scroll down and click **Advanced**, then click **Add Environment Variable** to add the following variables:

| Key | Value | Description |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates the PostgreSQL profile |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<neon-host>/neondb?sslmode=require` | Your Neon database JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `<neon-username>` | Your Neon database username |
| `SPRING_DATASOURCE_PASSWORD` | `<neon-password>` | Your Neon database password |

6. Click **Create Web Service**.
7. Render will build the Docker container and spin it up. Once complete, your backend will be live at:
   `https://my-url-shortener.onrender.com`

---

## Testing Local Container (Optional)

If you want to test the Docker image locally before deploying:

1. **Build the Docker Image**:
   ```bash
   docker build -t url-shortener .
   ```
2. **Run it locally with local H2 database (default profile)**:
   ```bash
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=default url-shortener
   ```
   Open `http://localhost:8080` in your browser.

3. **Run it locally connected to your production Neon PostgreSQL database**:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e SPRING_DATASOURCE_URL="jdbc:postgresql://<neon-host>/neondb?sslmode=require" \
     -e SPRING_DATASOURCE_USERNAME="<neon-username>" \
     -e SPRING_DATASOURCE_PASSWORD="<neon-password>" \
     url-shortener
   ```
