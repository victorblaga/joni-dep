# joni-dep
**Joni Dep** is a simple Java dependency injection framework.

### Motivation
Joni Dep is really frustrated with Java DI frameworks.
They are too bloated and difficult to use, because they have too much unnecessary functionality.
It's difficult to debug and understand what goes on in the background.
_"Automagic is bad"_ and _"it could be much simpler"_, 
Joni Dep thinks while he `@Autowire`s his `@Component`s.

Can he do any better?

### Philosophy
Joni Dep doesn't believe in autowiring.
He thinks it is a bad practice for many reasons:

 - It's too much magic, and Joni Dep has a hard time debugging and understanding his code when something goes wrong.
 - Joni Dep likes the freedom of changing his mind, but the frameworks force him to use specific annotations in his business-layer code, locking him into a particular vendor. Once the annotations are there, it's impossible to change the DI framework without touching the entire codebase.
 - Joni Dep thinks that classes with DI annotations go against the logic of the dependency injection pattern. The whole point of the pattern is that classes should not care how they are created, but the annotations do just that: they describe a creation pattern.
 - Joni Dep is a strong believer in the separation of concerns principle, which applies to object creation as well. He prefers things that do the same thing to be in the same place, rather than spread around all over the project.
 - Business objects shouldn't depend on non-business code. Period. Dependency injection is a non-business detail that should plug into your business layer, and not the other way around.

### How to use
Given the following buinsess components:

```java
package org.business;

public interface FirstService {}
public interface SecondService {}
public interface ThirdService {}

public class FirstServiceImpl implements FirstService {
	public FirstServiceImpl(SecondService secondService) {}
	public FirstServiceImpl() {}
}

public class SecondServiceImpl implements SecondService {
	public SecondServiceImpl(ThirdService thirdService) {}
}

public class ThirdServiceImpl implements ThirdService {
	public ThirdServiceImpl(FirstService firstService) {}
}
```

In a separate package, add some custom containers that handle their creations:

```java
package org.containers;

public class OneContainer extends CustomContainer {
	public OneContainer(Config config) {
		super(config);
	}
	
	public FirstService getFirstService() {
		return new FirstServiceImpl(
			get(SecondService.class)
		);
	}
	
	@Qualifier("simple")
	public FirstService getSimpleFirstService() {
		return new FirstServiceImpl();
	}
	
	public SecondService getSecondService() {
		return new SecondServiceImpl(
			get(ThirdService.class);
		);
	}
}

public class SecondContainer extends CustomContainer {
	public SecondContainer(Config config) {
		super(config);
	}
	
	public ThirdService getThirdService() {
		return new ThirdServiceImpl(
			get(FirstService.class, "simple")
		);
	}
}
```

Tell Joni Dep the name of the package where the containers are located and let him do the work for you:

```java
public class Application {
	public static void main(String[] args) {
		Config config = ConfigFactory.load(); // more details coming up
		Container container = new ContainerBuilder(config, "org.containers").build();
		
		FirstService firstService = container.get(FirstService.class);
	}
}
```

### How to write a container

- Extend from `CustomContainer`.
- For each component that you want to create, add a public method with no parameters that knows how to create it.
- Optionally annotate this method with a `@Qualifier("name")`;
by default, the component is qualified as "primary".
- If the component you are creating has dependencies created by another container,
call the `get(Dependency.class, "qualifier")` method. 
If you leave out the qualifier, by default it will be `"primary"`.
- If you need to inject values, retrieve them from the `config` protected field.

```java
public class MyContainer extends CustomContainer {
	public MyContainer(Config config) {
		super(config);
	}
	
	public Service getService() {
		return new ServiceImpl(
			get(Dependency1.class),
			get(Dependency2.class, "qualifier"),
			config.getString("param1")
		);
	}
}
```
In order for the above example to work, you need to have containers that create `Dependency1` and `Dependency2`:

```java
public class SecondaryContainer extends CustomContainer {
	public SecondaryContainer(Config config) {
		super(config);
	}
	
	public Dependency1 getDependency1() {
		return new Dependency1Impl(...);
	}
	
	@Qualifier("qualifier")
	public Dependency2 getDependency2() {
		return new Dependency2Impl(...);
	}
}
```


### More about configuration

Joni Dep uses the [Typesafe Config](https://lightbend.github.io/config/) library.
Joni Dep also adds a `ConfigParser` that adds a few conventions:

- You can manually add a configuration entry by calling the `entry` method:

```java
Config config = new ConfigParser().entry("key", "value").parse();
System.out.println(config.getString("key"));
// will print out -> "value"
```

- All the args that are passed to the `parse` method will be available in the config instance
under the namespace of `args.`

```java
String[] args = "--key1=value1 --key2 value2 --key3 --key4 10".split("\\s");
Config config = new ConfigParser().parse(args);
System.out.println(config.getString("args.key1"));
// will print out -> "value1"
System.out.println(config.getString("args.key2"));
// will print out -> "value2"
System.out.println(config.getBoolean("args.key3"));
// will print out -> true
System.out.println(config.getInt("args.key4"));
// will print out -> 10
```

- By default, as per the conventions of typesafe config, the `ConfigParser` looks for a `application.conf` and/or `reference.conf`
on the classpath.
- However, this behaviour can be overriden by supplying a `--resources` and/or `--files` arg.
The `resources` should point to a list of resource configuration names. 
The `files` should point to a list of configuration filesystem paths.
The order is `resources` first and `files` last.

```java
String[] args = "--resources=resource1.conf,resource2.conf --files /path/to/file1.conf,/path/to/file2.conf".split("\\s");
Config config = new ConfigParser().parse(args);
```

### How to use joni-dep in your project

#### Gradle
Add the following to `gradle.build`:

```groovy
repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile 'joni.dep:joni-dep:0.1.0.BETA'
}
```

#### Maven
```pom
<repositories>
    <repository>
        <snapshots>
	    <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>bintray</name>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>

...

<dependencies>
    <dependency>
        <groupId>joni.dep</groupId>
        <artifactId>joni-dep</artifactId>
        <version>0.1.0.BETA</version>
        <type>pom</type>
    </dependency>
</dependencies>
```

#### That's it
Don't forget to check out the tests and the [example project](https://github.com/victorblaga/joni-dep-example).
