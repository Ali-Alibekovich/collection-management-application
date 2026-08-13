# ADR 0001: Twin-class command protocol over Java serialization

## Status
Accepted (inherited from the original design, made explicit here)

## Context
The client and the server exchange commands over UDP using plain Java
serialization. A command needs different behaviour on each side: the client
variant builds and sends a request, the server variant executes it against the
collection and the database.

## Decision
Each wire command exists twice with the same fully qualified name
(`protocol.commands.*`) and a pinned `serialVersionUID`: one class in the
client module extending `ClientCommand`, one in the server module extending
`ServerCommand`. Java serialization resolves classes by name, so an object
serialized by the client materializes on the server as the server twin. The
shared `common` module holds the marker interface, the envelopes and the
domain model.

## Consequences
- No RPC framework and no hand-written codec; the JVM does the wire format.
- The twin pairs must stay name- and UID-compatible — guarded by the
  serialization round-trip tests and the end-to-end test.
- The format is JVM-only and fragile across refactorings; a schema-based
  protocol (e.g. protobuf) is the natural successor and is listed as future
  work.
