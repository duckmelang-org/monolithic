package umc.duckmelang.domain.member.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.application.domain.MateRelationship;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.notification.domain.NotificationSetting;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.global.common.BaseEntity;
import umc.duckmelang.global.common.serializer.LocalDateSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 30)
    private String nickname;

    @Column(length = 500)
    private String introduction;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "email")
    private String email;

    @Column(name = "loginId", unique = true)
    private String loginId;

    @Column(name = "phone_num", unique = true)
    private String phoneNum;

    @Column(nullable = true, length = 100)
    private String password; // 소셜 로그인은 null 가능

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isProfileComplete = false;

    private LocalDateTime deletedAt; // 탈퇴한 시간 저장

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private Gender filterGender;

    private Integer filterMinAge;
    private Integer filterMaxAge;

    // === 연관관계 메서드 === //
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberProfileImage> memberProfileImageList = new ArrayList<>();

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

    //Report
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> sentReportList = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> receivedReportList = new ArrayList<>();

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

    // === 도메인 메서드 === //
    public void updateProfile(String nickname, LocalDate birth, Gender gender){
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
    }

    public void deleteMember(){
        this.memberStatus = MemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void completeProfile(){
        this.isProfileComplete = true;
    }

    public void updateFilter(Gender gender, Integer minAge, Integer maxAge){
        this.filterGender = gender;
        this.filterMinAge = minAge;
        this.filterMaxAge = maxAge;
    }

    public void updatePhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void switchRole(Role role){
        this.role = role;
    }

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

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateProfile(String nickname, String introduction) {
        this.nickname = nickname;
        this.introduction = introduction;
    }
    public Member(Long id) {
        this.id = id;
    }
}
