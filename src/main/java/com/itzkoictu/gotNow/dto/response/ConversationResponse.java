package com.itzkoictu.gotNow.dto.response;


import com.itzkoictu.gotNow.model.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConversationResponse {
    private String id;
    private Long adminId;
    private Long userId;
    private String admin;
    private String username;
    private String avatarUser;
    private Message lastMessage;

}
