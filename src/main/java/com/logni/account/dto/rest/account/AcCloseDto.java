package com.logni.account.dto.rest.account;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcCloseDto {
    @NotBlank
    String identifier;
    @NotBlank
    String reason;

}
