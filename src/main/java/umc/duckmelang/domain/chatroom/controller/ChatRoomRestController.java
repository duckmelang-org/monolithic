package umc.duckmelang.domain.chatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.chatroom.converter.ChatRoomConverter;
import umc.duckmelang.domain.chatroom.dto.ChatRoomResponseDto;
import umc.duckmelang.domain.chatroom.service.ChatRoomQueryService;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.security.user.CustomUserDetails;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatRoomRestController {
    private final ChatRoomQueryService chatRoomQueryService;

    @GetMapping("")
    @CommonApiResponses
    @Operation(summary = "특정 멤버가 참여한 채팅방 전체 목록 조회",description = "이 api의 response로 넘기는 chatRoomId를 채팅방 세부 내역 조회 api의 request로 주시면 됩니다.\n status는 채팅방의 상태를 의미합니다")
    public ApiResponse<ChatRoomResponseDto.ChatRoomItemListDto> getChatRoomItemList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                    @RequestParam int page){
        Page<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtoList = chatRoomQueryService.findAllChatRooms(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(ChatRoomConverter.toChatRoomItemListDto(chatRoomItemDtoList));
    }

    @GetMapping("/ongoing")
    @CommonApiResponses
    @Operation(summary = "특정 멤버가 참여한 채팅방 중 진행중 목록 조회",description = "이 api의 response로 넘기는 chatRoomId를 채팅방 세부 내역 조회 api의 request로 주시면 됩니다.")
    public ApiResponse<ChatRoomResponseDto.ChatRoomItemListDto> getChatRoomOngoingItemList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                           @RequestParam int page){
        Page<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtoList = chatRoomQueryService.findOngoingChatRooms(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(ChatRoomConverter.toChatRoomItemListDto(chatRoomItemDtoList));
    }

    @GetMapping("/confirmed")
    @CommonApiResponses
    @Operation(summary = "특정 멤버가 참여한 채팅방 중 동행확정 목록 조회",description = "이 api의 response로 넘기는 chatRoomId를 채팅방 세부 내역 조회 api의 request로 주시면 됩니다.")
    public ApiResponse<ChatRoomResponseDto.ChatRoomItemListDto> getChatRoomConfirmedItemList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                             @RequestParam int page){
        Page<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtoList = chatRoomQueryService.findConfirmedChatRooms(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(ChatRoomConverter.toChatRoomItemListDto(chatRoomItemDtoList));
    }

    @GetMapping("/terminated")
    @CommonApiResponses
    @Operation(summary = "특정 멤버가 참여한 채팅방 중 종료 목록 조회",description = "이 api의 response로 넘기는 chatRoomId를 채팅방 세부 내역 조회 api의 request로 주시면 됩니다.")
    public ApiResponse<ChatRoomResponseDto.ChatRoomItemListDto> getChatRoomTerminatedItemList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                              @RequestParam int page){
        Page<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtoList = chatRoomQueryService.findTerminatedChatRooms(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(ChatRoomConverter.toChatRoomItemListDto(chatRoomItemDtoList));
    }

    @GetMapping("/{chatRoomId}")
    @CommonApiResponses
    @Operation(summary = "채팅방 세부 내역 조회(채팅 메세지 조회 별도)",description = "채팅 메세지 조회 api : \"/chat/{chatRoomId}/messages\"\n chatRoomStatus, applicationStatus, hasReviewed, postOwner이 화면 플래그입니다")
    public ApiResponse<ChatRoomResponseDto.ChatRoomDetailDto> getChatRoomItemList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                    @RequestParam Long chatRoomId){
        return ApiResponse.onSuccess(chatRoomQueryService.findChatRoomDetail(userDetails.getMemberId(), chatRoomId));
    }
}
