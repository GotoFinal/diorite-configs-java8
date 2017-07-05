# Diorite Configs
#### For java 8!
Special clone of diorite configs that supports java 8 (instead of 9) and use limited amount of dependencies, including support for older versions of snakeyaml.

## Installation
Maven:
```xml
<dependencies>
    <dependency>
        <groupId>com.gotofinal</groupId>
        <artifactId>diorite-configs-java8</artifactId>
        <version>1.2</version>
    </dependency>
</dependencies>
<repositories>
    <repository>
        <name>Diorite repository</name>
        <id>diorite</id>
        <url>https://repo.diorite.org/repository/diorite/</url>
    </repository>
</repositories>
```
Jenkins: https://diorite.org/jenkins/job/diorite-configs-java8/

Also configs depends on *diorite-commons-java8, snakeyaml, gson, apache commons lang3* and *groovy*, you must ensure that these libraries are available on classpath. (you can shade them with your project, note that spigot already contains snakeyaml, commons and gson libraries, PS: this library is NOT depending on spigot or any other minecraft related library)  
Additionally some utilities and/or serializers have support for *fastutil* and *vecmath* libraries, but they are optional.

## Usage
Basic idea of diorite configs is to represent configuration files as simple interfaces like:
```java
import java.util.List;
import java.util.UUID;

import org.diorite.config.Config;
import org.diorite.config.annotations.Comment;
import org.diorite.config.annotations.Footer;
import org.diorite.config.annotations.GroovyValidator;
import org.diorite.config.annotations.Header;
import org.diorite.config.annotations.Unmodifiable;
import org.diorite.config.annotations.Validator;

@Header("Comment on the top of the file")
@Footer("Comment on the bottom of the file")
public interface MyAppConfig extends Config
{
    @Comment("This is app id, this comment will appear above field for this settings in configuration file.")
    default UUID getAppId() { return UUID.randomUUID(); } // randomly generated UUID as default value

    @GroovyValidator(isTrue = "x > 0", elseThrow = "MaxRequestsPerSecond can't be negative")
    @GroovyValidator(isTrue = "x == 0", elseThrow = "MaxRequestsPerSecond can't be zero")
    default int getMaxRequestsPerSecond() { return 5; }
    void setMaxRequestsPerSecond(int newValue);
    void addMaxRequestsPerSecond(int toAdd);

    @Validator("maxRequestsPerSecond")
    static int maxRequestsPerSecondValidator(int value) {
        if (value > 100) {
            return 100;
        }
        return value;
    }

    @Unmodifiable // so returned list is immutable
    List<? extends UUID> getAPIIds();
    boolean containsInAPIIds(UUID uuid);
    boolean removeFromAPIIds(UUID uuid);
    
    default void someCustomMethod() {
        addMaxRequestsPerSecond(1);
    }
    // and much more
}
```
Then you can simply create instance of that interface and start using it!
```java
MyAppConfig config = ConfigManager.createInstance(MyAppConfig.class);
config.bindFile(new File("someFile"));
config.load();

UUID appId = config.getAppId();
config.removeFromAPIIds(UUID.nameUUIDFromBytes("Huh".getBytes()));

config.save();
config.save(System.out); // there are methods to load/save config to/from other sources too.
``` 

Table of available methods that will be auto-implemented by diorite config system:  
(for simplicity regex of property name `(?<property>[A-Z0-9].*)` was replaced by `<X>` and `(?<property>[A-Z0-9].*?)` was replaced by `<X?>`)
#### For any type
| Name      | Patterns                                                                               | Examples                                         | Return value                        |
| --------- |:--------------------------------------------------------------------------------------:|:------------------------------------------------:| ----------------------------------- |
| Get       | `get<X>`                                                                               | `int getMoney()`                                 | required: define field type         |  
| Set       | `set<X>`                                                                               | `void setMoney(int v)`<br>`int setMoney(byte v)` | optional, returns previous value    |  
| Equals    | `isEqualsTo<X>`<br>`areEqualsTo<X>`<br>`<X>isEqualsTo`<br>`<X>areEqualsTo`             | `boolean isEqualsToMoney(int v)`                 | required: boolean type              |
| NotEquals | `isNotEqualsTo<X>`<br>`areNotEqualsTo<X>`<br>`<X>isNotEqualsTo`<br>`<X>areNotEqualsTo` | `boolean moneyIsNotEqualsTo(int v)`              | required: boolean type              |

