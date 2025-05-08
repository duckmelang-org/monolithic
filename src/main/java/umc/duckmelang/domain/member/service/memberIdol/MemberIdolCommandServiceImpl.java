package umc.duckmelang.domain.member.service.memberIdol;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.repository.IdolCategoryRepository;
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.repository.MemberIdolRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.IdolCategoryException;

@Service
@RequiredArgsConstructor
public class MemberIdolCommandServiceImpl implements MemberIdolCommandService {
    private final MemberIdolRepository memberIdolRepository;
    private final IdolCategoryRepository idolCategoryRepository;

    @Override
    @Transactional
    public void deleteMemberIdol(Long memberId, Long idolId){
        MemberIdol memberIdol = memberIdolRepository.findByMemberAndIdol(memberId, idolId)
                .orElseThrow(()-> new IdolCategoryException(ErrorStatus.IDOL_CATEGORY_NOT_FOUND));
        memberIdolRepository.delete(memberIdol);
    }

    @Override
    @Transactional
    public MemberIdol addMemberIdol(Long memberId, Long idolId) {
        IdolCategory idolCategory = idolCategoryRepository.findById(idolId)
                .orElseThrow(() -> new IdolCategoryException(ErrorStatus.IDOL_CATEGORY_NOT_FOUND));
        boolean alreadyExists = memberIdolRepository.existsByMemberIdAndIdolCategoryId(memberId, idolId);
        if(alreadyExists){
            throw new IdolCategoryException(ErrorStatus.ALREADY_ADDED_IDOL);
        }
        MemberIdol memberIdol = MemberIdolConverter.toMemberIdol(memberId, idolCategory);
        return memberIdolRepository.save(memberIdol);
    }
}
