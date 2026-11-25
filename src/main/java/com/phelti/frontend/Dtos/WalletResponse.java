package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Integer walletId;

    private Integer userId;

    private BigDecimal availableBalance;

    private BigDecimal reservedBalance;
}
