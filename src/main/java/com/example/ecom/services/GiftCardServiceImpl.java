package com.example.ecom.services;

import com.example.ecom.exceptions.GiftCardDoesntExistException;
import com.example.ecom.exceptions.GiftCardExpiredException;
import com.example.ecom.models.GiftCard;
import com.example.ecom.models.LedgerEntry;
import com.example.ecom.models.TransactionType;
import com.example.ecom.repositories.GiftCardRepository;
import com.example.ecom.repositories.LedgerEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GiftCardServiceImpl implements GiftCardService{
    private GiftCardRepository giftCardRepository;
    private LedgerEntryRepository ledgerEntryRepository;
    @Autowired
    public GiftCardServiceImpl(GiftCardRepository giftCardRepository,
                               LedgerEntryRepository ledgerEntryRepository){
        this.giftCardRepository=giftCardRepository;
        this.ledgerEntryRepository=ledgerEntryRepository;
    }
    @Override
    public GiftCard createGiftCard(double amount) {

        GiftCard giftCard = new GiftCard();
        giftCard.setAmount(amount);

        //Get Current Date
        Calendar calender= Calendar.getInstance();
        Date currentDate = calender.getTime();

        //Set Created Date
        giftCard.setCreatedAt(currentDate);
        // Add 365 days to the current date
        calender.add(Calendar.DAY_OF_YEAR,365);
        //Set the expiry Date to current date + 365 days
        giftCard.setExpiresAt(calender.getTime());

        //Create Random 10 byte Alpanumeric string using UUID
        String uuid = UUID.randomUUID().toString().replace("-","");
        String randomString = uuid.substring(0,10);
        giftCard.setGiftCardCode(randomString);

        //Create Credit Ledger entry with the amount
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setCreatedAt(currentDate);
        ledgerEntry.setAmount(amount);
        ledgerEntry.setTransactionType(TransactionType.CREDIT);
        ledgerEntryRepository.save(ledgerEntry);

        //Add the ledger entry to the List of Ledger Entries
        List<LedgerEntry> ledgerEntries = new ArrayList<>();
        ledgerEntries.add(ledgerEntry);
        giftCard.setLedger(ledgerEntries);

        giftCardRepository.save(giftCard);
        return giftCard;

    }

    @Override
    @Transactional
    public GiftCard redeemGiftCard(int giftCardId, double amountToRedeem) throws GiftCardDoesntExistException, GiftCardExpiredException {
        Optional<GiftCard> giftCardOptional = giftCardRepository.findById(giftCardId);

        //Check if the Gift Card is Empty
        if(giftCardOptional.isEmpty()){
            throw new GiftCardDoesntExistException("The gift Card with ID " + giftCardId + " is not valid");
        }
        GiftCard giftCard=giftCardOptional.get();
        Date expiryDate = giftCard.getExpiresAt();
        Date currentDate = Calendar.getInstance().getTime();

        //Check if the Gift Card has expired
        if(currentDate.compareTo(expiryDate) > 0){
            throw new GiftCardExpiredException("The Gift Card has expired");
        }

        double totalAmount = giftCard.getAmount();
        amountToRedeem = Math.min(amountToRedeem, totalAmount);
        double remainingAmount = giftCard.getAmount() - amountToRedeem;

        //Add the entries for Debit in the Ledger
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setTransactionType(TransactionType.DEBIT);
        ledgerEntry.setAmount(amountToRedeem);
        ledgerEntry.setCreatedAt(currentDate);
        ledgerEntryRepository.save(ledgerEntry);

        //Update the Giftcard's Amount remaining and list of Ledger
        giftCard.getLedger().add(ledgerEntry);
        giftCard.setAmount(remainingAmount);
        return giftCardRepository.save(giftCard);

    }
}
