plugins {
    java
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    modularity.inferModulePath.set(false)
}

application {
    mainClass.set("com.rj.demo.engine.frontend.App")
}

dependencies {
    implementation("org.apache.calcite:calcite-core:1.39.0")
    implementation("io.substrait:isthmus:0.54.0")
}