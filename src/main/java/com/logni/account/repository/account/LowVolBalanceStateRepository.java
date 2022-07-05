package com.logni.account.repository.account;

import com.logni.account.entities.accounts.AcBalanceState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LowVolBalanceStateRepository extends JpaRepository<AcBalanceState,Long> {

}
