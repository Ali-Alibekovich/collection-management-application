# ADR 0002: Blocking receive with a bounded worker pool

## Status
Accepted (replaces the original non-blocking spin loop)

## Context
The original accept loop used a non-blocking channel polled in a busy spin,
reused one receive buffer for every datagram while handler threads were still
reading it (masked by a 50 ms sleep), and spawned an unbounded thread per
request.

## Decision
The single `DatagramChannel` stays in blocking mode — with one channel a
blocking `receive` burns no CPU while idle and needs no `Selector`. Every
datagram is copied out of the receive buffer before dispatch, and handlers
run on a fixed pool sized to the CPU count.

## Consequences
- No buffer races and no idle spin; throughput is bounded by the pool, not by
  thread creation.
- A `Selector` becomes worth it only if the server ever listens on multiple
  channels.
- Verified by the end-to-end concurrency test (24 simultaneous clients).
