# Tourism Package Management System

A full-stack tourism package management system: customers browse destinations
and packages, check live seat availability, book multi-traveler trips, pay
through a local payment simulation, and review completed trips. Tour
operators manage their own packages, itineraries, schedules and bookings.
Administrators manage the full catalog, users, support tickets, reviews and
reports.

- **Backend:** Java 17, Spring Boot 3.3, Spring Security (JWT), Spring Data
  JPA, Apache Derby (embedded, file-based — no external database server).
- **Frontend:** React 19, Vite, React Router 7, Axios, plain CSS design
  system (no UI framework dependency).

**Start here:** [`RUN_AND_DEPLOY.txt`](./RUN_AND_DEPLOY.txt) — step-by-step
instructions to run the whole system on your own machine, and to host it for
free on Vercel (frontend) + Render (backend). Demo login credentials are in
[`DEMO_CREDENTIALS.md`](./DEMO_CREDENTIALS.md).

## Project layout

```
tourism-backend/     Spring Boot API (Maven project)
tourism-frontend/    React app (Vite project)
RUN_AND_DEPLOY.txt   Local run + free hosting guide
DEMO_CREDENTIALS.md  Seeded demo accounts
```

## What's implemented

- **Auth & roles:** JWT-based login/registration, three roles (CUSTOMER,
  TOUR_OPERATOR, ADMIN) enforced by Spring Security on every endpoint —
  frontend route guards are a UX convenience only, never the real gate.
- **Catalog:** destinations, tour packages, itineraries, inclusions/exclusions,
  schedules, search/filter by name/destination/duration/price.
- **Booking engine:** multi-traveler bookings with a **pessimistic-locked,
  transactional** seat-reservation step, so two people racing for the last
  seat can never both succeed (see `BookingService` and the accompanying
  unit test `BookingServiceOverbookingTest`).
- **Payments:** a clearly-labelled local **simulation** (no real gateway, no
  card data ever collected or stored) that generates a transaction reference
  and an invoice.
- **Cancellations:** a centralized, configurable refund-rule engine
  (full/partial/no refund based on days-before-trip), with automatic seat
  restoration.
- **Reviews & feedback:** post-trip reviews (only for COMPLETED bookings,
  one per booking), general feedback, and support tickets with staff
  resolution workflow.
- **Recommendations & trip planner:** an explicit, explainable rule-based
  scoring engine (not a black box / not "AI") for personalized suggestions
  and a budget/duration/style-based trip planner.
- **Dashboards & reports:** role-specific dashboards (customer/operator/admin)
  and six admin reports (bookings, revenue, package performance, customers,
  cancellations, occupancy).
- **Audit logging:** key state-changing actions are recorded and viewable by
  admins.
- **Automatic housekeeping:** a scheduled job closes expired schedules and
  marks finished trips COMPLETED (which then unlocks reviews).

## Known limitations / honesty notes

- **Backend compilation was not verified by an automated build in the
  environment this was built in**, because that sandbox could not reach
  Maven Central to download Spring Boot's dependencies (only npm's registry
  was reachable there). The code was written carefully and checked
  structurally (package/directory consistency, brace balance, cross-file
  method signatures), but you should run `mvn clean install` yourself the
  first time to confirm a clean build in your own environment — see
  `RUN_AND_DEPLOY.txt`. The **frontend was fully built and linted
  successfully** in that same environment (`npm run build` and `oxlint`
  both passed).
- Reports/aggregations run in memory over data fetched via JPA — appropriate
  at this app's local, single-machine scale, but not how you'd architect
  analytics at large scale.
- The recommendation/trip-planner "scoring" is a small set of transparent,
  hand-written rules (documented in `RecommendationService`), not a trained
  model — this is intentional, not a placeholder for a missing ML system.
- Apache Derby is file-based; on free hosting tiers without a persistent
  disk (see `RUN_AND_DEPLOY.txt`), data does not survive a redeploy/restart.
