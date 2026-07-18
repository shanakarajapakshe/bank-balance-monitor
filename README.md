# Bank Balance Monitor

A privacy-first Android money tracker that converts supported Sri Lankan bank SMS alerts into an organized, local financial dashboard.

The app automatically records card purchases, deposits, fees, ATM withdrawals, and supported transfers. Transactions remain editable, and no internet permission is included in this public build.

## Features

- Automatic transaction capture from supported bank SMS alerts
- Separate Commercial Card, Commercial Saving, Sampath, Seylan, and Cash accounts
- Manual cash and saving-account transactions
- Editable account, category, and transaction notes
- Custom spending categories
- Current-month dashboard with bank and month filters
- Monthly opening and closing balances with optional manual corrections
- ATM withdrawals mirrored into the Cash wallet
- Internal transfers excluded from income and expense totals
- Monthly and yearly spending analytics
- Fully local SQLite storage
- Adaptive launcher icon and phone-safe navigation layout

## Supported SMS senders

- `COMBANK`
- `SAMPATHTXN`
- `Seylan Bank`

The parser is designed around known alert formats. Banks may change their message templates without notice, so new formats may require parser updates.

## Privacy

This public version:

- processes SMS messages on the device;
- accepts only the supported bank sender names;
- stores parsed transactions in a local SQLite database;
- does not include internet or network-state permissions;
- does not upload SMS messages, account data, or transaction data;
- does not store OTP messages.

See [PRIVACY.md](PRIVACY.md) for details.

## Requirements

- Android Studio with JDK 17
- Android SDK 35
- Android 6.0 (API 23) or newer device

## Build

Clone the repository and run:

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`.

You can also open the repository root directly in Android Studio and run the `app` configuration on a connected device.

## First run

1. Install and open the app.
2. Allow the requested SMS permissions.
3. Tap the refresh button to import supported existing bank alerts.
4. Open **Accounts** and correct the current balances if required.
5. Review automatically assigned categories and edit any incorrect transaction.

## Internal transfers

Transactions classified as `Internal Transfer`, `ATM Transfer`, or `Cash Withdrawal` are excluded from income and expense analytics. They still affect the relevant account balances.

If an incoming transaction is incorrectly recognized as an internal transfer, open that transaction and change its category.

## Security notes

- Never commit real SMS exports, local databases, signing keys, account numbers, webhook URLs, or API secrets.
- Release builds in this repository are intentionally unsigned. Configure your own secure signing process outside version control.
- Android and Google Play apply additional restrictions to apps requesting SMS permissions. Review the current distribution policies before publishing to an app store.

## Project structure

```text
app/src/main/java/com/example/bankmonitor/  App logic, parser, and local database
app/src/main/res/                           Theme, navigation, and launcher assets
app/src/main/AndroidManifest.xml            Permissions and Android components
```

## License

Released under the [MIT License](LICENSE).
