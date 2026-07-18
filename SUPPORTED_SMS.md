# Supported SMS formats

The parser currently recognizes patterns similar to the anonymized examples below.

## Commercial Bank

```text
Dear Cardholder, Purchase at SAMPLE MERCHANT for LKR 1,250.00 on 18/07/26 02:21 PM has been authorised on your debit card ending #1234.
```

```text
Withdrawal at SAMPLE ATM for LKR 2,000.00 on 18/07/26 12:43 PM from card ending #1234.
```

```text
Credit for Rs. 5,000.00 to 0000000000 at 16:07 at DIGITAL BANKING DIVISION
```

## Sampath Bank

```text
LKR 1,250.00 debited from AC **1234 for SAMPLE TRANSACTION
18/07/2026 14:21:00
```

## Seylan Bank

```text
Seylan Card ...1234 debit Txn 1000000000 of LKR 1,250.00 done on 18/07/2026 02:21:00 PM at SAMPLE MERCHANT. Avl bal 10,000.00
```

```text
Your Account 0000****1234 was credited by LKR 5,000.00 CASH DEPOSIT - AUTOMATED on 18/07/2026 02:21:00 PM. Avl bal 15,000.00
```

Only anonymized test fixtures should be committed when adding support for a new format.
