[![Build Status](https://travis-ci.org/gmantele/ucidy.svg?branch=master)](https://travis-ci.org/gmantele/ucidy)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](http://www.gnu.org/licenses/lgpl-3.0)

README
======

Preamble
---------

This GitHub repository contains the sources of a library aiming to validate any
UCD (Unified Content Descriptor). This current version aims to respect as much
as possible the definition provided by the [IVOA](http://www.ivoa.net/ "International Virtual Observatory Alliance")
standard: [An IVOA Standard for Unified Content Descriptors - Version 1.10](https://ivoa.net/documents/cover/UCD-20050812.html).
The parser is by default configured with the list of all validated UCD words
listed in [The UCD1+ controlled vocabulary 1.6](https://ivoa.net/documents/UCD1+/20241218).

### Functionalities

* Check whether each UCD word is:
  - syntactically valid
  - recognised (i.e. the word is among a list of well known UCD words)
  - recommended by the IVOA ([The UCD1+ controlled vocabulary 1.6](https://www.ivoa.net/documents/UCD1+/20241218))
* Possibility to customise the list of known UCD words
  _(by default all validated UCD1+ are automatically loaded)_
* Validate a full UCD with
  - a list of human-readable errors
  - an automatic correction suggestion (particularly for typo)
  - a list of advice to improve the readability of the UCD
* Detection of deprecated UCD words _(when detected a clear error message is
  returned and a correction suggestion is proposed)_
* Different ways to search UCD words
  - exact match
  - starting with
  - closest match _(take into account possible typo)_
* Support namespace prefix

### Java version

This library is developed using **Java 1.8**
_(should be compatible with Java 1.8 or newer)_.

### Download

The compiled JAR, the runnable JAR, the sources and the Javadoc API are
available on GitHub for [all releases](https://github.com/gmantele/ucidy/releases)
and especially for the [latest one](https://github.com/gmantele/ucidy/releases/latest).

### Documentation

* [Javadoc](https://gmantele.github.io/ucidy/)
* [UML](https://github.com/gmantele/ucidy/blob/master/uml/ari_ucidy.jpg)
* [Documentation/Wiki](https://github.com/gmantele/ucidy/wiki)

### License

This library is under the conditions of the LPGL-v3. See
[COPYING.LESSER](https://github.com/gmantele/ucidy/blob/master/COPYING.LESSER)
and [COPYING](https://github.com/gmantele/ucidy/blob/master/COPYING) for more
details. 

Collaboration
-------------

I strongly encourage you **to declare any issue you encounter**
[here](https://github.com/gmantele/ucidy/issues). Thus, anybody who has the same
problem can see whether his/her problem is already known. If the problem is
known the progress and/or comments about its resolution will be published.

In addition, if you have forked this repository and made some corrections on
your side which are likely to interest any other user of the libraries, please,
**send a pull request** [here](https://github.com/gmantele/ucidy/pulls). Provided these modifications are in
accordance with the IVOA definition and do not relate specifically to your use
case, they will be integrated (maybe after some modifications) in this
repository and thus will be made available to everybody.

Compilation
-----------

Both the management of dependencies and compilation are possible thanks to
Gradle. However, to compile or to run tests, no need to install Gradle. Just
use the `gradlew` (or `gradlew.bat` on Windows).

To generate the JAR file:

```bash
./gradlew jar
```

The JAR file will be available in the directory `lib/build/libs/`.

To run all tests:

```bash
./gradlew test
```

Tests results will be available in the directory `lib/build/test-results/`.

Repository content
------------------

### Dependencies

_No dependency._

### Resources

The directory `lib/src/main/resources/` contains two files for the moment:
* `ucd1p-words.txt`. It lists all official IVOA UCD1+ words as provided
  at <https://www.ivoa.net/documents/UCDlist/20241218/ucd-list.txt>.
* `ucd1p-deprecated.txt`. This is a list of all _deprecated_ UCD1+ words as
  provided at <https://www.ivoa.net/documents/UCDlist/20241218/ucd-list-deprecated.txt>.

These files are loaded by the default parser initialised in the class UCDParser.

If the file `ucd1p-words.txt` is renamed or removed, the default parser will
raise a warning on the standard error output and will be initialized with an
empty list of known UCD1+ words. Consequently, any UCD parsed using this parser
will be systematically flagged as _not recognised_ and so _not recommended_.

### Tests

The sources of these three libraries come with some JUnit test files. You can
find them in the directory `lib/src/main/test/java/`.
