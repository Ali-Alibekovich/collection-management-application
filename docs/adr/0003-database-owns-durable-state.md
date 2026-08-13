# ADR 0003: PostgreSQL owns durable state; passwords are bcrypt hashes

## Status
Accepted (replaces JSON-file persistence and MD2 hashing)

## Context
Earlier iterations persisted the collection to a JSON file and hashed
passwords with MD2 plus string padding; credential checks compared login and
password columns independently, so any user's password worked for any login.

## Decision
PostgreSQL is the single durable store: organizations and users live in
tables created on startup, and the in-memory collection is a cache loaded
from it. Passwords are stored as bcrypt hashes (salt embedded) and
credentials are verified as a pair via a targeted query.

## Consequences
- Restarts lose nothing; concurrent instances would need row-level
  coordination (out of scope for now).
- bcrypt makes every check deliberately slow (~100 ms) — acceptable for this
  scale, and the reason the regression suite checks pairs, not columns.
