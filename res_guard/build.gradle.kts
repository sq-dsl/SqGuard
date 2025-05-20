plugins {
    `kotlin-dsl`
    //id("maven-publish")
}
gradlePlugin {
    plugins {
        create("resGuardPlugin") {
            id = "sq.res-guard"
            version = "0.0.1"
            implementationClass = "com.sqwerty.res_guard.ResGuardPlugin"
        }
    }
}

dependencies {
    implementation(":core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}