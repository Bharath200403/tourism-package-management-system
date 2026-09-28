# Demo Credentials (Local / Demo Use Only)

These accounts are created automatically the first time the backend starts
against an empty database (see `DataSeeder.java`). **They are demo
credentials, not production credentials** — change or remove them before any
real deployment.

| Role         | Username    | Password       | Notes                                   |
|--------------|-------------|----------------|------------------------------------------|
| ADMIN        | `admin`     | `Admin@123`    | Full access to all admin endpoints/pages |
| TOUR_OPERATOR| `operator1` | `Operator@123` | Owns the 4 seeded demo packages          |
| CUSTOMER     | `customer1` | `Customer@123` | Has one COMPLETED trip + a review        |
| CUSTOMER     | `customer2` | `Customer@123` | Has one PENDING (unpaid) booking         |

## What's pre-loaded
- 4 destinations (Kerala, Goa, Manali, Rajasthan)
- 4 tour packages with inclusions/exclusions and itineraries
- Multiple schedules per package (mostly future-dated, one past/completed)
- 2 sample bookings (`BK-DEMO-0001` completed with payment+invoice+review,
  `BK-DEMO-0002` pending payment)

## Resetting demo data
Data lives in the `./data/tourismdb` Derby folder next to wherever you run
the backend from. To reset to a clean seeded state:
```bash
# stop the backend first
rm -rf data/tourismdb
# start the backend again - DataSeeder repopulates it automatically
```
`app.seed.enabled=false` (env var `APP_SEED_ENABLED=false`) disables
automatic seeding if you want to start from a truly empty database instead.
