# kolich-bolt

*synonym: lock*

A wrapper around Java's <a href="http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html">ReentrantReadWriteLock</a> with better wait and cleaner fail support.

Wraps a typical <a href="http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html">ReentrantReadWriteLock</a> with a much cleaner usage pattern.  And, lets you define a **success** callback and have it called upon successful execution of an internal transaction (a critical block that's protected by the lock).

## Latest Version

The latest stable version of this library is <a href="http://markkolich.github.com/repo/com/kolich/kolich-bolt/0.0.4">0.0.4</a>.

## Resolvers

If you wish to use this artifact, you can easily add it to your existing Maven or SBT project using <a href="https://github.com/markkolich/markkolich.github.com#marks-maven2-repository">my GitHub hosted Maven2 repository</a>.

### SBT

```scala
resolvers += "Kolich repo" at "http://markkolich.github.com/repo"

val kolichBolt = "com.kolich" % "kolich-bolt" % "0.0.4" % "compile"
```

### Maven

```xml
<repository>
  <id>Kolichrepo</id>
  <name>Kolich repo</name>
  <url>http://markkolich.github.com/repo/</url>
  <layout>default</layout>
</repository>

<dependency>
  <groupId>com.kolich</groupId>
  <artifactId>kolich-bolt</artifactId>
  <version>0.0.4</version>
  <scope>compile</scope>
</dependency>
```

## Usage

Your entity, a class, implements the <a href="https://github.com/markkolich/kolich-bolt/blob/master/src/main/java/com/kolich/bolt/LockableEntity.java">LockableEntity</a> interface as defined by *kolich-bolt*.

```java
import com.kolich.bolt.LockableEntity;
import java.util.concurrent.locks.ReadWriteLock;

public final class Foobar implements LockableEntity {
  private final ReadWriteLock lock_;
  public Foobar() {
    lock_ = new ReadWriteLock();
  }
  @Override
  public ReadWriteLock getLock() {
    return lock_;
  }
}
```

You wish to use an instance of this entity to protect a critical section of code (defined below as a `transaction`) using a <a href="https://github.com/markkolich/kolich-bolt/blob/master/src/main/java/com/kolich/bolt/ReentrantReadWriteEntityLock.java">ReentrantReadWriteEntityLock</a>.

```java
public static final Foobar x = new Foobar();
```

Grab a **shared** read lock on `x`, waiting forever on any threads who have already acquired the write lock.  If the read lock is not available then the current thread becomes disabled for thread scheduling purposes and lies dormant until the lock has been acquired.

```java
new ReentrantReadWriteEntityLock<T>(x) {
  @Override
  public T transaction() throws Exception {
    // Do something with "x"
    // Return something type T
    return baz;
  }
}.read(); // Wait forever
```

Grab a **shared** read lock on `x`, fail immediately with a <a href="https://github.com/markkolich/kolich-bolt/blob/master/src/main/java/com/kolich/bolt/exceptions/LockConflictException.java">LockConflictException</a> if the write lock already acquired by another thread.

```java
new ReentrantReadWriteEntityLock<T>(x) {
  @Override
  public T transaction() throws Exception {
    // Do something with "x"
    // Return something of type T
    return baz;
  }
}.read(false); // Fail immediately if read lock not available
```

Note that `read()` asks for a **shared** reader lock &mdash; the lock will be granted if and only if there are no threads holding a write lock on `x`.  There very well may be other reader threads.

Grab an **exclusive** write lock on `x`, fail immediately with a <a href="https://github.com/markkolich/kolich-bolt/blob/master/src/main/java/com/kolich/bolt/exceptions/LockConflictException.java">LockConflictException</a> if write or read lock already acquired by another thread.  Call the `success` callback method if and only if the `transaction` method finished cleanly without exception.

```java
new ReentrantReadWriteEntityLock<T>(x) {
  @Override
  public T transaction() throws Exception {
    // Do something with "x"
    // Return something of type T
    return baz;
  }
  @Override
  public T success(final T t) throws Exception {
    // Yay, it worked!
    // Only called if transaction() finished cleanly without exception
    return t;
  }
}.write(); // Fail immediately if write lock not available
```

**NOTE:** The acquired lock is held during execution of the `success` method.  It is only released when the `success` method returns, either in success or failure (throws an exception).

The <a href="https://github.com/markkolich/havalo">Havalo project</a> makes extensive real-world use of this locking mechanism.

## Building

This Java library and its dependencies are built and managed using <a href="https://github.com/harrah/xsbt">SBT (the Simple Build Tool)</a> version **0.12.1**.

To clone and build kolich-bolt, you must have <a href="http://www.scala-sbt.org/release/docs/Getting-Started/Setup">SBT 0.12.1 installed and configured on your computer</a>.

The kolich-bolt SBT <a href="https://github.com/markkolich/kolich-bolt/blob/master/project/Build.scala">Build.scala</a> file is highly customized to build and package this Java artifact.  It's written to manage all dependencies and versioning.

To build, clone the repository.

    #~> git clone git://github.com/markkolich/kolich-bolt.git

Run SBT from within kolich-bolt.

    #~> cd kolich-bolt
    #~/kolich-bolt> sbt
    ...
    kolich-bolt:0.0.4>

You will see a `kolich-bolt` SBT prompt once all dependencies are resolved and the project is loaded.

In SBT, run `package` to compile and package the JAR.

    kolich-bolt:0.0.4> package
    [info] Compiling 4 Java sources to ~/kolich-bolt/target/classes...
    [info] Packaging ~/kolich-bolt/dist/kolich-bolt-0.0.4.jar ...
    [info] Done packaging.
    [success] Total time: 4 s, completed

Note the resulting JAR is placed into the **kolich-bolt/dist** directory.

To create an Eclipse Java project for kolich-bolt, run `eclipse` in SBT.

    kolich-bolt:0.0.4> eclipse
    ...
    [info] Successfully created Eclipse project files for project(s):
    [info] kolich-bolt

You'll now have a real Eclipse **.project** file worthy of an Eclipse import.

Note your new **.classpath** file as well &mdash; all source JAR's are fetched and injected into the Eclipse project automatically.

## Licensing

Copyright (c) 2012 <a href="http://mark.koli.ch">Mark S. Kolich</a>

All code in this artifact is freely available for use and redistribution under the <a href="http://opensource.org/comment/991">MIT License</a>.

See <a href="https://github.com/markkolich/kolich-bolt/blob/master/LICENSE">LICENSE</a> for details.
