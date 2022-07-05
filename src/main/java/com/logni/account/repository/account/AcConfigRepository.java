package com.logni.account.repository.account;

import com.logni.account.entities.common.AccountConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcConfigRepository extends JpaRepository<AccountConfig,String> {

}
