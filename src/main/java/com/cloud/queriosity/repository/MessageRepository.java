package com.cloud.queriosity.repository;

import com.cloud.queriosity.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findTop50ByUserUuidOrderByTimestampAsc(String userUuid);

}
