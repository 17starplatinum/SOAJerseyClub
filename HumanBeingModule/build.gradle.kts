plugins {
    id("java")
    id("java-library")
    id("application")
    id("war")
}

group = "ru.itmo.cs.dandadan"

repositories {
    mavenCentral()
}

buildscript {
    extra.apply {
        set("junitVersion", "5.12.0")
        set("tcVersion", "1.21.3")
    }
}


sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
    }
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1")
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("jakarta.ejb:jakarta.ejb-api:4.0.1")
    compileOnly("jakarta.json.bind:jakarta.json.bind-api:3.0.1")
    compileOnly("jakarta.json:jakarta.json-api:2.1.3")
    compileOnly("jakarta.mvc:jakarta.mvc-api:2.1.0")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("jakarta.security.enterprise:jakarta.security.enterprise-api:3.0.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("jakarta.transaction:jakarta.transaction-api:2.0.1")
    compileOnly("jakarta.websocket:jakarta.websocket-api:2.1.1")
    compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:4.0.1")
    compileOnly("org.projectlombok:lombok:1.18.42")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.5.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.0")
    implementation("com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider:2.20.0")
    implementation("org.hibernate.orm:hibernate-core:6.0.0.Final")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:2.25.1")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    val junitVersion = rootProject.extra["junitVersion"]
    val tcVersion = rootProject.extra["tcVersion"]
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    // Обнаружены уязвимости в этих 2 фреймворках, советую поменять в будущем
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("org.testcontainers:junit-jupiter:${tcVersion}")

    testImplementation("org.testcontainers:postgresql:${tcVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}
