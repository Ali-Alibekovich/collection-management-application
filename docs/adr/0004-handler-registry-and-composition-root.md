# ADR 0004: Handler registry and a hand-wired composition root

## Status
Accepted (supersedes "deserialized commands execute themselves")

## Context
The server originally worked on static singletons (`CollectionManager`,
`DatabaseCommunicator`) and a god-class `Receiver` that mixed authentication,
business logic and transport for every command. Deserialized command objects
executed themselves, so they could only reach their dependencies through
global state — which made handlers untestable without a database and spread
duplicate credential checks across a dozen methods.

## Decision
Commands arrive as **data**: the server-side twins are empty marker classes,
and a `CommandDispatcher` routes each envelope to a `CommandHandler` looked up
by the command's class (Strategy). Handlers are constructed once in `Main` —
a hand-wired composition root — and receive their dependencies through
constructors: the `UserStore`/`OrganizationStore` abstractions (implemented by
the JDBC repositories) and the instance-based `OrganizationCollection`.
Credential verification lives in one place, the `AuthenticatedHandler`
template. Handlers are pure request→answer functions; sending bytes stays in
the net layer.

SOLID mapping:
- **S** — `Receiver` (230 lines, everything at once) became 14 single-purpose
  handlers plus a collection service.
- **O** — a new wire command is a new handler registration, not an edit to a
  dispatch switch.
- **L/I** — stores are narrow interfaces; twins no longer inherit an
  `execute` they must fake.
- **D** — high-level policy (handlers, collection) depends on abstractions;
  JDBC lives behind them; the object graph is wired at the edge in `Main`.

## Consequences
- Handlers are unit-testable with plain mocks — no database, no sockets
  (see `handlers/*Test`).
- No global mutable state: tests no longer reset statics, and two server
  instances could coexist in one JVM.
- The wire protocol is unchanged — verified by the untouched black-box
  end-to-end test.
- A DI container would remove the hand-written wiring in `Main`; at this
  size, explicit wiring is easier to read than a framework.
