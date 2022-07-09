package com.logni.account.service.account;

import java.time.Instant;

public interface EodService {
    void calculateCloseBalanceForMemberLedger(Instant closeTime);
    void closeBalanceCalculation(Instant closeTime);
    void calculateAllCloseBalance(Instant closeTime);

}

