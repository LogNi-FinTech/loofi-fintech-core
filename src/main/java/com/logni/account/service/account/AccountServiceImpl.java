package com.logni.account.service.account;

import com.logni.account.config.UserData;
import com.logni.account.dto.rest.CustomerAcDTO;
import com.logni.account.dto.rest.account.AcActivationDto;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AcCloseDto;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.AccountLock;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.MemberLedgerBalanceState;
import com.logni.account.entities.common.AccountConfig;
import com.logni.account.entities.transactions.LedgerAcEntries;
import com.logni.account.entities.transactions.MemberAcEntries;
import com.logni.account.enums.AccountState;
import com.logni.account.enums.LedgerType;
import com.logni.account.exception.CommonException;
import com.logni.account.exception.account.AccountCreationExp;
import com.logni.account.repository.account.*;
import com.logni.account.repository.transaction.LedgerAcEntriesRepository;
import com.logni.account.repository.transaction.MemberAcEntriesRepository;
import com.logni.account.utils.AccountErrors;
import com.logni.account.utils.AccountUtil;
import com.logni.account.utils.Constants;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class AccountServiceImpl implements AccountService{
    @Autowired
    public LedgerRepository ledgerRepository;
    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public AcLockRepository acLockRepository;

    @Autowired
    public MemberAcEntriesRepository memberAcEntriesRepository;

    @Autowired
    public LedgerAcEntriesRepository ledgerAcEntriesRepository;

    @Autowired
    public AcConfigRepository acConfigRepository;

    @Autowired
    public MemberLedgerBalanceStateRepo memberLedgerBalanceStateRepo;

    @Resource(name = "requestScopeTokenData")
    private UserData userData;

    @Transactional
    public void createMemberAccount(AccountDto accountDto){
        Account account = new Account(accountDto.getIdentifier(),accountDto.getName(),accountDto.getCustomerId());
         Optional<Ledger> ledgerOptional = ledgerRepository.findById(accountDto.getLedgerId());
         if(ledgerOptional.isPresent()){
             Ledger ledger = ledgerOptional.get();
             if(ledger.getType()!=LedgerType.MEMBER){
                 throw new AccountCreationExp("InValid Ledger");
             }else {
                 account.setLedger(ledger);
             }
         }else {
             handleLedgerNotFound();
         }
        account.setCreatedDate(Instant.now());
        if(accountDto.getState()!=null)
            account.setState(accountDto.getState());
        else
            account.setState(AccountState.ACTIVE);

        account.setCreatedBy(userData.getUserId()!=null?userData.getUserId():"REST");
        setOptionalField(accountDto,account);

        Account accountDb = accountRepository.save(account);
        AccountLock accountLock = new AccountLock();
        accountLock.setAccountId(accountDb.getId());
        acLockRepository.save(accountLock);

    }

    public void createCustomerAccount(CustomerAcDTO customerAcDTO){
        //todo do it cache
        Optional<AccountConfig> accountConfigOp = acConfigRepository.findById(Constants.CUSTOMER_LEDGER_ID);
        if(!accountConfigOp.isPresent()){
            throw new AccountCreationExp("NO MAPPING FOR CUSTOMER LEDGER");
        }

        AccountDto accountDto = new AccountDto();
        accountDto.setLedgerId(Long.parseLong(accountConfigOp.get().getValue()));
        accountDto.setIdentifier(customerAcDTO.getIdentifier());
        accountDto.setCustomerId(customerAcDTO.getCustomerId());
        accountDto.setState(customerAcDTO.getState());
        accountDto.setName(customerAcDTO.getName());
        createMemberAccount( accountDto);

    }

    void handleLedgerNotFound(){
        throw new AccountCreationExp("Ledger Not Found");
    }

    private void setOptionalField(AccountDto accountDto,Account account){
        account.setAccountTags(accountDto.getTagList());
        account.setType(accountDto.getType());
        account.setAlternativeAccountNumber(accountDto.getAlternativeAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setLowerLimit(BigDecimal.ZERO);
    }


    public Account getAccountByIdentifier(String identifier){
       return accountRepository.findByIdentifier(identifier);
    }


    @Transactional(readOnly = true)
    public AccountDto getAccountDetails(String identifier){
        if(StringUtils.isEmpty(identifier)){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.INVALID_ACCOUNT),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_ACCOUNT));
        }
        Account account = accountRepository.findByIdentifier(identifier);
        accountNotFoundCheck(account);
        return adaptAccount(account);

    }

    @Transactional
    public void activateAccount(AcActivationDto activationDto){
        Account account = accountRepository.findByIdentifier(activationDto.getIdentifier());
        accountNotFoundCheck(account);
        if(account.getState()==AccountState.PENDING){
            account.setState(AccountState.ACTIVE);
            account.setLastModifiedDate(Instant.now());
            account.setLastModifiedBy("TODO-MAKER");
            account.setActivationDate(Instant.now());
            //todo history of activation
        }else {
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.INVALID_ACCOUNT),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_ACCOUNT));
        }
        accountRepository.save(account);
    }
    public void closeAccount(AcCloseDto acCloseDto){
        Account account = accountRepository.findByIdentifier(acCloseDto.getIdentifier());
        accountNotFoundCheck(account);
        //todo implement all close business logic
        if(account.getState()!=AccountState.CLOSE){
            account.setState(AccountState.CLOSE);
            account.setLastModifiedDate(Instant.now());
            account.setLastModifiedBy("TODO-MAKER");

            // todo history of close
        }else {
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.INVALID_ACCOUNT),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_ACCOUNT));

        }

    }

    public AccountDto updateAccount(AccountDto accountDto){
        Account account = accountRepository.findByIdentifier(accountDto.getIdentifier());
        accountNotFoundCheck(account);
        //todo implement all close business logic

            account.setName(accountDto.getName());
            account.setState(accountDto.getState());
            account.setLastModifiedDate(Instant.now());
            account.setLastModifiedBy("TODO-MAKER");
            // todo history of close

        return accountDto;
    }

    private AccountDto adaptAccount(Account account){
        Optional<AccountDto> accountDtoOptional = AccountUtil.map(account,AccountDto.class);
        AccountDto accountDto = accountDtoOptional.get();
        return accountDto;
    }
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountByCustomerId(String customerId){

        List<Account> accountList = accountRepository.findAllByCustomerId(customerId);
        accountNotFoundCheck(accountList);
        return accountList.stream()
                .map(ac-> AccountUtil.map(ac, AccountDto.class).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
    @Transactional(readOnly = true)
    public AcBalance getAccountBalance(String identifier){
        Account account = accountRepository.findByIdentifier(identifier);
        accountNotFoundCheck(account);
        checkMemberAc(account);
        BigDecimal balance = account.getBalance();
        return new AcBalance(account.getIdentifier(),balance);
    }

    @Transactional(readOnly = true)
    public  Page<StmtTxn> accountStatement(String identifier, Pageable pageable){
        Account account = accountRepository.findByIdentifier(identifier);
        accountNotFoundCheck(account);
        checkMemberAc(account);
        Page<MemberAcEntries> memberAcEntriesPage = memberAcEntriesRepository.findAllByAccount(account,pageable);
        if(memberAcEntriesPage == null){
            return new PageImpl<>(new ArrayList<>(),pageable,0);
        }
        else {
            List<StmtTxn> stmtTxnList = memberAcEntriesPage.get()
                    .map(this::adaptStatement)
                    .collect(Collectors.toList());
            return new PageImpl<>(stmtTxnList,pageable,memberAcEntriesPage.getTotalElements());
        }

    }

    private void accountNotFoundCheck(Object account){
        if(account==null){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.ACCOUNT_NOT_FOUND),
                    AccountErrors.ERROR_MAP.get(AccountErrors.ACCOUNT_NOT_FOUND));
        }
    }

    private void checkMemberAc(Account account){
        if(account.getLedger().getType()!=LedgerType.MEMBER){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.INVALID_AC_TYPE),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_AC_TYPE));
        }
    }

    private void checkSystemAc(Account account){
        if(account.getLedger().getType()!=LedgerType.SYSTEM){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.INVALID_AC_TYPE),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_AC_TYPE));
        }
    }
    public StmtTxn adaptStatement(Object e){
        StmtTxn stmtTxn = null;
        if(e instanceof MemberAcEntries){
            stmtTxn = new StmtTxn();
            MemberAcEntries me = (MemberAcEntries)e;
            stmtTxn.setTxnId(me.getTransaction().getId());
            stmtTxn.setAmount(me.getAmount());
            stmtTxn.setDescription(me.getTransaction().getDescription());
            stmtTxn.setNote(me.getTransaction().getNote());
            stmtTxn.setChannel(me.getTransaction().getChannel());
            stmtTxn.setAccount(me.getAccount().getIdentifier());
            stmtTxn.setRemoteAccount(me.getRemoteAccount().getIdentifier());
            stmtTxn.setTxnType(me.getTxnType().getName());
            stmtTxn.setData(me.getTransaction().getData()!=null?me.getTransaction().getData().toString():null);
            stmtTxn.setTxnTime(me.getTxnTime());

        }else if( e instanceof LedgerAcEntries){
            LedgerAcEntries le = (LedgerAcEntries)e;
            stmtTxn = new StmtTxn();
            stmtTxn.setTxnId(le.getTransaction().getId());
            stmtTxn.setAmount(le.getAmount());
            stmtTxn.setDescription(le.getTransaction().getDescription());
            stmtTxn.setNote(le.getTransaction().getNote());
            stmtTxn.setChannel(le.getTransaction().getChannel());
            stmtTxn.setAccount(le.getAccount().getIdentifier());
            stmtTxn.setRemoteAccount(le.getRemoteAccount().getIdentifier());
            stmtTxn.setTxnType(le.getTxnType().getName());
            stmtTxn.setData(le.getTransaction().getData()!=null?le.getTransaction().getData().toString():null);
            stmtTxn.setTxnTime(le.getTxnTime());
        }

         return stmtTxn;
    }


    @Transactional(readOnly = true)
    public AcBalance getLedgerBalance(String ledgerCode){
        Account account = accountRepository.findByIdentifier(ledgerCode);
        accountNotFoundCheck(account);
        checkSystemAc(account);
        BigDecimal balance =  account.getBalance();
        return new AcBalance(account.getIdentifier(),balance);
    }


    @Transactional(readOnly = true)
    public BigDecimal getLedgerBalance(Ledger ledger,Instant time){
        if(time==null)
            time = Instant.now();

        //todo balance event time
        // from account balance state
        return BigDecimal.ZERO;

    }


    @Transactional(readOnly = true)
    public BigDecimal getMemberLedgerBalance(Ledger ledger,Instant time){

        if(ledger.getType()!=LedgerType.MEMBER){
            throw new CommonException(AccountErrors.INVALID_AC_TYPE,"LEDGER_TYPE_IS_NOT_MEMBER");
        }
        // todo let's guess individual member can't be LOW high volume if ledger not.
        // last state + memberEntries sum
        if(time==null)
            time = Instant.now();

        BigDecimal balance = BigDecimal.ZERO;
        MemberLedgerBalanceState memberLedgerBalanceState= memberLedgerBalanceStateRepo.findTopByLedgerAndBalanceAtBeforeOrderByIdDesc(ledger,time);
        if(memberLedgerBalanceState==null){
            balance = memberAcEntriesRepository.getBalanceSumUptoTime(ledger,time);
        }else {
            BigDecimal balanceDiff = memberAcEntriesRepository.getBalanceSumFromToUptoTime(ledger,memberLedgerBalanceState.getBalanceAt(),time);
            if(balanceDiff==null) balanceDiff = BigDecimal.ZERO;
            balance = memberLedgerBalanceState.getBalance().add(balanceDiff);
        }

        if(balance==null) balance = BigDecimal.ZERO;

        return balance;
    }


    @Transactional(readOnly = true)
    public Page<StmtTxn> ledgerStatement(String ledgerCode, Pageable pageable){
        Account account = accountRepository.findByIdentifier(ledgerCode);
        accountNotFoundCheck(account);
        checkSystemAc(account);

        Page<LedgerAcEntries> ledgerAcEntries = ledgerAcEntriesRepository.findAllByAccount(account,pageable);
        if(ledgerAcEntries == null){
            return new PageImpl<>(new ArrayList<>(),pageable,0);
        }
        else {
            List<StmtTxn> stmtTxnList = ledgerAcEntries.get()
                    .map(this::adaptStatement)
                    .collect(Collectors.toList());
            return new PageImpl<>(stmtTxnList,pageable,ledgerAcEntries.getTotalElements());
        }
    }


}
