import dev.crmodders.cosmicloom.extention.LoomGradleExtension.getCosmicQuilt
import dev.crmodders.cosmicloom.extention.LoomGradleExtension.getCosmicReach
import dev.crmodders.cosmicloom.task.tasks.RunClientTask

object Constants {
    const val GROUP = "io.github.startsmercury"
    const val MODID = "ao-anisotropy-fix"
    const val VERSION = "0.1.1"

    const val DISPLAY_NAME = "AO Anisotropy Fix"

    const val VERSION_COSMIC_REACH = "0.3.27"
    const val VERSION_JAVA = "17"
}

plugins {
    `java-library`
    `maven-publish`
    id("cosmicloom")
}

if (group == path.substring(1).replace(':', '.')) {
    group = Constants.GROUP
}

base {
    archivesName = Constants.MODID
}

if (Project.DEFAULT_VERSION == version) {
    version = createVersionString()
}

loom {
    accessWidenerPath = file("src/main/resources/ao-anisotropy-fix.accesswidener")
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain.languageVersion = JavaLanguageVersion.of(Constants.VERSION_JAVA)
}

repositories {
    ivy {
        name = "GitHub Releases"
        url = uri("https://github.com")
        patternLayout {
            artifact("[organization]/releases/download/[revision]/[module](-[classifier]).[ext]")
            artifact("[organization]/releases/download/[revision]/[module]-[revision](-[classifier]).[ext]")
            setM2compatible(true)
        }
        metadataSources {
            artifact()
        }
    }
}

dependencies {
    // Cosmic Reach
    cosmicReach(getCosmicReach("pre-alpha", Constants.VERSION_COSMIC_REACH))

    // Cosmic Quilt
    modImplementation(getCosmicQuilt("2.3.2"))

    // Mod Menu
    modImplementation(
        group = "org.codeberg.CRModders",
        name = "modmenu",
        version = "1.0.9",
    )

    // Simply Shaders
    compileOnly(
        group = "Shfloop.SimplyShaders",
        name = "SimplyShaders-1.1.4",
        version = "v1.1.4+0.3.1",
    )
}

tasks {
    withType<ProcessResources> {
        // Locations of where to inject the properties
        val resourceTargets = listOf(
            "quilt.mod.json",
        )

        // Left item is the name in the target, right is the variable name
        val replaceProperties = mutableMapOf<String, Any>(
            "group" to Constants.GROUP,
            "modid" to Constants.MODID,
            "display_name" to Constants.DISPLAY_NAME,
            "version" to Constants.VERSION,

            "cosmicreach_version" to Constants.VERSION_COSMIC_REACH,
        )

        inputs.properties(replaceProperties)
        replaceProperties["project"] = project

        filesMatching(resourceTargets) {
            expand(replaceProperties)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    javadoc {
        options {
            this as StandardJavadocDocletOptions

            source = "17"
            encoding = "UTF-8"
            charSet = "UTF-8"
            memberLevel = JavadocMemberLevel.PACKAGE
            addStringOption("Xdoclint:none", "-quiet")
            tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:",
            )
        }

        source(sourceSets.main.get().allJava)
        classpath = files(sourceSets.main.get().compileClasspath)
        include("**/api/**")
        isFailOnError = true
    }

    val runClient by existing(RunClientTask::class) {
        jvmArgs("-Dmixin.debug=true");
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

fun createVersionString(): String {
    val builder = StringBuilder()

    val isReleaseBuild = project.hasProperty("build.release")
    val buildId = System.getenv("GITHUB_RUN_NUMBER")

    if (isReleaseBuild) {
        builder.append(Constants.VERSION)
    } else {
        builder.append(Constants.VERSION.substringBefore('-'))
        builder.append("-snapshot")
    }

    builder.append("+cr").append(Constants.VERSION_COSMIC_REACH)

    if (!isReleaseBuild) {
        if (buildId != null) {
            builder.append("-build.${buildId}")
        } else {
            builder.append("-local")
        }
    }

    return builder.toString()
}

