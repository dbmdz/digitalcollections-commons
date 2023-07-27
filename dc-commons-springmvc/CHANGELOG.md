# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- `StringToOrderConverter` now allows `subProperty` containing dash "-" (e.g. "label_de-Latn")
- **Breaking** Bumped `dc-model` dependency to version `12`

## [6.0.0](https://github.com/dbmdz/digitalcollections-commons/releases/tag/dc-commons-springmvc-6.0.0) - 2022-07-18

### Changed

- **Breaking** Bumped `dc-model` dependency to version `11`

## [5.0.1](https://github.com/dbmdz/digitalcollections-commons/releases/tag/dc-commons-springmvc-5.0.1) - 2022-07-04

### Changed

- Updated some dependencies

## [5.0.0](https://github.com/dbmdz/digitalcollections-commons/releases/tag/dc-commons-springmvc-5.0.0) - 2022-06-07

### Added

- Added `ignorecase` to the conversion of order strings

### Changed

- **Breaking** Use `digitalcollections-model` version 10, which introduces breaking changes for all builders

## [4.2.0](https://github.com/dbmdz/digitalcollections-commons/releases/tag/dc-commons-springmvc-4.2.0) - 2022-04-12

### Added

- Added a Spring converter that converts instances of String to ones of [Order](https://github.com/dbmdz/digitalcollections-model/blob/main/dc-model/src/main/java/de/digitalcollections/model/paging/Order.java)
