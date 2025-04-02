package umc.duckmelang.domain.member.service.memberIdol;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.repository.MemberIdolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberIdolQueryServiceImpl implements MemberIdolQueryService {
    private final MemberIdolRepository memberIdolRepository;

    @Override
    public List<MemberIdol> getIdolListByMember(Long memberId){
        return memberIdolRepository.findByMember(memberId);
    }
}
