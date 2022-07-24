# Jackson Modules - Java 17 Sealed Classes [![tests](https://github.com/sigpwned/jackson-modules-java-17-sealed-classes/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/jackson-modules-java-17-sealed-classes/actions/workflows/tests.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_jackson-modules-java-17-sealed-classes&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sigpwned_jackson-modules-java-17-sealed-classes) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/jackson-modules-java17-sealed-classes/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/jackson-modules-java17-sealed-classes)

`jackson-modules-java17-sealed-classes` is a [Jackson](https://github.com/FasterXML/jackson) module that adds improved support for polymorphic serialization of Java 17 sealed classes.

## Goals

* Add improved support for polymorphic serialization of Java 17 sealed classes

## Non-Goals

* Change any other aspects of Jackson serialization

## Usage

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

However, this is repetitive, since sealed classes must already enumerate their subtypes explicitly. With this module, users get polymorphic serialization of sealed classes by using only the `@JsonTypeInfo` annotation on the parent sealed class. Users can also add `@JsonTypeName` to child classes to customize subtype naming, but it is not required.

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

Many thanks to @shartte for his work on [FasterXML/jackson-module-kotlin#239](https://github.com/FasterXML/jackson-module-kotlin/issues/239), where he implemented a very similar feature for Kotlin. And of course, thank you to @cowtowncoder for maintaining the excellent [FasterXML/jackson](https://github.com/FasterXML/jackson) library for all this time.

