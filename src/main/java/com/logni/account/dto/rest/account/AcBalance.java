package com.logni.account.dto.rest.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcBalance {
    String identifier;
    BigDecimal balance;
    BigDecimal availableBalance;
}
