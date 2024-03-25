package service;

import com.cloud.queriosity.entity.Message;
import com.cloud.queriosity.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> findRecentMessagesByUserUuid(String userUuid) {
        return messageRepository.findTop50ByUserUuidOrderByTimestampDesc(userUuid);
    }

    public Message saveMessage(String userUuid, String content) {
        Message message = new Message();
        message.setUserUuid(userUuid);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

}
