plugins {
  java
  application
  jacoco
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
  id("org.hypertrace.jacoco-report-plugin")
}

dependencies {
  implementation("org.hypertrace.core.attribute.service:attribute-service")
  implementation("org.hypertrace.core.attribute.service:attribute-service-impl")
  implementation("org.hypertrace.entity.service:entity-service")
  implementation("org.hypertrace.entity.service:entity-service-impl")
  implementation("org.hypertrace.config.service:config-service")
  implementation("org.hypertrace.config.service:config-service-impl")

  implementation("org.eclipse.jetty:jetty-server:9.4.39.v20210325")
  implementation("org.eclipse.jetty:jetty-servlet:9.4.39.v20210325")
  implementation("org.eclipse.jetty:jetty-rewrite:9.4.39.v20210325")

  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.23")
  implementation("org.hypertrace.core.grpcutils:grpc-server-utils:0.4.0")
  implementation("org.hypertrace.core.grpcutils:grpc-client-utils:0.4.0")
  implementation("org.hypertrace.core.documentstore:document-store:0.5.4")
  // Config
  implementation("com.typesafe:config:1.4.1")
  // Logging
  implementation("org.slf4j:slf4j-api:1.7.30")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")

  // GRPC
  runtimeOnly("io.grpc:grpc-netty:1.36.1")
  constraints {
    runtimeOnly("io.netty:netty-codec-http2:4.1.63.Final") {
      because("https://snyk.io/vuln/SNYK-JAVA-IONETTY-1089809")
    }
    runtimeOnly("io.netty:netty-handler-proxy:4.1.63.Final") {
      because("https://snyk.io/vuln/SNYK-JAVA-IONETTY-1089809")
    }
  }
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}

hypertraceDocker {
  defaultImage {
    javaApplication {
      envVars.put("CLUSTER_NAME", "default-cluster")
      envVars.put("POD_NAME", "default-pod")
    }
  }
}

// Config for gw run to be able to run this locally. Just execute gw run here on Intellij or on the console.
tasks.run<JavaExec> {
  jvmArgs = listOf("-Dservice.name=${project.name}", "-Dcluster.name=default-cluster")
}

tasks.processResources {
  dependsOn("copyServiceConfigs");
}

tasks.register<Copy>("copyServiceConfigs") {
  with(
      createCopySpec("entity-service", "entity-service"),
      createCopySpec("attribute-service", "attribute-service"),
      createCopySpec("config-service", "config-service")
  ).into("./build/resources/main/configs/")
}

fun createCopySpec(projectName: String, serviceName: String): CopySpec {
  return copySpec {
    from("../${projectName}/${serviceName}/src/main/resources/configs/common") {
      include("application.conf")
      into(serviceName)
    }
  }
}

tasks.test {
  useJUnitPlatform()
}
