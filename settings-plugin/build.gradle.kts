plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("githubPackages") {
            id = "settings.githubPackages"
            implementationClass = "settings.GithubPackagesPlugin"
        }
    }
}
