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
    // TODO Generate proper version?
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    // Add our sealed classes handler at the end of the handler list
    context.appendAnnotationIntrospector(new Jdk17SealedClassesAnnotationIntrospector());
  }
}
