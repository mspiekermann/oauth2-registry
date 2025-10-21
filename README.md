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

## 💡 Approach: OAuth2 for joining Dataspaces

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