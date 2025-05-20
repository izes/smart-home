# Smart Home System

A residential automation system that allows you to control and monitor home devices through a modern web interface.

## Project Structure

The project is divided into three main parts:

### Backend (Microservices)

- **api-gateway**: API Gateway that manages routing between services
- **event-service**: Service responsible for managing system events
- **device-management-service**: Service that manages home devices

### Frontend

- Web interface developed with Vue.js
- Dashboard for device visualization and control
- Real-time event support

### Infrastructure

- Docker and Docker Compose for containerization
- MySQL for data persistence
- Grafana and Loki for monitoring and logging

## 📋 Prerequisites

Before you begin, make sure you have installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Node.js](https://nodejs.org/) (version 16 or higher)
- [Java](https://adoptium.net/) (version 17 or higher)
- [Maven](https://maven.apache.org/download.cgi)

## How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/izes/smart-home
cd smart-home
```

### 2. Backend Setup

#### Build Project

```bash
mvn clean package -DskipTests
```

#### Start Services with Docker Compose

```bash
npm install -D @vue/cli-service
docker-compose up -d
```

### 3. Frontend Setup

#### Install Dependencies

```bash
cd frontend
npm install
```

#### Start Development Server

```bash
npm run serve
```

### 4. Access the Application

- Frontend: http://localhost:3002
- API Gateway: http://localhost:8080
- Grafana: http://localhost:3000

## 🔧 Configuration

### Environment Variables

The project uses environment variables for configuration. You can configure them in the `.env` file:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/smart_home
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Services
EVENT_SERVICE_URL=http://localhost:8081
DEVICE_MANAGEMENT_SERVICE_URL=http://localhost:8080
```

### Ports Used

- Frontend: 3002
- API Gateway: 8080
- Event Service: 8081
- Device Management Service: 8080
- MySQL: 3306
- Grafana: 3000
- Loki: 3100

## 📊 Monitoring

The system includes monitoring through Grafana and Loki:

1. Access Grafana at http://localhost:3000
2. Default credentials:
   - Username: admin
   - Password: admin
3. Configure the Loki data source (http://loki:3100)
4. Import the dashboards available in `grafana/dashboards/`
