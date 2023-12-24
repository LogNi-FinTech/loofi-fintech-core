package com.logni.account.controller;

import com.logni.account.dto.rest.CustomerAcDTO;
import com.logni.account.dto.rest.account.AcActivationDto;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AcCloseDto;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.service.account.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @Operation(summary = "Create Account", description = "", tags = {"account"})
  @PostMapping("/v1/account")
  public ResponseEntity createAccount(@RequestBody @Valid AccountDto accountDto) {
    log.debug("Account creation.Name:{},Identifier:{}", accountDto.getName(), accountDto.getIdentifier());
    accountService.createMemberAccount(accountDto);
    return ResponseEntity.ok().build();

  }

  @Operation(summary = "Create Customer Account", description = "", tags = {"account"})
  @PostMapping("/v1/account/customer")
  public ResponseEntity createCustomerAccount(@RequestBody @Valid CustomerAcDTO customerAcDTO) {
    log.debug("Customer Account creation.Name:{},Identifier:{}", customerAcDTO.getName(), customerAcDTO.getIdentifier());
    accountService.createCustomerAccount(customerAcDTO);
    return ResponseEntity.ok().build();

  }

  @Operation(summary = "Account Detail.", description = "", tags = {"account"})
  @GetMapping("/v1/account/{identifier}")
  public ResponseEntity<AccountDto> getAccountDetails(@PathVariable("identifier") String identifier) {
    log.debug("Account detail.Identifier:{}", identifier);
    return ResponseEntity.ok(accountService.getAccountDetails(identifier));
  }

  @Operation(summary = "Account List By Customer.", description = "", tags = {"account"})
  @GetMapping("/v1/account/customer/{customerId}")
  public ResponseEntity<List<AccountDto>> getAccountsByCustomerId(@PathVariable("customerId") String customerId) {
    return ResponseEntity.ok(accountService.getAccountByCustomerId(customerId));
  }

  @Operation(summary = "Account Balance.", description = "", tags = {"account"})
  @GetMapping("/v1/account/balance/{identifier}")
  public ResponseEntity<AcBalance> getAccountBalance(@PathVariable("identifier") String identifier) {
    log.debug("Account Balance.Identifier:{}", identifier);
    long s = System.currentTimeMillis();
    AcBalance acBalance = accountService.getAccountBalance(identifier);
    log.info("AC Balance Response.Latency: {} ms", (System.currentTimeMillis() - s));
    return ResponseEntity.ok(acBalance);
  }

  @Operation(summary = "Account Statement.", description = "", tags = {"account"})
  @GetMapping("/v1/account/statement")
  public ResponseEntity<Page<StmtTxn>> getAccountStatement(@PathParam("account") String account,
                                                           Pageable pageable) {
    return ResponseEntity.ok(accountService.accountStatement(account, pageable));
  }

  @Operation(summary = "Activate Account .", description = "", tags = {"account"})
  @PutMapping("/v1/account/activate")
  public ResponseEntity<Void> activateAc(@RequestBody AcActivationDto activationDto) {
    accountService.activateAccount(activationDto);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Close Account .", description = "", tags = {"account"})
  @PutMapping("/v1/account/close")
  public ResponseEntity<Void> closeAccount(@RequestBody AcCloseDto acCloseDto) {
    accountService.closeAccount(acCloseDto);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Update Account .", description = "", tags = {"account"})
  @PutMapping("/v1/account/update")
  public ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto accountDto) {
    return ResponseEntity.ok(accountService.updateAccount(accountDto));
  }

}


