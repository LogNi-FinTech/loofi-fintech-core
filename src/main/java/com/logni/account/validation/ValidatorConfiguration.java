package com.logni.account.validation;

import com.logni.account.validation.transaction.TxnAcTypeValidator;
import com.logni.account.validation.transaction.TxnTypeAndAccountValidation;
import com.logni.account.validation.transaction.TxnValidator;
import com.logni.account.validation.transaction.Validation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ValidatorConfiguration {

    @Bean
    TxnTypeAndAccountValidation createTypeAndAcValidation(){
        return new TxnTypeAndAccountValidation();
    }

    @Bean
    TxnValidator txnValidator(){
        List<Validation> txnRequestValidators = new ArrayList<>();
        txnRequestValidators.add(createTypeAndAcValidation());
        return new TxnValidator(txnRequestValidators);
    }
    @Bean
    TxnAcTypeValidator txnAcTypeValidator(){
        List<Validation> txnTypeAcValidators = new ArrayList<>();
        txnTypeAcValidators.add(createTypeAndAcValidation());
        return new TxnAcTypeValidator(txnTypeAcValidators);
    }
}
