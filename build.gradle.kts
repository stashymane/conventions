plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(buildLibs.kmp)
    compileOnly(buildLibs.android.library)
    compileOnly(buildLibs.compose)
}
