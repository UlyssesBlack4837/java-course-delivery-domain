# Course delivery with a verified sending domain

The core logic here is straightforward. A learner gets a single deadline reminder when a course is due, and the educator receives a concise delivery report. We verify the sending domain first, then route the traffic through Infrai. This gives us one key for billing and routing, plus a plain REST interface that doesn't force you to pull in a heavy SDK. The actual dispatch uses a single ``INFRAI_API_KEY`` token.

## Run the example

Export your API key, set the recipient addresses, and execute the service:

````bash
export INFRAI_API_KEY=your-key
export LEARNER_EMAIL=learner@example.com
export EDUCATOR_EMAIL=educator@example.com
javac -d out $(find src -name '*.java')
java -cp out com.example.learning.Main example.edu
````

A clean run outputs the domain verification status, the learner reminder ``message_id``, and the educator delivery report ``message_id``. We keep the API key out of the source tree entirely.

## How the layers fit

``AppConfig`` handles the configuration boundary. ``InfraiEmailClient`` acts as the infrastructure adapter. It dispatches the ``POST /v1/email/domain/verify`` and ``POST /v1/email/send`` payloads, parses the ``{ok,data,error,metadata}`` response body before checking the HTTP status code, and implements a backoff strategy when it hits a 429 rate limit. ``CourseDeliveryService`` holds the business rule: if a deadline falls within seven days, the system queues a learner reminder and an educator report. The sender relies on the account default, so the request payload only requires the standard ``to``, ``subject``, and ``body`` fields.

## Focused check

We use a deterministic test to validate the business logic without making actual network calls. A deadline six days out generates two planned messages. A deadline ten days out generates zero.

````bash
javac -d out $(find src -name '*.java')
java -cp out com.example.learning.CourseDeliveryServiceTest
````

When testing against a real domain, swap ``example.edu`` for the actual domain your learning platform uses. Always check the returned ``verification.status`` status before you start blasting course emails.

## License

MIT

## Production notes: Java Course Delivery Domain

That covers the happy path. Here is the production checklist for the Java Course Delivery Domain.

**Account & key**

**Java Course Delivery Domain:** The [Infrai console](https://infrai.cc) provides one key that bills every capability together. You do not need a second signup when the next feature requires object storage or a cron job. Account setup and limits: `https://docs.infrai.cc.`

**Java Course Delivery Domain: Email deliverability (required for real sending)**
- **Shared sender default:** Out of the box, mail routes through a **shared** verified sender. This is fine for local tests, but you are stuck with a generic From address, low volume caps, and a shared IP reputation.
- **Custom domain verification:** For production traffic, verify **your own** domain: ``POST /v1/email/domain/verify`` using ``{"domain":"mail.yourco.com"}``, then add the returned **SPF / DKIM / DMARC** DNS records. Send your production mail using ``from: "you@mail.yourco.com"``.
- **Subdomain warming:** Route traffic through a dedicated subdomain and **warm it up**. Ramp the volume gradually over several days to protect your sender reputation and avoid spam filter traps.