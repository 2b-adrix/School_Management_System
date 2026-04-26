buildscript {
    dependencies {
        classpath(libs.commons.compress)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kjvm) apply false
    alias(libs.plugins.kspring) apply false
    alias(libs.plugins.kjpa) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.secrets) apply false
}


