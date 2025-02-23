package umc.duckmelang.domain.post.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.postidol.domain.PostIdol;
import umc.duckmelang.domain.postimage.domain.PostImage;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.PostException;
import umc.duckmelang.global.common.BaseEntity;
import umc.duckmelang.global.common.serializer.LocalDateSerializer;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    @Column(name = "title", columnDefinition = "TINYTEXT", nullable = false)
    private String title;

    @Column(name = "event_date", columnDefinition = "DATE", nullable = false)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;

    @Column(name = "wanted", columnDefinition = "BIT", nullable = false)
    private Short wanted;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostIdol> postIdolList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImageList = new ArrayList<>();

    //n:1 단방향 고려
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applicationList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ChatRoom> chatRoomList = new ArrayList<>();


    // 연관관계 편의 메서드
    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getPostList().remove(this);
        }
        this.member = member;
        if (member != null) {
            member.getPostList().add(this);
        }
    }

    // 연관관계 편의 메서드
    public void setEventCategory(EventCategory eventCategory) {
        if (this.eventCategory != null) {
            this.eventCategory.getPostList().remove(this);
        }
        this.eventCategory = eventCategory;
        if (eventCategory != null) {
            eventCategory.getPostList().add(this);
        }
    }

//    연관관계 편의 메서드
    public void setPostIdolList(List<PostIdol> postIdolList) {
        if (this.postIdolList != null) {
            this.postIdolList.forEach(postIdol -> postIdol.setPost(null));
        }
        this.postIdolList = postIdolList;
        if (postIdolList != null) {
            postIdolList.forEach(postIdol -> postIdol.setPost(this));
        }

    }

    public void toggleWanted() {
        if (this.wanted == 0) {
            this.wanted = 1;
        } else if (this.wanted == 1) {
            this.wanted = 0;
        } else {
            throw new PostException(ErrorStatus.INVALID_WANTED);  // 원하시는 ErrorStatus를 여기서 던질 수 있습니다.
        }
    }

    public void setWanted(Short wanted) {
        if (wanted == 0 || wanted == 1) {
            this.wanted = wanted;
        } else {
            throw new PostException(ErrorStatus.INVALID_WANTED);  // 유효하지 않은 wanted 값에 대한 예외 처리
        }
    }

    public void setPostImageList(List<PostImage> postImageList){
        if(this.postImageList !=null){
            this.postImageList.forEach(postImage-> postImage.setPost(null));
        }
        this.postImageList = postImageList;
        if (postImageList != null) {
            postImageList.forEach(postImage-> postImage.setPost(this));
        }
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}