Pureconfig instances for Doobie's Transactor settings

---

- [Installation](#installation)
- [Usage](#usage)
- [Contributors to this project](#contributors-to-this-project)

## Installation

Add the following line to your `build.sbt` file:

```sbt
libraryDependencies += "com.permutive" %% "doobie-pureconfig" % "0.4.0"
```

The library is published for Scala versions: `2.13` and `3`.

## Usage

This library provides `ConfigReader` instances for Doobie's `hikari.Config` as well as a new case class `DoobieConfig` that wraps `hikari.Config` and adds a few more fields like data-source properties.

To use it just add the following import to your project:

```scala
import doobie.pureconfig._
```

And use either `hikari.Config` or `DoobieConfig` in your configuration classes.

## Contributors to this project

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |

[doobie]: https://typelevel.org/doobie/index.html
[pureconfig]: https://pureconfig.github.io