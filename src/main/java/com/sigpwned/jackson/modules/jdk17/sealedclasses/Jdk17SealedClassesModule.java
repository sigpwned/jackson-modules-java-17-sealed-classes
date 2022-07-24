package com.sigpwned.jackson.modules.jdk17.sealedclasses;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class Jdk17SealedClassesModule extends Module {
  @Override
  public String getModuleName() {
    return "Jdk17SealedClassesModule";
  }

  @Override
  public Version version() {
    // TODO PackageVersion.java
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    context.appendAnnotationIntrospector(new Jdk17AnnotationIntrospector());
  }
}
