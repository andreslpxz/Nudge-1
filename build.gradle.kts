// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    
    // Este es el plugin de Compose Compiler que ahora es parte de Kotlin 2.0
    // Se define aquí para que todo el proyecto sepa qué versión usar
    alias(libs.plugins.compose.compiler) apply false
    
    // Plugin para que funcione la serialización de Supabase y JSON
    alias(libs.plugins.kotlin.serialization) apply false
}

// Opcional: Limpieza automática de la carpeta build
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
