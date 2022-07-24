package com.sigpwned.jackson.modules.jdk17.sealedclasses;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.util.Objects;
import org.junit.Test;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jdk17SealedClassesModuleTest {
  /**
   * Our {@link ObjectMapper} uses the default configuration with the
   * {@link Jdk17SealedClassesModule} added in.
   */
  public static final ObjectMapper MAPPER =
      new ObjectMapper().registerModule(new Jdk17SealedClassesModule());

  /**
   * The "ExampleOne" objects test serialization of sealed classes without a JsonSubTypes
   * annotation, which is the new feature added by {@link Jdk17SealedClassesModule}.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  public static sealed class ExampleOne permits AlphaExampleOne, BravoExampleOne {
  }

  /**
   * Provide an explicit JsonTypeName here
   */
  @JsonTypeName("alpha")
  public static final class AlphaExampleOne extends ExampleOne {
    private String alpha;

    public AlphaExampleOne() {}

    public AlphaExampleOne(String alpha) {
      this.alpha = alpha;
    }

    /**
     * @return the alpha
     */
    public String getAlpha() {
      return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(String alpha) {
      this.alpha = alpha;
    }

    @Override
    public int hashCode() {
      return Objects.hash(alpha);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AlphaExampleOne other = (AlphaExampleOne) obj;
      return Objects.equals(alpha, other.alpha);
    }
  }

  /**
   * Use the default type name here
   */
  public static final class BravoExampleOne extends ExampleOne {
    public String bravo;

    public BravoExampleOne() {}

    public BravoExampleOne(String bravo) {
      this.bravo = bravo;
    }

    /**
     * @return the bravo
     */
    public String getBravo() {
      return bravo;
    }

    /**
     * @param bravo the bravo to set
     */
    public void setBravo(String bravo) {
      this.bravo = bravo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(bravo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BravoExampleOne other = (BravoExampleOne) obj;
      return Objects.equals(bravo, other.bravo);
    }
  }

  @Test
  public void oneAlphaTest() throws IOException {
    final String alpha = "apple";
    ExampleOne one =
        MAPPER.readValue("{\"type\":\"alpha\",\"alpha\":\"" + alpha + "\"}", ExampleOne.class);
    assertThat(one, is(new AlphaExampleOne(alpha)));
  }

  @Test
  public void oneBravoTest() throws IOException {
    final String bravo = "blueberry";
    ExampleOne one = MAPPER.readValue(
        "{\"type\":\"Jdk17SealedClassesModuleTest$BravoExampleOne\",\"bravo\":\"" + bravo + "\"}",
        ExampleOne.class);
    assertThat(one, is(new BravoExampleOne(bravo)));
  }

  /**
   * The "ExampleTwo" objects test serialization of sealed classes with a JsonSubTypes annotation,
   * which is the existing approach that must not break.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes({@JsonSubTypes.Type(AlphaExampleTwo.class),
      @JsonSubTypes.Type(value = BravoExampleTwo.class, name = "bravo")})
  public static sealed class ExampleTwo permits AlphaExampleTwo, BravoExampleTwo {
  }

  /**
   * Provide an explicit JsonTypeName here
   */
  @JsonTypeName("alpha")
  public static final class AlphaExampleTwo extends ExampleTwo {
    private String alpha;

    public AlphaExampleTwo() {}

    public AlphaExampleTwo(String alpha) {
      this.alpha = alpha;
    }

    /**
     * @return the alpha
     */
    public String getAlpha() {
      return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(String alpha) {
      this.alpha = alpha;
    }

    @Override
    public int hashCode() {
      return Objects.hash(alpha);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AlphaExampleTwo other = (AlphaExampleTwo) obj;
      return Objects.equals(alpha, other.alpha);
    }
  }


  /**
   * Use the default type name here
   */
  public static final class BravoExampleTwo extends ExampleTwo {
    public String bravo;

    public BravoExampleTwo() {}

    public BravoExampleTwo(String bravo) {
      this.bravo = bravo;
    }

    /**
     * @return the bravo
     */
    public String getBravo() {
      return bravo;
    }

    /**
     * @param bravo the bravo to set
     */
    public void setBravo(String bravo) {
      this.bravo = bravo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(bravo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BravoExampleTwo other = (BravoExampleTwo) obj;
      return Objects.equals(bravo, other.bravo);
    }

  }

  @Test
  public void twoAlphaTest() throws IOException {
    final String alpha = "apple";
    ExampleTwo two =
        MAPPER.readValue("{\"type\":\"alpha\",\"alpha\":\"" + alpha + "\"}", ExampleTwo.class);
    assertThat(two, is(new AlphaExampleTwo(alpha)));
  }

  @Test
  public void twoBravoTest() throws IOException {
    final String bravo = "blueberry";
    // Make sure we pick up the "bravo" name from the @@JsonSubTypes annotation.
    ExampleTwo two =
        MAPPER.readValue("{\"type\":\"bravo\",\"bravo\":\"" + bravo + "\"}", ExampleTwo.class);
    assertThat(two, is(new BravoExampleTwo(bravo)));
  }

  /**
   * The "ExampleTwo" objects test serialization of conventional classes with a JsonSubTypes
   * annotation, which is the existing approach that must not break.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes({@JsonSubTypes.Type(AlphaExampleThree.class),
      @JsonSubTypes.Type(value = BravoExampleThree.class, name = "bravo")})
  public static class ExampleThree {
  }

  /**
   * Provide an explicit JsonTypeName here
   */
  @JsonTypeName("alpha")
  public static final class AlphaExampleThree extends ExampleThree {
    private String alpha;

    public AlphaExampleThree() {}

    public AlphaExampleThree(String alpha) {
      this.alpha = alpha;
    }

    /**
     * @return the alpha
     */
    public String getAlpha() {
      return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(String alpha) {
      this.alpha = alpha;
    }

    @Override
    public int hashCode() {
      return Objects.hash(alpha);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      AlphaExampleThree other = (AlphaExampleThree) obj;
      return Objects.equals(alpha, other.alpha);
    }
  }


  /**
   * Use the default type name here
   */
  public static final class BravoExampleThree extends ExampleThree {
    public String bravo;

    public BravoExampleThree() {}

    public BravoExampleThree(String bravo) {
      this.bravo = bravo;
    }

    /**
     * @return the bravo
     */
    public String getBravo() {
      return bravo;
    }

    /**
     * @param bravo the bravo to set
     */
    public void setBravo(String bravo) {
      this.bravo = bravo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(bravo);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BravoExampleThree other = (BravoExampleThree) obj;
      return Objects.equals(bravo, other.bravo);
    }

  }

  @Test
  public void threeAlphaTest() throws IOException {
    final String alpha = "apple";
    ExampleThree three =
        MAPPER.readValue("{\"type\":\"alpha\",\"alpha\":\"" + alpha + "\"}", ExampleThree.class);
    assertThat(three, is(new AlphaExampleThree(alpha)));
  }

  @Test
  public void threeBravoTest() throws IOException {
    final String bravo = "blueberry";
    // Make sure we pick up the "bravo" name from the @@JsonSubTypes annotation.
    ExampleThree three =
        MAPPER.readValue("{\"type\":\"bravo\",\"bravo\":\"" + bravo + "\"}", ExampleThree.class);
    assertThat(three, is(new BravoExampleThree(bravo)));
  }
}
