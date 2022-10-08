package com.logni.account.service.account;

import com.logni.account.dto.rest.account.LedgerBalanceDto;
import com.logni.account.entities.accounts.Ledger;
import java.time.Instant;
import java.util.List;

public interface LedgerService {
    Ledger createLedger(Ledger ledger);

    List<LedgerBalanceDto> getAllLedgerBalance(Instant time);
    List<LedgerBalanceDto> getSystemLedgerBalance(Instant time);
    List<LedgerBalanceDto> getMemberLedgerBalance(Instant time);
}
