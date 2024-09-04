package org.example.gggauthorization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserLoginRequest (
        @NotEmpty(message = "사용자 이름은 필수 입력 값입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "알파벳과 숫자만 허용, 최소 1자 최대 20자")
        String username,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&+=]).{8,20}$", message = "최소 하나의 대문자, 소문자, 숫자, 특수문자(!@#$%^&+=)를 포함, 최소 8자 최대 20자")
        String password
) {

}