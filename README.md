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
        <version>1.4.1</version>
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
Gradle:
```gradle
repositories {
    maven {
        url 'https://repo.diorite.org/repository/diorite/'
    }
}

dependencies {
    compile group: 'com.gotofinal', name: 'diorite-configs-java8', version: '1.4.1'
}
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
import org.diorite.config.annotations.HelperMethod;
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
    
    @HelperMethod
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

## Features

### List of annotations
There is few annotations that can be used to control how given configuration option will be saved and/or accessed:

| Name                   | Example                                                                                                                                                                                                 | Meaning                                                                                                                                                                                                                                                                                                                                                                                                                   |
| ---------------------- |:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Comment                | `@Comment("comment")`<br>`int getX();`                                                                                                                                                                  | This comment will be added above given field in generated yaml<br>`# comment`<br>`x: 5`<br> This annotation can be also used inside non-config classes that are serialized to config values.                                                                                                                                                                                                                              |
| Header                 | `@Header({"line 1", "line 2"}})`<br>`public interface MyConfig`                                                                                                                                         | Adds header to generated yaml                                                                                                                                                                                                                                                                                                                                                                                             |
| Footer                 | `@Footer({"line 1", "line 2"}})`<br>`public interface MyConfig`                                                                                                                                         | Adds footer to generated yaml                                                                                                                                                                                                                                                                                                                                                                                             |
| PredefinedComment      | `@PredefinedComment(path = {"some", "path"}, value = "comment")`<br>`public interface MyConfig`                                                                                                         | Adds comment on given path, useful for commenting fields of serialized classes<br>`some:`<br>`  # comment`<br>`  path: 5`                                                                                                                                                                                                                                                                                                 |
| CustomKey              | `@CustomKey("some-name")`<br>`String getSomething();`                                                                                                                                                   | Change name of field used inside generated yaml. <br>`some-name: value` <br> Note that this only change config key name, setter for this value will still use real name: <br> `void setSomething(String value)`                                                                                                                                                                                                           |
| Unmodifiable           | `@Unmodifiable`<br>`Collection<X> getX();`                                                                                                                                                              | Collection returned by this getter is always unmodifiable                                                                                                                                                                                                                                                                                                                                                                 |  
| BooleanFormat          | `@BooleanFormat(trueValues = {"o"}, falseValues = {"x"})`<br>`boolean getX();`                                                                                                                          | Allows to use/support different text values as true/false values in config, while saving it will always use first value from list. <br>`x: o` = `x: true`                                                                                                                                                                                                                                                                 |
| HexNumber              | `@HexNumber`<br>`int getColor();`                                                                                                                                                                       | Value will be saved and loaded as hex value. <br> `color: 0xaabbcc`                                                                                                                                                                                                                                                                                                                                                       |
| PaddedNumber           | `@PaddedNumber(5)`<br>`int getMoney()`                                                                                                                                                                  | Used to save value with padding. <br> `money: 00030`                                                                                                                                                                                                                                                                                                                                                                      |
| Formatted              | `@Formatted("%.2f%n")`<br>`double getX();`                                                                                                                                                              | Uses java.util.Formatter to format value before saving (note that it might be impossible later to load it back, so it should be only used to choose number format like `0.00`) <br>for `x = 12.544444` -> `x: 12.54`                                                                                                                                                                                                      | 
| Property               | `@Property("y")`<br>`private int x() { return 0; }`                                                                                                                                                     | In java 9 it is used to define private properties, name of property is optional, if name is not present method name will be used instead. <br> Note that this is name of property, so setter for this value will looks like: <br> `void setY(int y)`<br> `CustomKey` annotation can be still combined with this property <br> In java 8 it still can be used on default methods to define properties with different name. |
| PropertyType           | `@PropertyType(CharSequence.class)`<br>`String getSomething();`                                                                                                                                         | Allows to select different serializer type for given property                                                                                                                                                                                                                                                                                                                                                             |
| CollectionType         | `@CollectionType(CharSequence.class)`<br>`List<? extends String> getSomething();`                                                                                                                         | Allows to select different serializer type for given property collection type                                                                                                                                                                                                                                                                                                                                             |
| MapTypes               | `@MapTypes(keyType = CharSequence.class)`<br>`Map<? extends String, Integer> getSomething();`                                                                                                           | Allows to select different serializer type for given property map type, note that you can choose if you want to provide type just for key, just for value or for both                                                                                                                                                                                                                                                     |
| GroovyValidator        | `@GroovyValidator(isTrue = "x > 0", elseThrow = "y can't be negative")`<br>`int getY()`                                                                                                                 | Allows to define validator using simple groovy expressions, note that you can place more than one annotation like that                                                                                                                                                                                                                                                                                                    |
| Validator              | `@Validator`<br>`static double xValidator(double x) {`<br>`    return x > 100 ? 100 : 0`<br>`}`                                                                                                         | Used to annotate validator methods, if name of method is `<property>Validator` then name of validator is optional, note that single validator method can be connected to more than one property <br> `@Validator({"x", "y"})` <br> Read more in validators section.                                                                                                                                                       |
| AsList                 | `@AsList(keyProperty = "uuid")`<br>`Map<UUID, SomeObject> getX()`                                                                                                                                       | Allows to save map of values as list using given property as key when loading. <br>`x: `<br>`- uuid: "uuid here"`<br>`  someField: value`<br>`- uuid: "next uuid"`<br>`  someField: next value`<br> Also this annotation like `MapTypes` allows to select types of keys and values in map (optional)                                                                                                                      |
| Mapped                 | `@Mapped()`<br>`Collection<SomeObject> getY()`                                                                                                                                                          | Allows to save collection as map, when using `Mapped` annotation it might be also required to also use `ToStringMapperFunction` annotation!                                                                                                                                                                                                                                                                               |
| ToStringMapperFunction | `@ToStringMapperFunction("x.uuid")`<br>`Collection<SomeObject> getY()`<br>OR<br>`@ToStringMapperFunction(property = "y")`<br>`static String mapper(SomeObject x) {`<br>`    return x.getUuid();`<br>`}` | Allows to choose how to select key for each element of list serialized as map when using `Mapped` annotation. It cn be used over property to provide groovy script, or over method that should be invoked to provide that value. <br> It can be also used on `Map` properties without `AsList` annotation to choose how key should be serialized and override default serialization code.                                 |
| ToKeyMapperFunction    | `@ToKeyMapperFunction("Utils.parse(x)")`<br>`Map<Z, SomeObject> getY()`<br>OR<br>`@ToKeyMapperFunction(property = "y")`<br>`static Z mapper(String x) {`<br>`    return Utils.parse(x);`<br>`}`         | Allows to choose how map key should be loaded, can be used to override default deserialization code                                                                                                                                                                                                                                                                                                                       |
| HelperMethod           | `@HelperMethod`<br>`default void someCustomMethod() {`<br>`    setX(0)`<br>`}`                                                                                                                          | Used to mark additional methods that should not be implemented by diorite config system, annotation is optional in most cases                                                                                                                                                                                                                                                                                             | 
| Serializable           | [**org/diorite/config/serialization/MetaObject**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/MetaObject.java)                        | Used to mark class as serializable and mark serializer/deserializer methods in it.                                                                                                                                                                                                                                                                                                                                        |
| StringSerializable     | [**org/diorite/config/serialization/MetaValue**](https://github.com/GotoFinal/diorite-configs-java8/blob/master/src/test/java/org/diorite/config/serialization/MetaValue.java)                          | Used to mark class as string serializable and mark string serializer/deserializer methods in it.                                                                                                                                                                                                                                                                                                                          |
| DelegateSerializable   | `DelegateSerializable(Player.class)`<br>`public class PlayerImpl implements Player`                                                                                                                     | Allow to register given class as serializable but using serializer of different type, so while registering class annotated with `DelegateSerializable` as `StringSerializable` system will look for `StringSerializable` annotations/methods/interface in provided class                                                                                                                                                  |
| SerializableAs         | `SerializableAs(Player.class)`<br>`public class PlayerImpl implements Player`                                                                                                                           | Allow to register given class as serializable but using different type, useful for separate implementation classes.                                                                                                                                                                                                                                                                                                       |


### Validators
There are 2 ways to create validator, using `@GroovyValidator` as in table above, or using custom `Validator` methods.  
Each property can have multiple validators of each type.  
By using custom methods you gain additional possibility of affecting property, as validator method can both throw some exception or change return value, like:  
```java
@Validator
default SomeType storageValidator(SomeType data)
{
    if (data == null) {
        return new SomeType();
    }
    if (data.something() > 100) {
        throw new RuntimeException("Too big");
    }
    return data;
}
```
`@Validator` can specify name of property that will be validated by it, or even validate more than one property: `@Validator({"x", "y"})`.  
If name of validator methods ends with `Validator` then beginning of method name will be used as property name if annotation does not define own names.  
Method that is used as validator must match one of this patterns and have @Validator annotation over it:
```java
private T validateName(T name) {...}
default T validateName(T name) {...}
private void validateSomething(T something) {...}
default void validateSomething(T something) {...}
static void validateAge(T age) {...}
static T validateNickname(ConfigType config, T nickname) {...}
static T validateNickname(T nickname, ConfigType config) {...}
```

### Comment handling
Using annotations to configure comments may look ugly in other classes than config itself, we can use the `PredefinedComment` annotations to setup comments, but it might look horrible if we have a lot of fields to process.  

This is why the diorite library provide multiple ways to provide own comment messages, all of them are based on special `org.diorite.config.serialization.comments.DocumentComments` class.  
One of the simplest thing to do, is just get an instance from a config template and add own comments:
```java
ConfigTemplate<SomeConfig> configTemplate = ConfigManager.get().getConfigFile(SomeConfig.class);
DocumentComments comments = configTemplate.getComments();
comments.setComment("some.path", "Comment on path");
comments.setFooter("New footer!");
```
We can also fetch comments from some class to use them for other purposes: (like joining, described below)
```java
DocumentComments someConfigComments = DocumentComments.parseFromAnnotations(SomeConfig.class);
```

But what if you have a list of elements? 
```yaml
listOfEntities:
- id: 4
  name: Steve
- id: 43
  name: Kate
  special: true
```
Just... ignore it:
```java
comments.setComment("people.name", "This comment will be above 'name' property of first entity in list.");
comments.setComment("people.little", "Moar comments");
```
And you will get:
```yaml
people:
- id: 4
  # This comment will be above 'name' property of first entity in list.
  name: Steve
- id: 43
  name: Kate
  # Moar comments
  little: true
```
(note that library does not duplicate comments in lists, and only place comment above first occurrence of given path.)
If you want to use a map like this:
```yaml
people:
  '4':
    # This comment will be above 'name' property of first entity in list.
    name: Steve
  '43':
    name: Kate
    # Moar comments
    little: true
```
Just use a `*` wildcard:
```java
comments.setComment("people.*.name", "This comment will be above 'name' property of first entity in list.");
comments.setComment("people.*.little", "Moar comments");
```

----  

But the best way to create comments are special yaml-like template files:
```yaml
# Header of file, first space of comments is always skipped, if you want indent a comment just use more spaces.
#     Like this.
#
#But first char don't need to be a space.

# This comment will appear over `node` path, yup, THIS one <-- this!
node: value is ignored

other:
  # this comment will be ignored, as it isn't above any property.

  # This comment will appear over `other.node` path
  node:
  
# Map of people
peopleMap:
  *:
    # This comment will be above 'name' property of first entity in list.
    name: Steve
    # Moar comments
    little: true
  
# List of people
peopleList:
  # This comment will be above 'name' property of first entity in list.
  name: Steve
  # Moar comments
  little: true
# Footer, just like header
```
It will just read the comments that are above all nodes and construct valid `DocumentComments` for you:
```java
DocumentComments comments = DocumentComments.parse(new File("mycomments")); // comments can be also loaded from InputStream or Reader
```
----

You can also combine multiple files or documents to one, so you can create a separate file for people comments and apply them on both `peopleMap` and `peopleList`:
```java
DocumentComments comments = DocumentComments.create();
comments.setComment("some.path", "Comment on path");
comments.setFooter("New footer!");
DocumentComments peopleComments = DocumentComments.parse(new File("people-comments"));
comments.join("peopleMap.*", peopleComments);
// or use existing node
comments.join("peopleList", comments.getNode("peopleMap.*"));
// or read from some annotations
comments.join("some.node", DocumentComments.parseFromAnnotations(SomeConfig.class));
```

End.
