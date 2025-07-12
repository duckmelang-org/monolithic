package umc.duckmelang.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminQueryServiceImpl implements AdminQueryService {
    private final MemberRepository memberRepository;

    @Override
    public List<Member> getAdmin(){
        return memberRepository.findByRole(Role.ADMIN);

    }

}
