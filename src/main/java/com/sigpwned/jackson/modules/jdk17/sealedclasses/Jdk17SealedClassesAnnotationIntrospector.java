package com.sigpwned.jackson.modules.jdk17.sealedclasses;

import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;

public class Jdk17SealedClassesAnnotationIntrospector extends JacksonAnnotationIntrospector {
  /**
   * Matches other jackson values
   */
  private static final long serialVersionUID = 1L;

  @Override
  public Version version() {
    // TODO Generate proper version?
    return Version.unknownVersion();
  }

  /**
   * This new logic applies only to sealed classes, which were introduced as a preview feature in
   * Java 15 and released as a full feature in Java 17. In this feature, sealed classes declare
   * their "permitted subclasses" explicitly, which is essentially a baked-in {@link JsonSubTypes}
   * annotation. The subtypes themselves can use a {@link JsonTypeName} annotation to customize
   * their name, or use the default value assigned by Jackson. The top-level sealed class should
   * still use {@link JsonTypeInfo}.
   * 
   * For now, only one "layer" of sealed class subtypes is discovered, although this could change in
   * a future release. Of course, users are always free to use {@link JsonSubTypes} for more complex
   * serialization needs.
   */
  @Override
  public List<NamedType> findSubtypes(Annotated a) {
    if (a.getAnnotated() instanceof Class<?> klass) {
      if (klass.isSealed()) {
        Class<?>[] permittedSubclasses = klass.getPermittedSubclasses();
        if (permittedSubclasses.length > 0) {
          return Arrays.stream(permittedSubclasses).map(psc -> new NamedType(psc)).toList();
        }
      }
    }
    return null;
  }
}
