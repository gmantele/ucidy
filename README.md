README
======

Preambule
---------

This GitHub repository contains the sources of a library aiming to validate any UCD (Unified Content Descriptor) as defined by the [IVOA](http://www.ivoa.net/ "International Virtual Observatory Alliance") standard: [The UCD1+ controlled vocabulary](http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html).

### Documentation
_Coming soon!_

### Java version
This library is developed using **Java 1.7**, but it should be compatible with no modification with _Java 1.6_, and with few modifications (just removing appropriate *@Override*) with _Java 1.5_.

### License
This library is under the terms of the [LGPL v3 license](https://www.gnu.org/licenses/lgpl.html). You can find the full description and all the conditions of use in the files src/COPYING and src/COPYING.LESSER.

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

If you are using Eclipse (or maybe also with another Integrated Development Environment), JUnit is generally already available. Then you can directly execute and compile the provided JUnit test files. So you do not need the two libraries mentioned just below.

Otherwise, you will need to get the JUnit library. Generally it is provided with the JDK, but you can find the corresponding JAR also on the [JUnit website](https://github.com/junit-team/junit4/wiki/Download-and-Install).

You may also need another library called `hamcrest`. You can find this one on its [Maven repository](http://search.maven.org/#search|ga|1|g%3Aorg.hamcrest) ; just to be sure to have everything needed, just take `hamcrest-all` as a JAR.

*__Note:__ The JUnit and Hamcrest libraries are not provided in this Git repository in order to avoid version incompatibility with the host system (i.e. your machine when you checkout/clone/fork this repository).*

### ANT scripts
At the root of the repository, there is an ANT script. It is able to generate JAR for sources, binaries and Javadoc.

Only one property must be set before using one of these scripts:
* `JUNIT-API` *not required if you are not interested by running the JUnit tests*: a path toward one or several JARs or binary directories containing all classes to use JUnit.

This ANT script have the following main targets:
* `junitValidation`: Executes all JUnit tests related to the target library and stop ANT at any error.
* `buildLib` *DEFAULT*: run the JUnit tests and if they are all successful, compile the library's classes and build a JAR file with them and the resources files. A similar JAR file is also generated, but as a runnable JAR executing the main function of ari.ucd.UCDParser. 
* `buildLibAndSrc`: same as `buildLib` + building of a JAR file containing all the sources.
* `buildJavadoc`: generate a JAR containing the Javadoc of the target library's classes.
* `buildAll`: equivalent of `buildLibAndSrc` and `buildJavadoc` together. The result is 4 JARs: one with the compiled classes, a runnable JAR, one with the corresponding sources and the last one with the Javadoc.
