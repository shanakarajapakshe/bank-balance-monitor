# Security Policy

## Reporting a vulnerability

Please use GitHub's private vulnerability reporting feature for this repository. Do not post real SMS messages, account numbers, balances, secrets, or other financial information in a public issue.

## Repository hygiene

The repository must not contain:

- production signing keys or keystores;
- local SQLite databases or SMS exports;
- real account or card identifiers;
- API keys, webhook URLs, passwords, or secrets;
- personal financial screenshots.

The included `.gitignore` blocks common sensitive and generated files, but contributors must still review every commit before pushing it.
