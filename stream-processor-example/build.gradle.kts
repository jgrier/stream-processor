plugins {
    java
    application
}

dependencies {
    implementation(project(":stream-processor-core"))
    annotationProcessor("dev.restate:sdk-api-gen:2.4.1")

    runtimeOnly("org.apache.logging.log4j:log4j-core:2.23.1")
}

application {
    mainClass.set("dev.streamprocessor.example.WordCountExample")
}

tasks.register<JavaExec>("runContinuous") {
    mainClass.set("dev.streamprocessor.example.ContinuousWordCountExample")
    classpath = sourceSets["main"].runtimeClasspath
}
