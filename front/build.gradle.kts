import com.google.devtools.ksp.gradle.KspTaskMetadata
import opensavvy.gradle.vite.base.viteConfig
import org.apache.tools.ant.filters.ReplaceTokens

//import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
//
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vite.kotlin)
}
//	//
////	alias(libs.plugins.kotlin.multiplatform)
////	alias(libs.plugins.google.ksp)
//	// List of releases: https://gitlab.com/opensavvy/kotlin-vite/-/releases
//	id("dev.opensavvy.vite.kotlin") version "0.5.0"// version "REPLACE THIS"
//}
//
repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // new repository here

}

vite {
    base.set("/codeServer2/body")
    build {
        outDir.set(layout.projectDirectory.dir("../backend/body"))
    }
}

kotlin {
    js(IR) {
        useCommonJs()
        browser()
        binaries.executable()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.fritz2.core)
                // implementation("dev.fritz2:headless:$fritz2Version") // optional headless comp
            }
        }
        jsMain {
            dependencies {
//                implementation(npm("@codemirror/state", "6.4.1"))
//                implementation(npm("@codemirror/view", "6.35.0"))
//                implementation(npm("@codemirror/command", "6.7.1"))
            }
        }
    }
}

// KSP support for Lens generation
dependencies.kspCommonMainMetadata(libs.fritz2.lenses)
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }

// plugin viteの足りない部分を補う（add by kiwamu）
//tasks.named("viteRun") {
//    doFirst {
//        //println(layout.buildDirectory.dir("muu").toString())
//        copy {
//            from(layout.projectDirectory.file("postcss.config.mjs"))
//            into(layout.buildDirectory.dir("vite/dev/child"))
//        }
//    }
//}
tasks.named("viteRun") {
    doFirst {
//        println(viteConfig.base.get())
//        println(viteConfig.build.outDir.get())

        copy {
            from(layout.projectDirectory.file("vite.config.templ.js"))
            into(layout.buildDirectory.file("vite/dev/child"))
            expand(
                Pair("base", viteConfig.base.get()),
                Pair("outDir", viteConfig.build.outDir.get()),
                )
            rename("vite.config.templ.js", "vite.config.js")
        }
    }
}

tasks.named("viteBuild") {
    doLast {
        copy {
            from(layout.projectDirectory.file("vite.config.templ.js"))
            into(layout.buildDirectory.file("vite/prod"))
            expand(
                Pair("base", viteConfig.base.get()),
                Pair("outDir", viteConfig.build.outDir.get()),
            )
            rename("vite.config.templ.js", "vite.config.js")
        }
    }
}
