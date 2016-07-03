package com.bgs.domain.chat.repository;

import com.bgs.domain.chat.model.ChatMessage;

import java.util.Date;
import java.util.List;

/**
 * Created by zhufre on 6/28/2016.
 */
public interface IMessageRepository {
    ChatMessage getMessageById(int id);
    ChatMessage getLastMessageByContact(int contactId);
    List<ChatMessage> getListMessageByContactAndDate(int contactId, Date date);
    List<ChatMessage> getListMessageByContact(int contactId);
    List<ChatMessage> getListNewMessageByContact(int contactId);
    long getNewMessageCount();
    long getNewMessageCountByContact(int contactId);
    void createOrUpdate(ChatMessage message);
}
