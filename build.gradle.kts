tezovConfig {
    configuration {
        hasResources = false
        hasAssets = false
    }
    debug {
        hasJUnitRunner = true
    }
    release {
//        proguards.apply {
//            add(File("consumer-rules.pro"))
//        }
    }
    configureAndroidPlugin()
}

android {
    tezovCatalog {
        with("projectVersion") {
            compileSdk = int("defaultCompileSdk")
            compileOptions {
                sourceCompatibility = javaVersion("javaSource")
                targetCompatibility = javaVersion("javaTarget")
            }
            kotlinOptions {
                jvmTarget = javaVersion("jvmTarget").toString()
            }
            defaultConfig {
                minSdk = int("defaultMinCompileSdk")
            }
        }
    }
}

dependencies {
    tezovCatalog {
//api
        with("projectPath.dependencies.core") {
            api(string("annotation"))
            api(string("coroutines"))
            api(string("coroutines_android"))
        }

    }
}