plugins {
    id("java")
    id("war")
}

val junitVersion by extra { "5.12.0" }
val httpClientVersion by extra { "5.2.1" }

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.glassfish.jersey.media:jersey-media-jaxb:2.25.1")
    implementation(project(":human-being-ejb"))
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:$httpClientVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.war {
    archiveFileName.set("human-being-web.war")
    dependsOn(":human-being-ejb.jar")
    rootSpec.include("**/human-being-ejb.jar")
}
