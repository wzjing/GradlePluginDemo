package com.wzjing

import com.android.build.VariantOutput
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject

private const val TAG = "PreConfig"

data class Channels(var name: String, var channels: Array<String>)

data class Signature(
    var keyAlias: String,
    var keyPassword: String,
    var storeFile: String,
    var storePass: String
)

data class Configuration(
    var applicationId: String? = null,
    var versionCode: Int = 999,
    var versionName: String? = null,
    var appChannels: Array<Channels> = arrayOf(Channels("伊对", arrayOf("market_QQ"))),
    var constants: Array<String> = emptyArray(),
    var signature: Signature
)

open class PreConfigPluginExtension(objects: ObjectFactory) {
    val configFile: Property<File> = objects.property()
}

open class ConfigTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {
    @Input
    val configFileProp: RegularFileProperty = objects.fileProperty()

    @TaskAction
    fun compile() {
        println("configuring android plugin")
        project.plugins.forEach {
            when (it) {
                is AppPlugin -> {
                    val configFile = configFileProp.orNull?.asFile
                    if (configFile == null) {
                        println("$TAG : Config File not set, please add config to you gradle build file")
                        return@forEach
                    } else if (!configFile.exists()) {
                        println("$TAG : Config File not exits ${configFile.absolutePath}")
                        return@forEach
                    }
                    val config: Configuration
                    try {
                        config = configFile.readText().fromJson(Configuration::class)
                    } catch (e: Exception) {
                        println("$TAG : error while deserialize config file ${configFile.absolutePath}")
                        e.printStackTrace()
                        return@forEach
                    }
                    it.extension.apply {
                        defaultConfig {
                            versionCode = config.versionCode
                            versionName = config.versionName
                        }

                        signingConfigs {
                            getByName("debug") {
                                storeFile = File(config.signature.storeFile)
                                storePassword = config.signature.keyPassword
                                keyAlias = config.signature.keyAlias
                                keyPassword = config.signature.keyPassword
                            }

                            getByName("release") {
                                storeFile = File(config.signature.storeFile)
                                storePassword = config.signature.keyPassword
                                keyAlias = config.signature.keyAlias
                                keyPassword = config.signature.keyPassword
                            }
                        }

                        buildTypes {
                            getByName("debug") {
                                applicationIdSuffix = "test"
                                signingConfig = signingConfigs.getByName("debug")
                            }

                            getByName("release") {
                                applicationIdSuffix = "publish"
                                signingConfig = signingConfigs.getByName("release")
                            }
                        }
                    }
                }
            }
        }
    }

}

class PreConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("Plugin : $TAG")
        val extension = target.extensions.create(
            "preconfig",
            PreConfigPluginExtension::class.java,
            target.objects
        )

        val appExtension = target.extensions.findByName("android") as? AppExtension
        appExtension?.productFlavors?.forEach {
            println("$TAG : production flavor ${it.name}")
        }

        println("$TAG : appExtension ${appExtension == null}")

        appExtension?.apply {
            defaultConfig {
                versionName = "9.9.9"
            }

            buildTypes {
                getByName("debug") {
                    applicationVariants.forEach {variant ->
                        variant.outputs.forEach { variantOutput ->
                            (variantOutput as BaseVariantOutputImpl).apply {
                                outputFileName = "$outputFileName-${variant.buildType}-${variant.flavorName}-${variant.versionName}.apk"
                            }
                        }
                    }

                    println("$TAG : apply to buildType debug")
                }
            }
        }

//        val preConfig = target.tasks.register("preConfig", ConfigTask::class.java) {
//            configFileProp.set(extension.configFile.orNull)
//        }
//        target.tasks.findByName("preBuild")?.dependsOn(preConfig)
    }
}