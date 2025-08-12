plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Kafka client
    implementation("org.apache.kafka:kafka-clients:3.6.1")
    
    // JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
    
    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// Define tasks for running producer and consumer
task("runProducer", JavaExec::class) {
    mainClass.set("org.example.ProducerApp")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

task("runConsumer", JavaExec::class) {
    mainClass.set("org.example.ConsumerApp")
    classpath = sourceSets["main"].runtimeClasspath
}