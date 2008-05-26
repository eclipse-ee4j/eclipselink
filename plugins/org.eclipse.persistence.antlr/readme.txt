This directory contains a renamed version of the ANTLR tool's runtime component.

ANTLR is used for JPQL compilation in EclipseLink.

Please do not change these files unless you are either upgrading the ANTLR version or applying a patch that absolutely must be made in ANTLR.  The goal is to keep ANTLR the same as a real ANTLR release from http://www.antlr.org/.  The current version is 3.0.

The following is checked in:

1. The source code - this can be used for debugging or to regenerate the ANTLR classes
2. The compiled classes - these are used to package in the eclipselink.jar file and our ANTLR bundle.
3. A PDE project - this can be used for OSGI integration
4. extra-src.jar - this contains some source files that are not included in eclipselink since they are not required at runtime and they depend on components that are shipped with ANTLR v3, that depend on parts of ANTLR that cannot be checked into Eclipse repositories (ANTLR v2 and the String Template library)

Replacing ANTLR requires to following steps
1. Get the new ANTLR source
2. Rename it using either the EclipseLink rename utility or some other utility.  The rename involves changing the package names from org.antlr.runtime to org.eclipse.persistence.internal.libraries.antlr.runtime
3. Compile the classes
4. Check in the new source and classes
