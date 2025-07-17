package umc.duckmelang.domain.member.util;

import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;

public class NicknameUtil {

    private static final String DELETED_MEMBER_NICKNAME = "탈퇴한 회원";

    public static String getDisplayName(Member member){
        if (member.getMemberStatus() == MemberStatus.DELETED){
            return DELETED_MEMBER_NICKNAME;
        }
        return member.getNickname();
    }
}
