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
        with("projectVersions") {
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
        with("libraries.runtime.kmm_core") {
            api(string("annotation"))
            api(string("coroutines"))
        }

    }
}