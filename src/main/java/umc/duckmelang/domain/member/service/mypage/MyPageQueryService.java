package umc.duckmelang.domain.member.service.mypage;

import umc.duckmelang.domain.member.dto.member.MemberFilterDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;

public interface MyPageQueryService {
    MyPageResponseDto.MyPageProfileEditBeforeDto getMemberProfileBeforeEdit(Long memberId);
    MemberFilterDto.FilterResponseDto getMemberFilter(Long memberId);
}
