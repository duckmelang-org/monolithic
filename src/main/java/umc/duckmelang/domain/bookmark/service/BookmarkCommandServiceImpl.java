package umc.duckmelang.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.bookmark.converter.BookmarkConverter;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.repository.BookmarkRepository;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.notification.service.NotificationCommandService;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.notificationsetting.service.NotificationSettingQueryService;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostRequestDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.postimage.dto.PostThumbnailResponseDto;
import umc.duckmelang.domain.postimage.service.PostImageQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.*;

import java.util.List;

import static umc.duckmelang.domain.notification.domain.enums.NotificationType.BOOKMARK;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkCommandServiceImpl implements BookmarkCommandService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationCommandService notificationCommandService;
    private final NotificationSettingQueryService notificationSettingQueryService;
    private final PostImageQueryService postImageQueryService;

    @Override
    public Bookmark joinBookmark(Long postId, Long memberId) {
//        Member 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
//        Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

//        이미 스크랩되어있는지 확인
        if(bookmarkRepository.existsByMemberAndPost(member,post)){
            throw new BookmarkException(ErrorStatus.DUPLICATE_BOOKMARK);
        }


//        게시물 대표 이미지 URL 조회
        PostThumbnailResponseDto postThumbnail = postImageQueryService.getLatestPostImage(postId);
        String postImageUrl = (postThumbnail != null) ? postThumbnail.getPostImageUrl() : null;

        Bookmark bookmark = BookmarkConverter.toBookmark(member, post);
        NotificationSetting notificationSetting = notificationSettingQueryService.findNotificationSetting(post.getMember().getId());

        if(notificationSetting.getBookmarkNotificationEnabled()){
            notificationCommandService.send(
                    member, post.getMember(), BOOKMARK, "내 동행글이 스크랩되었어요", postImageUrl
            );
        }
        return bookmarkRepository.save(bookmark);
    }

    @Override
    public void deleteBookmark(Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new BookmarkException(ErrorStatus.INVALID_BOOKMARK));
        bookmarkRepository.delete(bookmark);
    }
}


