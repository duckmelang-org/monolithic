package umc.duckmelang.domain.member.service.member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.repository.EventCategoryRepository;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.repository.IdolCategoryRepository;
import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.domain.landmine.repository.LandmineRepository;
import umc.duckmelang.domain.member.converter.MemberConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberRequestDto;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.memberevent.domain.MemberEvent;
import umc.duckmelang.domain.memberevent.repository.MemberEventRepository;
import umc.duckmelang.domain.memberidol.domain.MemberIdol;
import umc.duckmelang.domain.memberidol.repository.MemberIdolRepository;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.notificationsetting.repository.NotificationSettingRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.EventCategoryException;
import umc.duckmelang.global.apipayload.exception.IdolCategoryException;
import umc.duckmelang.global.apipayload.exception.LandmineException;
import umc.duckmelang.global.apipayload.exception.MemberException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdolCategoryRepository idolCategoryRepository;
    private final MemberIdolRepository memberIdolRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final MemberEventRepository memberEventRepository;
    private final LandmineRepository landmineRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public Member signupMember(MemberSignUpDto.SignupDto request){
        if(memberRepository.existsByEmail(request.getEmail())){
            throw new MemberException(ErrorStatus.DUPLICATE_EMAIL);
        }
        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));
        newMember = memberRepository.save(newMember);

        // 알림 설정 자동 추가
        NotificationSetting notificationSetting = NotificationSetting.builder()
                .chatNotificationEnabled(true)  // 기본값을 true로 설정
                .requestNotificationEnabled(true)
                .reviewNotificationEnabled(true)
                .bookmarkNotificationEnabled(true)
                .build();

        notificationSettingRepository.save(notificationSetting);
        newMember.setNotificationSetting(notificationSetting);

        return newMember;
    }

    @Override
    @Transactional
    public Member registerProfile(Long memberId, MemberRequestDto.ProfileRequestDto request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        if(memberRepository.existsByNickname(request.getNickname())){
            throw new MemberException(ErrorStatus.DUPLICATE_NICKNAME);
        }

        member.setNickname(request.getNickname());
        member.setBirth(request.getBirth());
        member.setGender(request.getGender());

        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public List<MemberIdol> selectIdols(Long memberId, MemberRequestDto.SelectIdolsDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        // 아이돌 카테고리 조회 및 유효성 검증
        List<IdolCategory> idolCategoryList = idolCategoryRepository.findAllById(request.getIdolCategoryIds());
        if (idolCategoryList.size() != request.getIdolCategoryIds().size()) {
            throw new IdolCategoryException(ErrorStatus.INVALID_IDOL_CATEGORY);
        }

        // 기존 데이터 존재 시 삭제
        memberIdolRepository.deleteAllByMember(member);

        // 새 데이터 저장
        List<MemberIdol> memberIdolList = idolCategoryList.stream()
                .map(idolCategory -> MemberConverter.toMemberIdol(member, idolCategory))
                .toList();

        return memberIdolRepository.saveAll(memberIdolList);
    }

    @Override
    @Transactional
    public List<MemberEvent> selectEvents(Long memberId, MemberRequestDto.SelectEventsDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        // 선호하는 행사를 하나도 고르지 않은 경우, 아래 로직을 진행하지 않고 바로 빈 리스트를 return
        if (request.getEventCategoryIds() == null || request.getEventCategoryIds().isEmpty()) {
            return Collections.emptyList();
        }

        //행사 카테고리 조회 및 유효성 검증
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllById(request.getEventCategoryIds());
        if (eventCategoryList.size() != request.getEventCategoryIds().size()) {
            throw new EventCategoryException(ErrorStatus.INVALID_EVENT_CATEGORY);
        }

        // 기존 데이터 존재 시 삭제
        memberEventRepository.deleteAllByMember(member);

        // 새 데이터 저장
        List<MemberEvent> memberEventList = eventCategoryList.stream()
                .map(eventCategory -> MemberConverter.toMemberEvent(member, eventCategory))
                .toList();

        return memberEventRepository.saveAll(memberEventList);
    }

    @Override
    @Transactional
    public List<Landmine> createLandmines(Long memberId, MemberRequestDto.CreateLandminesDto request) {
        // 회원 조회 및 유효성 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        // 지뢰를 하나도 설정하지 않은 경우, 아래 로직을 진행하지 않고 바로 빈 리스트를 return
        if (request.getLandmineContents() == null || request.getLandmineContents().isEmpty()) {
            return Collections.emptyList();
        }

        // 지뢰 내용을 가져온다
        List<String> landmineContents = request.getLandmineContents();

        // 지뢰 내용을 검증하여 중복된 키워드가 있는지 체크하고, 있다면 에러 발생
        Set<String> uniqueContents = new HashSet<>();
        for (String content : landmineContents) {
            if (!uniqueContents.add(content)) {
                throw new LandmineException(ErrorStatus.DUPLICATE_LANDMINE);
            }
        }

        // 기존 데이터 존재 시 삭제
        landmineRepository.deleteAllByMember(member);

        // 새 데이터 저장
        List<Landmine> landmineList = request.getLandmineContents().stream()
                .map(content -> MemberConverter.toLandmine(member, content))
                .collect(Collectors.toList());

        return landmineRepository.saveAll(landmineList);
    }

    @Override
    @Transactional
    public Member createIntroduction(Long memberId, MemberRequestDto.CreateIntroductionDto request) {
        // 회원 조회 및 유효성 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        // 자기소개 문구 유효성검증
        if (request.getIntroduction().trim().isEmpty()) {
            throw new MemberException(ErrorStatus.MEMBER_EMPTY_INTRODUCTION);
        }

        // 자기소개 업데이트
        Member updatedMember = MemberConverter.toMemberWithIntroduction(member, request.getIntroduction());

        // 자기소개 업데이트
        updatedMember.completeProfile();

        return memberRepository.save(updatedMember);
    }

    @Override
    public boolean isNicknameExists(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
}
