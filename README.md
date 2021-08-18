![Logo](https://raw.githubusercontent.com/axelrindle/PocketKnife/master/logo.svg)

----

<p align="center">
  <a href="https://github.com/axelrindle/PocketKnife">
    <img src="https://github.com/axelrindle/PocketKnife/workflows/Test/badge.svg" alt="Test Status">
  </a>
  <a href="https://www.codacy.com/app/axelrindle/PocketKnife?utm_source=github.com&utm_medium=referral&utm_content=axelrindle/PocketKnife&utm_campaign=Badge_Grade">
    <img src="https://api.codacy.com/project/badge/Grade/44f2287392d3482c86bf467223f2e88a" alt="Codacy Badge">
  </a>
  <a href="https://www.codacy.com/app/axelrindle/PocketKnife?utm_source=github.com&utm_medium=referral&utm_content=axelrindle/PocketKnife&utm_campaign=Badge_Coverage">
    <img src="https://api.codacy.com/project/badge/Coverage/44f2287392d3482c86bf467223f2e88a" alt="Codacy Badge">
  </a>
  <a href="https://jitpack.io/#axelrindle/PocketKnife">
    <img src="https://jitpack.io/v/axelrindle/PocketKnife.svg" alt="JitPack Status">
  </a>
  <img src="https://img.shields.io/badge/Kotlin-1.5.10-7F52FF">
</p>

----

# PocketKnife

> :recycle: Utilities and reusable components to simplify the process of creating a Bukkit/Spigot plugin.

## Get Started

### Installation

If not already done, add the `JitPack` repository and the dependency:

#### Repository

```gradle
maven { url 'https://jitpack.io' }
```

#### Dependency

```gradle
implementation 'com.github.axelrindle:PocketKnife:LATEST_VERSION'
```

#### Package relocation

To avoid classpath conflicts with other plugins using the same dependencies, dependencies should be relocated to a unique package. More information [can be found here](https://imperceptiblethoughts.com/shadow/configuration/relocation/).

The following Gradle configuration relocates all dependencies using the `shadow` plugin, that will be included in the jar file, to a new location:

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '6.1.0'
}

shadowJar {
    archiveClassifier.set('') // remove the 'all' suffix
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
task relocateShadowJar(type: ConfigureShadowRelocation) {
    target = tasks.shadowJar
    prefix = "mypluginshadow" // Default value is "shadow"
}

tasks.shadowJar.dependsOn tasks.relocateShadowJar
```

Afterwards a jar file can be built by running the Gradle task `shadowJar`.

### Usage

Documentation is moving to [GitBook](https://axelrindle.gitbook.io/pocketknife/).

## Used by

- [Broadcaster](https://github.com/axelrindle/Broadcaster-Plugin)
- [SimpleCoins](https://github.com/axelrindle/SimpleCoins)

*Feel free to add your plugin here by sending a pull request.*

## License

[Apache-2.0](LICENSE)

### Copyright notices

- [pocket knife by teleymon from the Noun Project](https://thenounproject.com/icon/923802/)
