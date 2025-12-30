plugins {
  id("java")
}

val junitVersion by extra { "5.12.0" }
val jacksonVersion by extra { "2.20.0" }
val mockitoVersion by extra { "5.21.0" }
val tcVersion by extra { "1.21.4" }
val httpClientVersion by extra { "5.2.1" }

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("jakarta.ejb:jakarta.ejb-api:4.0.0")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("org.projectlombok:lombok:1.18.42")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider:$jacksonVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:$httpClientVersion")
    implementation("org.hibernate:hibernate-core:6.2.7.Final")
    implementation("org.hibernate:hibernate-jpamodelgen:6.2.7.Final")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.glassfish.jersey.media:jersey-media-jaxb:3.1.11")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    testImplementation("org.testcontainers:testcontainers:2.0.3")
    testImplementation("org.testcontainers:postgresql:$tcVersion")
    testImplementation("org.testcontainers:junit-jupiter:$tcVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.jar {
    archiveFileName.set("human-being-ejb.jar")
}

tasks.test {
    useJUnitPlatform()
}
