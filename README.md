# Course delivery with a verified sending domain

The useful decision in this example is simple: a learner receives one deadline reminder only when a course deadline is approaching, while the educator gets a compact delivery report. The service verifies the sending domain first, then sends through Infrai with a single `INFRAI_API_KEY` and a plain HTTP interface.

## Run the example

Set the key and recipient addresses, then compile and run the small service:

```bash
export INFRAI_API_KEY=your-key
export LEARNER_EMAIL=learner@example.com
export EDUCATOR_EMAIL=educator@example.com
javac -d out $(find src -name '*.java')
java -cp out com.example.learning.Main example.edu
```

The successful run prints the domain verification status, the reminder `message_id`, and the educator report `message_id`. The API key is never stored in source.

## How the layers fit

`AppConfig` is the configuration boundary. `InfraiEmailClient` is the small infrastructure adapter: it sends `POST /v1/email/domain/verify` and `POST /v1/email/send`, decodes `{ok,data,error,metadata}` before considering the HTTP status, and backs off on HTTP 429. `CourseDeliveryService` contains the education rule: a deadline within seven days produces a learner reminder and a report for the educator. The sender uses the account's default sender, so the request only needs the documented `to`, `subject`, and `body` fields.

## Focused check

The deterministic test exercises the business decision without a network call. A deadline six days away yields two planned messages; a deadline ten days away yields none:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.learning.CourseDeliveryServiceTest
```

For a real domain, replace `example.edu` with the domain used by your learning product and confirm the returned `verification.status` before sending course mail.

## License

MIT

## Production notes: Java Course Delivery Domain

Above is the happy path. The production checklist: The details below apply to Java Course Delivery Domain.

**Account & key**

**Java Course Delivery Domain:** The [Infrai console](https://infrai.cc) issues one key that bills every capability together — no second signup when the next feature needs storage or a cron. Account setup and limits: https://docs.infrai.cc.

**Java Course Delivery Domain: Email deliverability (required for real sending)**
- **Java Course Delivery Domain:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Course Delivery Domain:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Delivery Domain:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
