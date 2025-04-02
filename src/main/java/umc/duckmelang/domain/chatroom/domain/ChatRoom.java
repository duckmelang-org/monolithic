package umc.duckmelang.domain.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.config.core.userdetails.UserDetailsMapFactoryBean;
import umc.duckmelang.domain.chatroom.domain.enums.ChatRoomStatus;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.PostException;
import umc.duckmelang.global.common.BaseEntity;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;   // 게시글(작성자 쪽 id 조회 가능)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member otherMember;   // 게시글을 보는 쪽 회원

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'ONGOING'")
    private ChatRoomStatus chatRoomStatus;

    // 연관관계 편의 메서드
    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getChatRoomList().remove(this);
        }
        this.post = post;
        if (post != null) {
            post.getChatRoomList().add(this);
        }
    }

    public void setMember(Member member) {
        if (this.otherMember != null) {
            this.otherMember.getChatRoomList().remove(this);
        }
        this.otherMember = member;
        if (member != null) {
            member.getChatRoomList().add(this);
        }
    }

    public boolean hasTerminated(){
        if (this.post == null) throw new PostException(ErrorStatus.POST_NOT_FOUND);
        boolean result =  post.getEventDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()));
        if (result)
            this.chatRoomStatus = ChatRoomStatus.TERMINATED;
        return result;
    }

    public void setChatRoomStatus(ChatRoomStatus chatRoomStatus) {
        this.chatRoomStatus = chatRoomStatus;
    }
}