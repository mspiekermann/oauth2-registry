plugins {
    id("org.springframework.boot") // keep using the version you had when things first ran (e.g. 3.3.3 or 3.3.3-ish)
    id("io.spring.dependency-management")
    java
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Roll back to pre-DPoP SAS
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}