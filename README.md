README
======

Preambule
---------

This GitHub repository contains the sources of a library aiming to validate any UCD (Unified Content Descriptor). This current version aims to respect as much as possible the definition provided by the [IVOA](http://www.ivoa.net/ "International Virtual Observatory Alliance") standard: [An IVOA Standard for Unified Content Descriptors - Version 1.1](http://www.ivoa.net/documents/cover/UCD-20050812.html). The parser is by default configured with the list of all validated UCD words listed in [The UCD1+ controlled vocabulary](http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html).

### Documentation
_Coming soon!_

### Java version
This library is developed using **Java 1.7**, but it should be compatible with no modification with _Java 1.6_, and with few modifications (just removing appropriate *@Override*) with _Java 1.5_.

Collaboration
-------------

I strongly encourage you **to declare any issue you encounter** [here](https://github.com/gmantele/ucdvalidator/issues). Thus anybody who has the same problem can see whether his/her problem is already known. If the problem is known the progress and/or comments about its resolution will be published.

In addition, if you have forked this repository and made some corrections on your side which are likely to interest any other user of the libraries, please, **send a pull request** [here](https://github.com/gmantele/ucdvalidator/pulls). If these modifications are in adequation with the IVOA definition and are not too specific to your usecase, they will be integrated (maybe after some modifications) on this repository and thus made available to everybody.

Repository content
------------------

### Dependencies

_No dependency._

### Resources

The `resources` directory contains just one file for the moment: `ucd1p-words.txt`. It lists all official IVOA UCD1+ words as provided at http://cdsweb.u-strasbg.fr/UCD/ucd1p-words.txt.

This file is loaded by the default parser initialised in the class UCDParser. If the name is changed or if the file is removed, the default parser will raise a warning on the standard error output and will be initialized with an empty list of known UCD1+ words. Consequently any UCD parsed using this parser will be systematically flagged as _not recognised_ and so _not recommended_.

### JUnit

The sources of these three libraries come with some JUnit test files. You can find them in the `test` directory.

In the `test-lib` directory, you will find all JAR files needed to compile and run these JUnit tests. You can use the task `test` of the ANT script as explained below.

### ANT scripts
At the root of the repository, there is an ANT script. It is able to generate JAR for sources, binaries and Javadoc.

This ANT script have the following main targets:
* `build`: Compile all classes of this project.
* `test` *DEFAULT*: Compile all classes and run all the JUnit tests.
* `javadoc`: Generate the Javadoc.
* `publish`: Compile all classes and run all the JUnit tests. If these latter are passed, the library JAR and runnable JAR are generated, in addition of a JAR containing all the sources and of another with the complete Javadoc.
