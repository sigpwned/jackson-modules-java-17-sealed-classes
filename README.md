# Jackson Modules - Java 17 Sealed Classes [![tests](https://github.com/sigpwned/jackson-modules-java-17-sealed-classes/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/jackson-modules-java-17-sealed-classes/actions/workflows/tests.yml) ![Maven Central Version](https://img.shields.io/maven-central/v/com.sigpwned/jackson-modules-java17-sealed-classes)

`jackson-modules-java17-sealed-classes` is a [Jackson](https://github.com/FasterXML/jackson) module that adds improved support for polymorphic serialization of Java 17 sealed classes.

This implementation was [merged](https://github.com/FasterXML/jackson-databind/pull/5025) upstream into jackson-databind proper for [release 3.0.0-rc2](https://cowtowncoder.medium.com/jackson-3-0-0-rc2-minor-update-593306f89e2c#10b4). So as of Jackson 3.x, this module is now obsolete! I will continue to maintain for Jackson 2.x.

## Goals

* Add improved support for polymorphic serialization of Java 17 sealed classes

## Non-Goals

* Change any other aspects of Jackson serialization

## Usage

The versioning of this project is aligned with Jackson versioning. (For example, if your project is using Jackson 2.18.2, then you should add the latest version of 2.18.2.x available in Maven Central to your POM file.)

    <dependency>
        <groupId>com.sigpwned</groupId>
        <artifactId>jackson-modules-java17-sealed-classes</artifactId>
        <version>2.19.0.0</version>
    </dependency>

To activate this feature, users must register the `Jdk17SealedClassesModule` module with the `ObjectMapper`. Simply include this library, and add the module to the `ObjectMapper`:

    ObjectMapper mapper=new ObjectMapper().registerModule(new Jdk17SealedClassesModule());

Without this module, sealed classes must use the `@JsonSubTypes` annotation for [polymorphic serialization](https://www.baeldung.com/jackson-annotations), just like any other class:

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=AlphaSealedExample.class, name="alpha"),
        @JsonSubTypes.Type(value=BravoSealedExample.class, name="bravo")})
    public sealed class SealedExample permits AlphaSealedExample, BravoSealedExample {
    }
    
    public final class AlphaSealedExample extends SealedExample {
        private String alpha;
        
        public String getAlpha() {
            return alpha;
        }
        
        public void setAlpha(String alpha) {
            this.alpha = alpha;
        }
        
        public SealedExample withAlpha(String alpha) {
            setAlpha(alpha);
            return this;
        }
    }
    
    public final class BravoSealedExample extends SealedExample {
        private String bravo;
        
        public String getBravo() {
            return bravo;
        }
        
        public void setBravo(String bravo) {
            this.bravo = bravo;
        }
        
        public SealedExample withBravo(String bravo) {
            setBravo(bravo);
            return this;
        }
    }

However, this is repetitive, since sealed classes already enumerate their subtypes explicitly. With this module, users get polymorphic serialization of sealed classes by using only the `@JsonTypeInfo` annotation on the parent sealed class. Users can also add `@JsonTypeName` to child classes to customize subtype naming, but it is not required.

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    public sealed class SealedExample permits AlphaSealedExample, BravoSealedExample {
    }
    
    @JsonTypeName("alpha")
    public final class AlphaSealedExample extends SealedExample {
        private String alpha;
        
        public String getAlpha() {
            return alpha;
        }
        
        public void setAlpha(String alpha) {
            this.alpha = alpha;
        }
        
        public SealedExample withAlpha(String alpha) {
            setAlpha(alpha);
            return this;
        }
    }
    
    @JsonTypeName("bravo")
    public final class BravoSealedExample extends SealedExample {
        private String bravo;
        
        public String getBravo() {
            return bravo;
        }
        
        public void setBravo(String bravo) {
            this.bravo = bravo;
        }
        
        public SealedExample withBravo(String bravo) {
            setBravo(bravo);
            return this;
        }
    }
    
Either example would result in the following JSON:

    serialize(new AlphaSealedExample().withAlpha("value")) → {"type":"alpha","alpha":"value"}

    deserialize({"type":"alpha","alpha":"value"}) → new AlphaSealedExample().withAlpha("value")
    
    serialize(new BravoSealedExample().withBravo("value")) → {"type":"bravo","bravo":"value"}
    
    deserialize({"type":"bravo","bravo":"value"}) → new BravoSealedExample().withBravo("value")
    
## Acknowledgements

Many thanks to [@shartte](https://github.com/shartte) for his work on [FasterXML/jackson-module-kotlin#239](https://github.com/FasterXML/jackson-module-kotlin/issues/239), where he implemented a very similar feature for Kotlin. And of course, thank you to [@cowtowncoder](https://github.com/cowtowncoder) for maintaining the excellent [FasterXML/jackson](https://github.com/FasterXML/jackson) library for all this time.

