package com.logni.account.controller;

import com.logni.account.entities.common.AccountConfig;
import com.logni.account.repository.account.AcConfigRepository;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/account/config")
public class AccountConfigController {

    @Autowired
    AcConfigRepository acConfigRepository;


    @Operation(summary = "Create Account Config", description = "", tags={ "account" })
    @PostMapping
    public ResponseEntity createAccountConfig(@RequestBody @Valid AccountConfig accountConfig){
        acConfigRepository.save(accountConfig);
        return ResponseEntity.ok().build();

    }
    @Operation(summary = "Update Account Config", description = "", tags={ "account" })
    @PutMapping
    public ResponseEntity updateAccountConfig(@RequestBody @Valid AccountConfig accountConfig){
        acConfigRepository.save(accountConfig);
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "Get Account Config", description = "", tags={ "account" })
    @GetMapping
    public ResponseEntity<Page<AccountConfig>> getAcountConfig(Pageable pageable){
        return ResponseEntity.ok().body(acConfigRepository.findAll(pageable));

    }


}
