# Privacy

Bank Balance Monitor is designed to work locally on the Android device.

## Data accessed

With user permission, the app reads received SMS messages to identify supported transaction alerts from Commercial Bank, Sampath Bank, and Seylan Bank sender names.

## Data stored

Parsed transaction details, account balances, categories, and notes are stored in an on-device SQLite database. This database is part of the app's private storage.

## Data not collected

The public build does not include internet permission and does not transmit SMS messages or financial data to a developer, analytics provider, advertising network, cloud service, or third party.

OTP messages and messages from unsupported senders are not stored as transactions.

## Data deletion

Uninstalling the app removes its private local database under normal Android behavior. Users can also clear the app's storage from Android system settings.

## Important limitation

Bank SMS templates can change. Users should verify parsed amounts, categories, dates, and balances before relying on the app's reports.
