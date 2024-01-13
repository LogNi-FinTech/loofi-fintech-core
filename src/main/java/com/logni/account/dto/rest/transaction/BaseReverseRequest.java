package com.logni.account.dto.rest.transaction;


import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
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
