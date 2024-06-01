package com.myproject.lms.shared.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class LoginRequest implements Request, Serializable {
    private String username;
    private String password;
    private Boolean isUserExit = false;
}
