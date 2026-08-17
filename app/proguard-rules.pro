# Proguard rules for linkoDAW

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.linkodaw.Hilt_* { *; }

# Keep Room entities
-keep class com.linkodaw.domain.model.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep serialization
-keep class kotlinx.serialization.** { *; }

# Keep Media3
-keep class androidx.media3.** { *; }

# Keep ViewBinding
-keep class com.linkodaw.databinding.** { *; }