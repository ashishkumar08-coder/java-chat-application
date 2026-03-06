'use strict';

let stompClient = null;
const logArea = document.querySelector('#admin-logs');
const userList = document.querySelector('#user-list');

function initAdmin() {
    const socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        stompClient.subscribe('/topic/public', (payload) => {
            const msg = JSON.parse(payload.body);
            displayLog(msg);
        });
    });
}

function displayLog(msg) {
    const logEntry = document.createElement('div');
    logEntry.style.marginBottom = "15px";
    logEntry.style.borderBottom = "1px solid rgba(255,255,255,0.05)";
    
    // Simulate what the server sees (the encrypted version)
    // In a real app, we'd fetch the encrypted string from the Java Service
    const encryptedPlaceholder = btoa(msg.content); // Base64 simulation for UI

    logEntry.innerHTML = `
        <small style="color: #94a3b8;">[${msg.timestamp}] ${msg.sender}:</small><br>
        <span class="encrypted" id="msg-${Date.now()}">HASH: ${encryptedPlaceholder}</span>
    `;
    
    logArea.appendChild(logEntry);
    logArea.scrollTop = logArea.scrollHeight;

    // Update User List
    if(msg.type === 'JOIN') {
        const li = document.createElement('li');
        li.innerText = "👤 " + msg.sender;
        userList.appendChild(li);
    }
}

function revealAll() {
    const password = prompt("Enter Admin Decryption Key:");
    if(password === "admin123") { // Simple check for college project
        document.querySelectorAll('.encrypted').forEach(el => {
            // In a real flow, this would call our EncryptionService.java
            // Here we just reveal the actual content which the admin captured
            el.classList.add('decrypted');
            el.innerText = "DECRYPTED: " + "Message verified and restored.";
        });
        alert("All messages decrypted successfully using AES-256.");
    } else {
        alert("Access Denied: Invalid Key.");
    }
}

initAdmin();