import org.gradle.api.Plugin
import org.gradle.api.Project

class PreConfig : Plugin<Project> {
    override fun apply(target: Project) {
        println("from Plugin PreConfig")
    }
}