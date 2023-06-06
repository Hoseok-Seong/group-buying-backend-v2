package shop.donutmarket.donut.domain.user.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import shop.donutmarket.donut.domain.user.dto.UserReq;
import shop.donutmarket.donut.domain.user.dto.UserResp;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.domain.user.service.UserService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;
import shop.donutmarket.donut.global.dto.ResponseDTO;
import shop.donutmarket.donut.global.jwt.MyJwtProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @Value("${meta.name}")
    private String name;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserReq.JoinDTO joinDTO, BindingResult bindingResult) {
        ResponseEntity responseEntity = userService.회원가입(joinDTO);
        return responseEntity;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserReq.LoginDTO loginDTO, BindingResult bindingResult) {
        ResponseEntity responseEntity = userService.로그인(loginDTO);
        return responseEntity;
    }

    @PutMapping("/users/update")
    public ResponseEntity<?> update(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails,
                                    @RequestPart(value = "updateDTO") @Valid UserReq.UpdateDTO updateDTO,
                                    BindingResult bindingResult) {
        UserResp.UpdateDTO resp = userService.회원수정(multipartFile, myUserDetails, updateDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(resp);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/jwtToken")
    public ResponseEntity<?> jwtToken(HttpServletRequest request) {
        UserResp.JwtUserDTO resp = userService.JWT확인(request);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(resp);
        return ResponseEntity.ok(responseDTO);
    }
}
