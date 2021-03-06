All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Dates are displayed in the format `DD.MM.YYYY`.

## 2.1.0 `(06.03.2021)`

### Added

- Additional localization files can be loaded additionally to the supported ones, for example for testing. They just have to be placed inside the `lang/` directory and filled with data

### Changed

- The API will not be included as a standalone plugin anymore, but instead be included directly into the plugin's jar file. To avoid classpath conflicts, consider [relocating packages](https://imperceptiblethoughts.com/shadow/configuration/relocation/).

### Deprecated

- The `overrideDefault` parameter in `PocketLang#init` is rather useless. Changes to the defaults should be made directly to the files by users

## 2.0.0 `(09.07.2020)`

## 1.3.0 `(16.06.2019)`

### Added

- Fully functional tab completion
- Commands can automatically check whether the sender is a Player
- Commands can be handled when no matching subcommand is found
- `ChatUtils`, `LocationUtils`
- Kotlin extension functions

## 1.2.1 `(09.09.2018)`

### Fixed

- Check for subcommands if arguments are present

- Subcommands are given a wrong arguments sub array

- PocketConfig creates any parent directories

## 1.2.0 `(25.08.2018)`

### Fixed

- Subcommands now test permissions
- `PocketCommand#getPermission` can return `null`
- Methods that access the `pluginCommand` in commands can be overridden by subcommands

## 1.1.0 `(11.08.2018)`

### Changed

- The API will be loaded by an own plugin on a server to avoid classpath conflicts

## 1.0.0 `(18.06.2018)`

Initial release :tada:
