export default function About() {
  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 720 }}>
        <h1>About Wayfarer</h1>
        <p>
          Wayfarer is a local, self-contained tourism package management system. Customers can browse
          destinations and packages, check live availability, book multi-traveler trips, pay through a
          local payment simulation, and review completed trips. Tour operators manage their own packages,
          schedules and bookings, while administrators oversee the full catalog, customers, payments and
          reports.
        </p>
        <p>
          Every piece of data — destinations, packages, schedules, bookings, payments and reviews — is
          stored in a local Apache Derby database managed by the Spring Boot backend. No external APIs,
          payment gateways or cloud services are used.
        </p>
      </div>
    </div>
  )
}
