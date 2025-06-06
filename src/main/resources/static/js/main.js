'use strict';

let stompClient = null;
let username = null;

// Get username from authenticated user
function initializeChat() {
    const authenticatedUsernameElement = document.querySelector('#authenticatedUsername');
    if (authenticatedUsernameElement) {
        username = authenticatedUsernameElement.value;
    }
    
    if (username) {
        document.querySelector('#chat-page').classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    } else {
        // Redirect to login if no authenticated user
        window.location.href = '/login';
    }
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({
            sender: username,
            type: 'JOIN'
        })
    );

    document.querySelector('.connecting').classList.add('hidden');
}

function onError(error) {
    document.querySelector('.connecting').textContent = 
        'Could not connect to WebSocket server. Please refresh this page to try again!';
    document.querySelector('.connecting').style.color = 'red';
}

function sendMessage(event) {
    const messageInput = document.querySelector('#message');
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const messageArea = document.querySelector('#messageArea');
    const messageElement = document.createElement('li');

    messageElement.classList.add('message-data');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('span');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        avatarElement.style['padding'] = '5px 10px';
        avatarElement.style['margin-right'] = '10px';
        avatarElement.style['border-radius'] = '50%';
        avatarElement.style['color'] = 'white';
        avatarElement.style['font-weight'] = 'bold';
        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('strong');
        const usernameText = document.createTextNode(message.sender + ': ');
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('span');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    if (message.time) {
        const timeElement = document.createElement('small');
        timeElement.classList.add('message-data-time');
        const timeText = document.createTextNode(' (' + message.time + ')');
        timeElement.appendChild(timeText);
        textElement.appendChild(timeElement);
    }

    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    const colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];
    return colors[Math.abs(hash % colors.length)];
}

// Note: LEAVE messages are automatically handled by WebSocketEventListener
// when the WebSocket connection is closed, so no manual beforeunload event needed

// Initialize chat when page loads
window.addEventListener('DOMContentLoaded', function() {
    initializeChat();
});

document.querySelector('#messageForm').addEventListener('submit', sendMessage, true); 