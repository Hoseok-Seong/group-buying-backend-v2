package shop.donutmarket.donut.domain.main.controller;

import java.util.List;

import org.checkerframework.checker.units.qual.m;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.main.dto.MainResp;
import shop.donutmarket.donut.domain.main.service.MainService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/")
    public String lb() {
        return "ok";
    }

    @GetMapping("/main")
    public ResponseEntity<?> main(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        MainResp mainResp = mainService.게시글목록보기(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(mainResp);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/main/{categoryId}")
    public ResponseEntity<?> mainList(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long categoryId) {
        List<Board> boardList = mainService.게시글필터(myUserDetails, categoryId); 
        ResponseDTO<?> responseDTO = new ResponseDTO<>(boardList);
        return ResponseEntity.ok(responseDTO);
    }
}
