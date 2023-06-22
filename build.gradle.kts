plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("mysql:mysql-connector-java:8.0.26")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation(files("additional_libs/jave-1.0.2.jar"))
    implementation(files("additional_libs/TarsosDSP-1.9.jar"))


 
}

tasks.test {
    useJUnitPlatform()
}