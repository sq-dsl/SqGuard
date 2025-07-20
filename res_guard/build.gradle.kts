plugins {
    `kotlin-dsl`
    id("java-library")
    id("maven-publish")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
gradlePlugin {
    plugins {
        create("resGuardPlugin") {
            id = "sq.res-guard"
            version = "0.0.3"
            implementationClass = "com.sqwerty.res_guard.ResGuardPlugin"
        }
    }
}
dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.core)
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                groupId = "com.github.sq-dsl"
                artifactId = "sq.res-guard"
                version = "0.0.3"
            }
        }
    }
}