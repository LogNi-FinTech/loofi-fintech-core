package com.logni.account.dto.rest.transaction;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseReverseRequest {
   @NotBlank
   String txnId;
   private String note;
   private String channel;
   private JsonNode data;
}
