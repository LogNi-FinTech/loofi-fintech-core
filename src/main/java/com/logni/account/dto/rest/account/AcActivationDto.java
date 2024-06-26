package com.logni.account.dto.rest.account;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcActivationDto {
    @NotBlank
    String identifier;
    String note;

}
