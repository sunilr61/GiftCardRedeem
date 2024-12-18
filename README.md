# Implement place order functionality for an e-commerce platform

## Problem Statement

You are building an e-commerce platform. As a part of this system, you need to expose a functionality Gift Card, just like Vantage points.

## Solution
1. Create Gift Card functionality
   * For creating the gift card we need to take the following inputs:
     * User id for whom the gift card is being created.
     * Amount for which the gift card is being created.
     * Expiry date of the gift card.
     * We will keep a Ledger of entries done on the Gift card. Like Creation of giftcard is Credit and Redemption of gift card is Debit.
   
2. Redeem Gift Card functionality
   * Check if the Gift Card exists in the system, if not then we need to throw an exception.
   * Check if the Gift Card has Expired, if not then we need to throw an exception.
   * Check if the Redeem amount is valid and not greater than the available balance in the Gift Card.
   * If Redeem amount greater than the available balance redeem the entire balance.
   * Create a ledger entry in the GiftCard.


