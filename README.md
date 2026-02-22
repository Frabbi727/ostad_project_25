# GitHub Repo Reader (Module 25)

Spring Boot backend that reads data from `https://github.com/abusaeed2433/JavaInREADME`, stores parsed content in a persistent database, and serves API endpoints.

## Features
- Daily-capable automatic sync (configured every 6 hours by default).
- Persistent storage using file-based H2 (`./data/repo-reader-db`).
- GitHub README parsing into `topic -> sub-topic -> content`.
- Contributor ingestion from GitHub contributors API.

## Endpoints
- `GET /api/v1/read_contributions`
- `GET /api/v1/read_indices`
- `GET /api/v1/read_blog?topic_name={x}&sub_topic_name={y}`

## Run
```bash
./gradlew bootRun
```

## Config
Defined in `src/main/resources/application.properties`:
- `app.github.owner`
- `app.github.repo`
- `app.github.branch`
- `app.github.readme-path`
- `app.github.token` (optional, recommended to avoid rate limit)
- `app.sync.cron`

You can override with environment variables, for example:
```bash
APP_GITHUB_TOKEN=ghp_xxx ./gradlew bootRun
```
