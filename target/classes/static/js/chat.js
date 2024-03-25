// Corrected chat.js
function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const message = messageInput.value.trim();

    if (message) {
        const userUuid = window.userUuid; // Assuming userUuid is globally defined in your HTML
        console.log(userUuid); // Correct placement for logging userUuid

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
            return response.json(); // Parse the JSON directly here.
        })
        .then(data => {
            const serverMessage = `Server: ${data.response}`;
            appendMessage(serverMessage, 'server-message');
        })
        .catch(error => {
            console.error('Error sending message:', error);
        });

        appendMessage(`You: ${message}`, 'user-message');
        messageInput.value = ''; // Clear the input after sending
    }
}

function appendMessage(text, className) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', className);
    messageDiv.textContent = text;
    const chatArea = document.getElementById('chat-area');

    // If using `flex-direction: column-reverse;`, use `prepend()` instead of `appendChild()`
    chatArea.prepend(messageDiv);

    // Adjust scrolling if necessary
    chatArea.scrollTop = chatArea.scrollHeight; // May need adjustment based on layout
}


// Attach the event listener for the 'Enter' key press in the message input field
document.addEventListener('DOMContentLoaded', () => {
    const messageInput = document.getElementById('message-input');
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) { // Prevents sending on Shift+Enter
            e.preventDefault(); // Prevent form submission or newline in case of textarea
            sendMessage();
        }
    });

    // Assuming you have a send button with id 'sendButton'
    const sendButton = document.getElementById('sendButton');
    if (sendButton) {
        sendButton.addEventListener('click', sendMessage);
    }
});
