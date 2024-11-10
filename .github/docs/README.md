@DESCRIPTION@

```scala mdoc:toc
```

## Installation

Add the following line to your `build.sbt` file:

```sbt
libraryDependencies += "@ORGANIZATION@" %% "@NAME@" % "@VERSION@"
```

The library is published for Scala versions: @SUPPORTED_SCALA_VERSIONS@.

## Usage

This library provides `ConfigReader` instances for Doobie's `hikari.Config` as well as a new case class `DoobieConfig` that wraps `hikari.Config` and adds a few more fields like data-source properties.

To use it just add the following import to your project:

```scala
import doobie.pureconfig._
```

And use either `hikari.Config` or `DoobieConfig` in your configuration classes.

## Contributors to this project

@CONTRIBUTORS_TABLE@

[doobie]: https://typelevel.org/doobie/index.html
[pureconfig]: https://pureconfig.github.io