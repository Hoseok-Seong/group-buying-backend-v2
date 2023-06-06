package shop.donutmarket.donut.domain.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.domain.user.dto.UserReq;
import shop.donutmarket.donut.domain.user.dto.UserResp;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;
import shop.donutmarket.donut.global.jwt.MyJwtProvider;
import shop.donutmarket.donut.global.s3.S3Service;
import shop.donutmarket.donut.global.util.S3KeyGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RateRepository rateRepository;
    private final S3Service s3Service;

    @Transactional
    public ResponseEntity<?> 회원가입(UserReq.JoinDTO joinDTO) {
        // 회원가입 로직
        String rawPassword = joinDTO.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword); // 60Byte
        joinDTO.setPassword(encPassword);

        Optional<Rate> rateOP = rateRepository.findById(1L);
        if (rateOP.isEmpty()) {
            throw new Exception404("등급이 존재하지 않습니다");
        }

        try {
            Rate ratePS = rateOP.get();
            userRepository.save(joinDTO.toEntity(ratePS));
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }

        // JWT 인증 로직
        Optional<User> userOP = userRepository.findByUsername(joinDTO.getUsername());
        if (userOP.isPresent()) {
            User userPS = userOP.get(); // 조회하는 객체는 PS
            if (passwordEncoder.matches(rawPassword, userPS.getPassword())) {
                String jwt = MyJwtProvider.create(userPS);

                // Body 만들기
                UserResp.JoinDTO body = new UserResp.JoinDTO(userPS);
                ResponseDTO<?> responseDTO = new ResponseDTO<>(body);

                // ResponseEntity 생성
                return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
            }
            throw new RuntimeException("패스워드 유효성 실패");
        } else {
            throw new RuntimeException("이메일 유효성 실패");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> 로그인(UserReq.LoginDTO loginDTO) {
        Optional<User> userOP = userRepository.findByUsername(loginDTO.getUsername());
        if (userOP.isEmpty()) {
            throw new Exception404("존재하지 않는 회원입니다");
        }
        try {
            User userPS = userOP.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), userPS.getPassword())) {
                String jwt = MyJwtProvider.create(userPS);

                // Body 만들기
                UserResp.LoginDTO body = new UserResp.LoginDTO(userPS);
                ResponseDTO<?> responseDTO = new ResponseDTO<>(body);

                // ResponseEntity 생성
                return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
            }
            throw new RuntimeException("패스워드 유효성 실패");
        } catch (Exception e) {
            throw new Exception500("로그인 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public UserResp.UpdateDTO 회원수정(MultipartFile multipartFile,
                                   @AuthenticationPrincipal MyUserDetails myUserDetails,
                                   UserReq.UpdateDTO updateDTO) {
        Optional<User> userOP = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if (userOP.isEmpty()) {
            throw new Exception404("존재하지 않는 회원입니다");
        }
        User userPS = userOP.get();

        try {
            // 회원 수정
            LocalDateTime localDateTime = LocalDateTime.now();
            String imgKey = S3KeyGenerator.makeKey(multipartFile);
            s3Service.upload(multipartFile, imgKey);

            userPS.updateUser(updateDTO.getPassword(), imgKey, localDateTime);

        } catch (Exception e) {
            throw new Exception500("회원수정 실패 : " + e.getMessage());
        }

        // 다시 db에서 조회
        Optional<User> data = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if (data.isEmpty()) {
            throw new Exception404("존재하지 않는 회원입니다");
        }

        try {
            User user = data.get();
            UserResp.UpdateDTO resp = new UserResp.UpdateDTO(user.getUsername(), user.getEmail(), user.getProfile(),
                    user.getRole());
            return resp;
        } catch (Exception e) {
            throw new Exception500("회원수정 데이터 반환 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public UserResp.JwtUserDTO JWT확인(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");

        if (jwtToken == null) {
            throw new Exception404("토큰이 헤더에 없습니다");
        }

        System.out.println("토큰이 헤더에 있습니다");

        jwtToken = jwtToken.replace(MyJwtProvider.TOKEN_PREFIX, "");
        DecodedJWT decodeJwt = MyJwtProvider.verify(jwtToken);
        Long userId = decodeJwt.getClaim("id").asLong();

        User userEntity = userRepository.findByIdJoinFetch(userId).orElseThrow(() -> new Exception500("토큰 검증 실패"));
        UserResp.JwtUserDTO jwtUserDTO = new UserResp.JwtUserDTO(userEntity);
        return jwtUserDTO;
    }
}
