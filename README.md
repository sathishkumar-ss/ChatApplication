# Spring Boot WebSocket Chat Application

A real-time chat application built using Spring Boot and WebSocket.

## Features

- Real-time messaging using WebSocket
- User join/leave notifications
- Message timestamps
- Clean and responsive UI
- User avatars with unique colors

## Technologies Used

- Spring Boot 3.5.0
- WebSocket with STOMP
- SockJS
- Bootstrap 5.3.2
- jQuery 3.7.1
- Lombok

## Prerequisites

- Java 21
- Maven

## Running the Application

1. Clone the repository
```bash
git clone <your-repository-url>
```

2. Navigate to the project directory
```bash
cd chat
```

3. Run the application using Maven
```bash
mvn spring-boot:run
```

4. Open your browser and visit `http://localhost:8080`

## How to Use

1. Open the application in your browser
2. Enter your name to join the chat
3. Start sending messages
4. Messages will be delivered to all connected users in real-time
5. When you close the browser window, other users will be notified that you've left

## Project Structure

```
src/main/java/com/example/chat/
├── ChatApplication.java           # Main Spring Boot Application
├── config/
│   ├── WebSocketConfig.java      # WebSocket Configuration
│   └── WebSocketEventListener.java # WebSocket Event Handlers
├── controller/
│   └── ChatController.java       # WebSocket Message Controllers
└── model/
    └── ChatMessage.java          # Message Model
``` 