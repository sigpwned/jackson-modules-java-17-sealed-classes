# Jackson Modules - Java 17 Sealed Classes

`jackson-modules-java17-sealed-classes` is a [Jackson](https://github.com/FasterXML/jackson) module that adds improved support for polymorphic serialization of Java 17 sealed classes.

## Goals

* Add improved support for polymorphic serialization of Java 17 sealed classes

## Non-Goals

* Change any other aspects of Jackson serialization

## Java 17 Sealed Classes

Java 17 [sealed classes](https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html) allow users to declare class hierarchies with fixed members. This change combined with [switch pattern matching](https://docs.oracle.com/en/java/javase/17/language/pattern-matching-switch-expressions-and-statements.html) brings Java a big step closer to [algebraic types](https://en.wikipedia.org/wiki/Algebraic_data_type). A simple example of sealed classes is here:

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

The `SealedExample` class has exactly two child classes: `AlphaSealedExample` and `BravoSealedExample`, per its declaration. No other classes are allowed to extend `SealedExample`, by definition. In this example, `AlphaSealedExample` and `BravoSealedExample` are both `final`, so `SealedExample` is guaranteed not to have any other ancestor classes, but the feature does allow for more nesting.

## Examples

Without this module, sealed classes must use the `@JsonSubTypes` annotation like any other class, just like in [this example](https://www.baeldung.com/jackson-annotations). With this module, users get polymorphic serialization of sealed classes with only the the `@JsonTypeInfo` on the parent sealed class. Users can also add `@JsonTypeName` to child classes to customize subtype naming.

Adapting the above example:

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    public sealed class SealedExample permits AlphaSealedExample, BravoSealedExample {
    }
    
    @JsonTypeName("alpha")
    public final class AlphaSealedExample extends SealedExample {
    }
    
    @JsonTypeName("bravo")
    public final class BravoSealedExample extends SealedExample {
    }
    
Would result in the following JSON:

    serialize(new AlphaSealedExample().withAlpha("value")) → {"type":"alpha","alpha":"value"}

    deserialize({"type":"alpha","alpha":"value"}) → new AlphaSealedExample().withAlpha("value")
    
    serialize(new BravoSealedExample().withBravo("value")) → {"type":"bravo","bravo":"value"}
    
    deserialize({"type":"bravo","bravo":"value"}) → new BravoSealedExample().withBravo("value")
    
## Acknowledgements

Many thanks to @shartte for his work on [FasterXML/jackson-module-kotlin#239](https://github.com/FasterXML/jackson-module-kotlin/issues/239), where he implemented a very similar feature for Kotlin. And of course, thank you to @cowtowncoder for maintaining the excellent [FasterXML/jackson](https://github.com/FasterXML/jackson) library for all this time.

