rootProject.name = "hypertrace-service"

pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    maven("https://dl.bintray.com/hypertrace/maven")
  }
}

plugins {
  id("org.hypertrace.version-settings") version "0.1.2"
}

includeBuild("./attribute-service")
includeBuild("./entity-service")
includeBuild("./gateway-service")
includeBuild("./query-service")
includeBuild("./hypertrace-graphql")
includeBuild("./hypertrace-graphql/hypertrace-core-graphql")
includeBuild("./config-bootstrapper")
includeBuild("./config-service")

include(":hypertrace-service")
include(":hypertrace-data-query-service")
include(":hypertrace-data-config-service")
