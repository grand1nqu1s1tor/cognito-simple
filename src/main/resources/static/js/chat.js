function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const message = messageInput.value.trim();

    if (message) {
        const userUuid = window.userUuid; //  userUuid is globally defined in  HTML
        console.log(userUuid);

        fetch('/send-message', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Retrieve CSRF token and header from meta tags
                [document.querySelector('meta[name="_csrf_header"]').content]: document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify({ content: message, userUuid: userUuid }) // Correctly formatted object
        })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok.');
            return response.json(); //
        })
        .then(data => {
            const serverMessage = `Server: ${data.response}`;
            appendMessage(serverMessage, 'server-message');
        })
        .catch(error => {
            console.error('Error sending message:', error);
        });

        appendMessage(`You: ${message}`, 'user-message');
        messageInput.value = '';
    }
}

function appendMessage(text, className) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', className);
    messageDiv.textContent = text;
    const chatArea = document.getElementById('chat-area');

    chatArea.prepend(messageDiv);

    chatArea.scrollTop = chatArea.scrollHeight;
}


document.addEventListener('DOMContentLoaded', () => {
    const messageInput = document.getElementById('message-input');
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // Assuming you have a send button with id 'sendButton'
    const sendButton = document.getElementById('sendButton');
    if (sendButton) {
        sendButton.addEventListener('click', sendMessage);
    }
});
