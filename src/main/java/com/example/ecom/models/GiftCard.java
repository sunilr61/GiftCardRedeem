package com.example.ecom.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class GiftCard extends BaseModel{
    private double amount;
    private Date createdAt;
    private Date expiresAt;
    @OneToMany
    private List<LedgerEntry> ledger;
    private String giftCardCode;
}