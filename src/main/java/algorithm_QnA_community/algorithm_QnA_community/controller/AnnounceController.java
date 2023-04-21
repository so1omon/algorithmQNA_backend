package algorithm_QnA_community.algorithm_QnA_community.controller;

import algorithm_QnA_community.algorithm_QnA_community.dto.AnnounceRequestDto;
import algorithm_QnA_community.algorithm_QnA_community.dto.AnnounceResponseDto;
import algorithm_QnA_community.algorithm_QnA_community.service.AnnounceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AnnounceController {

    private final AnnounceService announceService;

    @PostMapping("/announce/create")
    public ResponseEntity<String> createAnnounce(AnnounceRequestDto requestDto){
        announceService.createAnnounce(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("공지사항이 저장되었습니다.");
    }

    @GetMapping("/announce/{announce-id}/detail")
    public ResponseEntity<AnnounceResponseDto> getAnnounceDetail(@PathVariable("announce-id")Long announceId){
        AnnounceResponseDto announce = announceService.getAnnounce(announceId);
        return ResponseEntity.status(HttpStatus.OK).body(announce);
    }
}
