plugins {
    `java-library`
}

dependencies {
    api("dev.restate:sdk-java-http:2.4.1")
    annotationProcessor("dev.restate:sdk-api-gen:2.4.1")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    compileOnly("org.apache.kafka:kafka-clients:3.7.0")

    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
}
