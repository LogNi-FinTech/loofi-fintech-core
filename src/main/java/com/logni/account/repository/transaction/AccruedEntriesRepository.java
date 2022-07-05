package com.logni.account.repository.transaction;

import com.logni.account.entities.transactions.AccruedAcEntries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccruedEntriesRepository extends JpaRepository<AccruedAcEntries,Long> {
}
