package shop.donutmarket.donut.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.user.model.User;

public class UserReq {
    @Getter
    @Setter
    public static class JoinDTO {
        @Email(message = "이메일 형식으로 적어주세요")
        @NotBlank(message = "이메일을 적어주세요")
        private String username;
        @NotBlank(message = "비밀번호를 적어주세요")
        private String password;
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;

        public User toEntity(Rate rate) {
            return User.builder().username(username).email(username).password(password)
                    .nickname(nickname).rate(rate).build();
        }
    }

    @Getter
    @Setter
    public static class LoginDTO {
        @Email(message = "이메일 형식으로 적어주세요")
        private String username;
        @NotBlank(message = "비밀번호를 적어주세요")
        private String password;
    }

    @Getter
    @Setter
    public static class UpdateDTO {
        private String password;

        public User toEntity() {return User.builder().password(password).build();}
    }
}
