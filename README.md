# Sentiment Analysis Feedback App

A full-stack application that allows users to submit feedback and analyzes the sentiment using machine learning. The system includes a Spring Boot backend, a Flask ML service, and a React frontend.

## Project Structure

- **Spring Boot Backend**: Handles API endpoints, database operations, and communicates with the ML service
- **Flask ML Service**: Processes text and returns sentiment analysis results using transformer-based models
- **React Frontend**: Provides an interface for submitting feedback and visualizing sentiment data

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Node.js 16+ and npm
- Python 3.8+
- MySQL 8.0+ or PostgreSQL

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/sentiment-analysis-app.git
cd sentiment-analysis-app
```

### 2. Backend Setup

#### Database Configuration

Create a MySQL or PostgreSQL database:

```sql
CREATE DATABASE feedback;
```

#### Configure application.properties

Create a file at `src/main/resources/application.properties`:

```properties
# Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/feedback
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ML Service URL
ml.service.url=http://localhost:5000/analyze

# Server Port
server.port=8080
```

Adjust the database connection settings as needed.

#### Build and Run the Backend

```bash
mvn clean install
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`.

### 3. ML Service Setup

#### Create a Python Virtual Environment

```bash
cd ml-service
python -m venv venv

# Activate on Windows
venv\Scripts\activate

# Activate on macOS/Linux
source venv/bin/activate
```

#### Install Dependencies

```bash
pip install -r requirements.txt
```

#### Run the Flask Service

```bash
python run.py
```

The ML service will run on `http://localhost:5000`.

### 4. Frontend Setup

```bash
cd frontend
npm install
```

#### Configure API URL

Create a `.env` file in the frontend directory:

```
VITE_API_URL=http://localhost:8080/api
```

#### Run the Frontend

```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`.

## Usage

1. Open `http://localhost:5173` in your browser
2. Navigate to the "Submit Feedback" page to add new feedback
3. View sentiment analysis results on the dashboard

## Features

- **Feedback Submission**: Users can submit feedback text
- **Sentiment Analysis**: Automatically analyzes feedback as positive, negative, or neutral
- **Dashboard**: Visualizes sentiment trends and statistics
- **Filtering**: Filter feedback by sentiment and date range

## Fallback Mechanism

The system includes a fallback sentiment analysis method if the ML service is unavailable, ensuring continuous operation.

## Advanced ML Features

- Transformer-based sentiment analysis
- Confidence scores
- Text preprocessing for improved accuracy

### Backend Issues

- Ensure your database server is running
- Check application.properties for correct database credentials
- Review Java logs for detailed error information

### ML Service Issues

- Make sure all Python dependencies are installed
- Check if port 5000 is available
- Review Flask logs for model loading issues

### Frontend Issues

- Verify the API URL in the .env file
- Check browser console for CORS or API errors
- Ensure Node.js version is compatible

## License

MIT
