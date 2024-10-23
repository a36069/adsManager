# Ad Manager Application

A Spring Boot application for managing advertising data, including impressions and clicks, with functionalities for data loading, metrics computation, and recommendations generation.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
    - [Running the Application](#running-the-application)
    - [Accessing the API](#accessing-the-api)
- [API Documentation](#api-documentation)
- [Docker Compose Services](#docker-compose-services)
- [Logging and Reports](#logging-and-reports)

---

## Introduction

The Ad Manager Application is designed to process advertising data by loading impressions and clicks from JSON files, calculating metrics, and providing recommendations for optimizing ad performance. The application uses Spring Boot, Hibernate, and Redis, and it runs within Docker containers orchestrated by Docker Compose.

## Features

- **Data Loading**: Upload and process `impressions.json` and `clicks.json` files via an API endpoint.
- **Metrics Calculation**: Compute metrics such as revenue per app and country.
- **Recommendations**: Generate recommendations to improve ad performance.
- **API Documentation**: Interactive API documentation available via Swagger UI.
- **Dockerized Deployment**: Easily deploy the application using Docker and Docker Compose.
- **Logging and Reporting**: Detailed logs and JSON reports are generated for metrics and recommendations.

## Architecture

- **Backend**: Spring Boot application with RESTful APIs.
- **Database**: MySQL for persistent storage.
- **Caching**: Redis for caching and tracking processing status.
- **Containerization**: Docker for containerizing services.
- **Orchestration**: Docker Compose for managing multi-container applications.

## Prerequisites

Ensure you have the following installed on your system:

- **Java Development Kit (JDK)**: Version 17 or higher.
- **Maven**: For building the application.
- **Docker**: For containerization.
- **Docker Compose**: For orchestrating Docker containers.

## Installation

### Clone the Repository

```bash
git clone https://github.com/yourusername/ad-manager.git
cd ad-manager
```

### Build the Application

Build the Spring Boot application using Maven:

```bash
mvn clean package
```

This will generate an executable JAR file in the target/ directory.

### Build Docker Images

Build the Docker images using Docker Compose:

```bash
docker-compose build
```

## Usage

### Running the Application

Start the application along with MySQL and Redis services:
```bash
docker-compose up
```

This command will:

*  Start the MySQL database container.
*  Start the Redis container.
*  Start the Ad Manager application container.
*  Set up the necessary networks and volume mappings.

### Accessing the API

The application exposes APIs on port **8093**.

    Base URL: http://localhost:8093/api

### Data Loading Endpoint

Upload your impressions.json and clicks.json files:

```
POST /api/v1/ads/load
Content-Type: multipart/form-data

Form Data:
- impressionsFile: [impressions.json file]
- clicksFile: [clicks.json file]
```

### Metrics Endpoint

Retrieve calculated metrics:

`GET /api/v1/ads/metrics`

### Recommendations Endpoint

Retrieve generated recommendations:

`GET /api/v1/ads/recommendations`

## API Documentation

Interactive API documentation is available via Swagger UI.

Swagger UI URL:

`http://localhost:8093/api/swagger-ui/index.html
`

Use this interface to explore and test the APIs.

## Docker Compose Services

The docker-compose.yml defines the following services:

* **app**: The Ad Manager application.
* **mysql**: MySQL database service.
* **redis**: Redis caching service.

Environment Variables

The application uses environment variables for configuration:

- **SPRING_DATASOURCE_URL**: JDBC URL for the MySQL database.
- **SPRING_DATASOURCE_USERNAME**: Database username.
- **SPRING_DATASOURCE_PASSWORD**: Database password.
- **SPRING_REDIS_HOST**: Redis host (set to redis in Docker Compose).
- **SPRING_REDIS_PORT**: Redis port (default 6379).
- **SPRING_JPA_HIBERNATE_DDL_AUTO**: Hibernate DDL auto setting (e.g., validate).

### Volumes and Networks

    Volumes:
        mysql_data: Persists MySQL data between container restarts.
        ./reports:/app/reports: Maps the reports directory for report generation.
    Networks:
        app-network: A bridge network that allows services to communicate.

### Logging and Reports

**Logging**: Logs are written to `/app/logs/adsManager` inside the container and can be accessed via Docker logs.

**Reports**: Metrics and recommendations are written to JSON files in the reports directory.

## Accessing Reports

```bash 
ls reports/
```
Reports are generated in the reports directory on the host machine:

Files are named with timestamps, for example:

    metrics_20231023153000.json
    recommendations_20231023153000.json