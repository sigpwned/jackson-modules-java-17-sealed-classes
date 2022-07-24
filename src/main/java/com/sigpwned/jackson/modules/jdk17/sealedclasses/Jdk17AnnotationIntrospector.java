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

public class Jdk17AnnotationIntrospector extends JacksonAnnotationIntrospector {
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
   * Method for locating subtypes related to annotated entity (class, method, field). Note that this
   * is only guaranteed to be a list of directly declared subtypes, no recursive processing is
   * guarantees (i.e. caller has to do it if/as necessary). Handles sealed classes subtype discovery
   * automatically.
   * 
   * @param a Annotated entity (class, field/method) to check for annotations
   *
   * @return List of subtype definitions found if any; {@code null} if none
   */
  @Override
  public List<NamedType> findSubtypes(Annotated a) {
    // This is the "existing" subtype implementation, which uses the @JsonSubTypes annotation to
    // allow for polymorphic de/serialization. We
    List<NamedType> result = findSubtypesByAnnotation(a);
    if (result == null)
      result = findSubtypesByPermittedSubtypes(a);
    return result;
  }

  /**
   * This is the original {@link JsonSubTypes}-based implementation. We leave it here, untouched, to
   * ensure that existing code continues to work exactly the same.
   */
  protected List<NamedType> findSubtypesByAnnotation(Annotated a) {
    return super.findSubtypes(a);
  }

  /**
   * This new logic applies only to sealed classes, which were introduced as a preview feature in
   * Java 15 and released as a full feature in Java 17. In this feature, sealed classes declare
   * their "permitted subclasses" explicitly, which is essentially a baked-in {@link JsonSubTypes}
   * annotation. The subtypes themselves can use a {@link JsonTypeName} annotation to customize
   * their name, or use the default value assigned by Jackson. The top-level sealed class should
   * still use {@link JsonTypeInfo}.
   */
  protected List<NamedType> findSubtypesByPermittedSubtypes(Annotated a) {
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
