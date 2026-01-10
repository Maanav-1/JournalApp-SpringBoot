# JournalApp Backend (Spring Boot)

A robust, production-ready REST API built with **Spring Boot 3.4.1** that powers a personalized journaling platform. This backend handles secure authentication, AI-driven insights, and automated user notifications.

### Key Features

* **Secure Authentication**: Implements **Spring Security** with a stateless **JWT** architecture and **GitHub OAuth2** integration.
* **AI Sentiment Analysis**: Integrates the **Google Gemini API** to automatically analyze the emotional tone of journal entries.
* **Automated Insights**: A scheduled service that aggregates weekly sentiments and sends personalized reports via **Spring Mail** (SMTP).
* **Advanced Security**: Custom **AOP-based rate limiting** using **Redis** to protect API endpoints from abuse.
* **Role-Based Access Control (RBAC)**: Distinct permissions for `USER` and `ADMIN` roles, including a protected admin console for user management.
* **Cloud Native**: Fully containerized with **Docker** and optimized for deployment on platforms like **Render**.

### 🛠️ Tech Stack

* **Framework**: Spring Boot 3.4.1
* **Database**: MongoDB (via MongoDB Atlas)
* **Caching/Rate-Limiting**: Redis
* **Security**: Spring Security, JWT, OAuth2
* **AI**: Google Gemini API
* **Containerization**: Docker

### ⚙️ Environment Variables Required

To run this application, configure the following environment variables (locally in `application.yml` or in your cloud dashboard):

| Variable | Description |
| --- | --- |
| `SPRING_DATA_MONGODB_URI` | Your MongoDB Atlas connection string |
| `PROJECT_JWT_SECRET` | Secret key for signing JWT tokens |
| `SPRING_MAIL_PASSWORD` | App password for Gmail SMTP |
| `GEMINI_API_KEY` | Your Google Generative AI API key |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID` | GitHub OAuth Client ID |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET` | GitHub OAuth Client Secret |
