plugins {
    id("java")
    id("war")
}

val junitVersion by extra { "5.12.0" }
val jacksonVersion by extra { "2.20.0" }
val httpClientVersion by extra { "5.2.1" }

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    implementation(project(":human-being-ejb"))
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.glassfish.jersey.media:jersey-media-jaxb:2.25.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")
    implementation("org.hibernate:hibernate-core:6.2.7.Final")
    implementation("org.hibernate:hibernate-jpamodelgen:6.2.7.Final")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:$httpClientVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.war {
    archiveFileName.set("human-being-web.war")
    dependsOn(":human-being-ejb:jar")
}
