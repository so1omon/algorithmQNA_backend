package algorithm_QnA_community.algorithm_QnA_community.domain.dto;

import lombok.Data;

@Data
public class CodeAndState {
    private String code;
    private String state;

    public CodeAndState(String code, String state) {
        this.code = code;
        this.state = state;
    }
}
