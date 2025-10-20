plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Authorization Server (correct artifact)
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
