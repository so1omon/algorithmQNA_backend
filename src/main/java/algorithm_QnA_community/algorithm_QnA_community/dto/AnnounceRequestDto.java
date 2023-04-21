package algorithm_QnA_community.algorithm_QnA_community.dto;

import algorithm_QnA_community.algorithm_QnA_community.domain.Announce;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class AnnounceRequestDto {
    // 관리자 정보 추가
    private String title;
    private String contents;


    public Announce toEntity(){
        return Announce.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}
