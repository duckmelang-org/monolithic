package umc.duckmelang.domain.member.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.auth.domain.Auth;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.materelationship.domain.MateRelationship;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.memberidol.domain.MemberIdol;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.notification.domain.Notification;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.memberevent.domain.MemberEvent;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.common.BaseEntity;
import umc.duckmelang.global.common.serializer.LocalDateSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 30, nullable = true)
    private String nickname;

    @Column(length = 500)
    private String introduction;

    @Column(nullable = true)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Column(unique = true, columnDefinition = "TINYTEXT")
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean isProfileComplete = false;

    public void completeProfile(){
        this.isProfileComplete = true;
    }

    @Enumerated(EnumType.STRING)
    private Gender filterGender;

    private Integer filterMinAge;
    private Integer filterMaxAge;

    // 필터 조건 업데이트 메서드
    public void updateFilter(Gender gender, Integer minAge, Integer maxAge){
        this.filterGender = gender;
        this.filterMinAge = minAge;
        this.filterMaxAge = maxAge;
    }

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    private LocalDateTime deletedAt; // 탈퇴한 시간 저장

    public void deleteMember(){
        this.memberStatus = MemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileImage> memberProfileImageList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auth> authList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberIdol> memberIdolList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberEvent> memberEventList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applicationList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Landmine> landmineList = new ArrayList<>();

    //Review
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> sentReviewList = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviewList = new ArrayList<>();

    //mateRelationship
    @OneToMany(mappedBy = "firstMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MateRelationship> mateRelationshipinFirstList = new ArrayList<>();

    @OneToMany(mappedBy = "secondMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MateRelationship> mateRelationshipinSecondList = new ArrayList<>();

    @OneToMany(mappedBy = "otherMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomList = new ArrayList<>();

    //notificationSetting
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "notification_setting_id")
    private NotificationSetting notificationSetting;


    // 비밀번호 설정 함수
    public void encodePassword(String password){
        this.password=password;
    }

    // 복사 생성자
    public Member(Member other) {
        this.introduction = other.introduction;
    }

    // 회원의 만 나이를 계산하는 메서드
    public int calculateAge(){
        // 생년월일 가져오기
        LocalDate birth = this.birth;
        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 현재 연도와 태어난 연도의 차이를 계산
        int age = today.getYear() - birth.getYear();

        // 생일이 올해 아직 지나지 않았다면 나이를 1 줄임
        if (today.isBefore(birth.withYear(today.getYear()))) {
            age--;
        }
        return age;
    }

    // 자기소개 업데이트 메서드
    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    // 프로필 업데이트 메서드
    public void updateProfile(String nickname, String introduction) {
        if (nickname == null || nickname.isBlank()) {
            throw new MemberException(ErrorStatus.MEMBER_EMPTY_NICKNAME);
        }
        if (introduction == null || introduction.isBlank()) {
            throw new MemberException(ErrorStatus.MEMBER_EMPTY_INTRODUCTION);
        }
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public Member(Long id) {
        this.id = id;
    }
}
