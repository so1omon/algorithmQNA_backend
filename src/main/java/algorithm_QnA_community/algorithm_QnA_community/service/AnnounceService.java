package algorithm_QnA_community.algorithm_QnA_community.service;

import algorithm_QnA_community.algorithm_QnA_community.domain.Announce;
import algorithm_QnA_community.algorithm_QnA_community.dto.AnnounceRequestDto;
import algorithm_QnA_community.algorithm_QnA_community.dto.AnnounceResponseDto;
import algorithm_QnA_community.algorithm_QnA_community.repository.AnnounceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@RequiredArgsConstructor
@Service
public class AnnounceService {

    private final AnnounceRepository announceRepository;

    @Transactional
    public void createAnnounce(AnnounceRequestDto announceRequestDto){
        announceRepository.save(announceRequestDto.toEntity());
    }

    // 공지사항 글 번호로 조회
    @Transactional(readOnly = true)
    public AnnounceResponseDto getAnnounce(Long announceId){
//        Announce announce = announceRepository.findByAnnounceId(announceId);
//        if(announce == null){
//            throw new IllegalStateException("해당 공지사항이 존재하지 않습니다.");
//        }
        Announce announce = announceRepository.findById(announceId)
                .orElseThrow(()->new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        return new AnnounceResponseDto(announce);
    }
}
