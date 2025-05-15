package umc.duckmelang.domain.chatroom.service;

import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.global.redis.concurrency.RedissonLock;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;

public class TestChatRoomCommandServiceImpl implements ChatRoomCommandService {

    @Override
    @RedissonLock(key = "'chatroom:'.concat(#request.getPostId()).concat('-').concat(#request.getSenderId())")
    public ChatRoom createChatRoom(ChatMessageRequestDto.CreateChatMessageDto request)  {
        return null;
    }
}
