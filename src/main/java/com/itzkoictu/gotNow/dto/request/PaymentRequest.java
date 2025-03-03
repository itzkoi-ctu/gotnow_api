package com.itzkoictu.gotNow.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private int amount;
    private String currency;
}
