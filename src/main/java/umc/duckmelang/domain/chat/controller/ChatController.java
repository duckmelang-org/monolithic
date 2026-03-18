package umc.duckmelang.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.chat.dto.response.ChatMessageResponseDto;
import umc.duckmelang.domain.chat.dto.response.ChatRoomListResponseDto;
import umc.duckmelang.domain.chat.service.ChatService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat", description = "채팅 API")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "내 채팅방 목록 조회", description = "내가 참여 중인 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomListResponseDto>> getChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(chatService.getChatRooms(userDetails.getMemberId()));
    }

    @Operation(summary = "이전 메시지 조회", description = "채팅방의 이전 메시지를 최신순으로 페이징 조회합니다.")
    @GetMapping("/{roomId}/messages")
    public ApiResponse<Slice<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.onSuccess(chatService.getMessages(roomId, page, size));
    }
}
