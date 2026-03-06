var stompClient = null;
var username = null;

function connect() {
    username = document.querySelector('#name').value.trim();
    if(username) {
        document.querySelector('#username-page').classList.add('hidden');
        document.querySelector('#chat-page').classList.remove('hidden');
        var socket = new SockJS('/ws-chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, (err) => alert("Connection Error"));
    }
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
}

function sendMessage() {
    var content = document.querySelector('#message').value.trim();
    if(content && stompClient) {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({sender: username, content: content, type: 'CHAT'}));
        document.querySelector('#message').value = '';
    }
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageArea = document.querySelector('#messageArea');
    var li = document.createElement('li');
    li.className = (message.type === 'CHAT') ? 'message-bubble' : 'event';
    li.innerHTML = `<strong>${message.sender}</strong>: ${message.content} <small>${message.timestamp || ''}</small>`;
    messageArea.appendChild(li);
    messageArea.scrollTop = messageArea.scrollHeight;
}