#### For numeric values
| Name      | Patterns                                                 | Examples                                                      | Return value                     |
| --------- |:--------------------------------------------------------:|:-------------------------------------------------------------:| -------------------------------- |
| Add       | `(?:add<X>)`<br>`(?:increment<X?>(?:By)?)`               | `void addMoney(int x)`<br>`int incrementMoney(byte x)`        | optional, returns previous value |
| Subtract  | `(?:subtract(?:From)?<X>)`<br>`(?:decrement<X?>(?:By)?)` | `void decrementMoneyBy(int x)`<br>`int subtractMoney(byte x)` | optional, returns previous value |
| Multiple  | `(?:multiple\|multi)<X?>(?:By)?)`                        | `void multipleMoneyBy(double x)`<br>`int multiMoney(byte x)`  | optional, returns previous value |
| Divide    | `(?:divide\|div)<X?>(?:By)?`                             | `void divMoneyBy(int x)`<br>`int divideMoney(float x)`        | optional, returns previous value |
| Power     | `(?:power\|pow)<X?>(?:By)?`                              | `void powerMoney(int x)`<br>`int powMoneyBy(double x)`        | optional, returns previous value |

#### For collections
(both maps and lists)  
Example properties used for examples in table:
```java
List<? extends UUID> getIds();
Map<String, UUID> getApis();
```

| Name            | Patterns                                                                                                                                                | Examples                                                                                                                                                  | Return value                                                     |
| --------------- |:-------------------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------:| ---------------------------------------------------------------- |
| GetFrom         | `getFrom<X>`<br>`get<X?>By`                                                                                                                             | `UUID getFromIds(int index)`<br>`UUID getFromApisBy(String name)`                                                                                         | required: collection value type                                  |
| AddTo           | `(?:addTo\|putIn)<X>`                                                                                                                                   | `boolean addToIds(UUID x)`<br>`void addToIds(UUID... ids)`<br>`UUID putInApis(String k, UUID v)`<br>`void putInApis(Entry<String, UUID>...)`              | optional, returns result of `.add`/`.put` operation              |
| RemoveFrom      | `removeFrom<X>`                                                                                                                                         | `boolean removeFromIds(UUID id)`<br>`Collection<UUID> removeFromApis(String... keys)`<br>`UUID removeFromApis(String key)`                                | optional, `true/false` for collection and removed value for maps |
| SizeOf          | `(?:sizeOf)<X>`<br>`<X?>(?:Size)`                                                                                                                       | `int sizeOfIds()`<br>`int apisSize()`                                                                                                                     | required: numeric type                                           |
| IsEmpty         | `(?:is)<X?>(?:Empty)`                                                                                                                                   | `boolen isIdsEmpty()`<br>`boolean isApisEmpty()`                                                                                                          | required: boolean type                                           |
| ContainsIn      | `(?:contains(?:Key?)(?:In?)\|isIn)<X>`<br>`(?:contains(?:In?)\|isIn)<X>`<br>`(?:contains\|isIn)<X>`                                                     | `boolen containsKeyInApis(String key)`<br>`boolean isInApis(String... keys)`<br>`boolean isInIds(UUID uuid)`<br>`boolean containsIds(UUID... uuid)`       | required: boolean type                                           |
| NotContainsIn   | `(?:(notContains\|excludes)(?:Key?)(?:In?)\|isNotIn)<X>`<br>`(?:(notContains\|excludes)(?:In?)\|isNotIn)<X>`<br>`(?:notContains\|excludes\|isNotIn)<X>` | `boolen excludesKeyInApis(String key)`<br>`boolean excludes(String... keys)`<br>`boolean isNotInIds(UUID uuid)`<br>`boolean notContainsIds(UUID... uuid)` | required: boolean type                                           |
| RemoveFromIf    | `removeFrom<X?>If`<br>`remove<X?>If`                                                                                                                    | `void removeApisIf(BiPredicate<UUID, String> test)`<br>`boolean removeFromIdsIf(Predicate<UUID> test)`                                                    | optional: boolean type                                           |
| RemoveFromIfNot | `removeFrom<X?>IfNot`<br>`remove<X?>IfNot`                                                                                                              | `void removeFromApisIfNot(Predicate<Entry<UUID, String>> test)`<br>`boolean removeIdsIfNot(Predicate<UUID> test)`                                         | optional: boolean type                                           |


## Implementing serialization
Some classes can't be serialized by default (library is able to serialize anything that json/snakeyaml is able by default + deserialization of yaml is a bit enchanted to support even more types by default), then additional serializer needs to be registered.  
To see examples of serializers just go to: [**diorite-configs-java8/org/diorite/config/serialization**](https://github.com/GotoFinal/diorite-configs-java8/tree/master/src/test/java/org/diorite/config/serialization)  

* As string serialization: [**org/diorite/config/serialization/MetaValue**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/MetaValue.java)
* Serialization of map wrapper: [**org/diorite/config/serialization/SomeProperties**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/SomeProperties.java)
* Serialization with selecting subtype based on configuration fields: [**org/diorite/config/serialization/AbstractEntityData**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/AbstractEntityData.java) + [**CreeperEntityData**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/CreeperEntityData.java) + [**SheepEntityData**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/SheepEntityData.java)
* Registering serializers: [**SerializationTest#prepareSerialization**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/SerializationTest.java#L49)
* Serializers can be also registered for existing types.
