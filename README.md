# Towards more dynamic Dataspaces

In today’s data economy, **dataspaces** promise trusted data sharing across organizational boundaries. Yet, many current initiatives rely on **central governance authorities** that onboard participants. While this works for early-stage deployments, it limits scalability and impedes the emergence of dynamic, distributed ecosystems.

This project explores how **OAuth2 mechanisms** can be applied to dataspaces to enable **scalable onboarding**—without locking participants into individual governance silos. It is a POC and by no means should be used in production! It is just to bootstrap the discussion within the dataspace community, based on some working code and not just paperwork. 

**There is no direct link to IDSA or some service offering by the association, it is used as an example only!**

---

## 🚧 Problem Background

As dataspaces evolve, we are witnessing several emerging trends and growing challenges:

- ✅ Efforts are underway to **enable switching between dataspace contexts**
- ✅ **Reusable dataspace components** can already work across multiple dataspaces
- ❓ But **how will future dataspaces evolve** beyond centralized governance models?
- ❓ How will **ecosystems for data sharing form dynamically**, across business domains?

We expect to see:

- 🌐 **Service providers offering a bunch of use cases across all domains that leverage dataspace capabilities**
- 🔗 Participants will **join these services by registering their components** (e.g., connectors, agents)

However, there's a major usability roadblock:

> ❗ **Nobody wants to register individually for every single dataspace-enabled service.**

Today, most dataspaces rely on **manual company registration via web portals**—a repetitive, error-prone process that doesn't scale. Even as **agentic systems** begin to automate machine-readable registration and login, **manual onboarding remains the norm** in industry use cases and dataspace initiatives.

This current onboarding model is **blocking mainstream dataspace adoption**.

---

## 💡 Approach A: OAuth2 for joining Dataspaces

We can learn from other domains: **federated identity in the web scaled because of OAuth2**.

Instead of dozens of isolated signup processes, familiar options are:

> _"Log in with Google"_,  
> _"Continue with GitHub"_,  
> _"Sign in with Facebook"_.

Similarly, dataspaces need **federated onboarding and authorization**:

| Current Reality                            | Future Vision                                    |
|--------------------------------------------|--------------------------------------------------|
| Manual registration portals                | OAuth2-based seamless onboarding                 |
| One account per dataspace service          | Reusable identity and trust relationships        |
| Central governance per dataspace           | Federated trust with user-controlled delegation  |
| Static registries                          | Dynamic discovery of trusted services           |

Imagine a future where your connector, agent, or company dashboard offers:

> 👉 **“Log in with IDSA”** 
> 👉 **“Join via Catena-X Trust Authority”**  
> 👉 **“Authenticate with Eclipse Foundation”**

Using OAuth2-based delegation, **trusted identity providers** could share verified metadata i.e. the Connector and/or Catalog endpoint. If a dataspace onboarding process requires even more information? After the connector endpoint is known services can leverage the capabilities of the Decentralized Claims Protocol to get credentials and claims, for example about:

- business identity
- security attestations
- certifications
- trust level agreements

## Approach B: Decentralized Identity — Federation Without Central Authorities

While OAuth2 enables federated trust through centralized providers,
Decentralized Identifiers (DIDs) take the next step: they **enable trust without central control**.

Instead of logging in through a fixed authority, users and systems authenticate as verifiable, self-sovereign identities — controlled by cryptographic keys rather than user accounts.

> 👉 **“Authenticate with your DID”**
> 👉 **“Prove control of your organization’s identifier”**  
> 👉 **“Sign in with your digital wallet or agent”**

This is self-sovereign identity (SSI) in action — removing the need for each dataspace to maintain its own identity silos.

## 🚀 Getting Started: Demo Application

To illustrate the concept, this project includes a demo application that demonstrates how both OAuth2 federation and Decentralized Identifier (DID) mechanisms can be used to dynamically register and authenticate dataspace participants.

The demo simulates a minimal dataspace ecosystem with:

- A centralized identity provider (the IDSA Registry) representing a traditional OAuth2/OIDC trust authority
- A decentralized identity provider (the DID Registry) representing a self-sovereign trust model
- A demo dataspace service acting as an OAuth2 client and resource consumer

You can run the services locally to explore:

- Classic OAuth2/OpenID Connect login flows (Login via IDSA)
- Decentralized, proof-based login flows using DIDs (Login via DID)
and understand how federated and decentralized onboarding can coexist in a unified dataspace trust framework.

This side-by-side setup highlights how dataspaces can evolve:

- From centralized identity management toward decentralized, self-sovereign trust
- While still using standard OAuth2-based protocols for secure and interoperable authentication

| Feature              | Federated Registry            | DID Registry                       |
| -------------------- |------------------------------------------| ------------------------------------------------- |
| **Login Mechanism**  | Username & password                      | DID + signed challenge (JWS)                      |
| **Identity Model**   | Central registry (federated trust)       | Self-sovereign (no central authority)             |
| **User Metadata**    | Stored and managed centrally             | Derived dynamically from DID document             |
| **OAuth2 Flow**      | Standard Authorization Code              | Same flow, but proof-of-control replaces password |
| **Integration Goal** | Simulate centralized dataspace onboarding | Demonstrate decentralized trust via DIDs          |


> 💡 Detailed setup instructions and configuration steps will be provided below — simply follow them to start the demo and experiment with different onboarding scenarios.


1. Clone the repository

```bash
git clone https://github.com/mspiekermann/oauth2-registry.git
cd oauth2-registry
```

2. Start the classic federated identity provider

```bash
./gradlew :idsa-registry:bootRun
```
The IDSA Registry simulates a central identity authority.
It supports login via static demo users:
- `alice` / `password`
- `bob` / `password`

The registry issues ID tokens containing a participant-specific connector endpoint (metadata claim).

3. Start the DID Registy

Run the decentralized identity provider (no username/password — uses DID-based challenge proof):

```bash
./gradlew :did-registry:bootRun
```

This service issues tokens after verifying a DID-based proof.

You can test it easily using the built-in bypass DID:
- DID: did:web:example.com
- JWS: (any string, e.g. eyJhbGciOiJub25lIn0.eyJjaGFsbGVuZ2UiOiJzaWduaW5nX2lzX2Rpc2FibGVkIn0.)

The DID Registry mimics Self-Sovereign Identity (SSI) behavior:

- No centralized user database
- Identities are verified through cryptographic signatures tied to decentralized identifiers
- Metadata (like connector endpoint URLs) is dynamically resolved or derived

4. Start the demo service

Run the sample dataspace service that consumes identity tokens:

```bash
./gradlew :demo-service:clean :demo-service:bootRun
```

The demo service can log in with either provider:
- Login via IDSA → centralized OAuth2 flow
- Login via DID → decentralized DID-based flow

Visit:
👉 http://localhost:8080