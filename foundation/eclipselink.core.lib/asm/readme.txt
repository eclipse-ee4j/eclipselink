This directory contains a renamed version of the ASM tool.

ASM is used for weaving within EclipseLink

Please do not change these files unless you are either upgrading the ASM version or applying a patch that absolutely must be made in ASM.  The goal is to keep ASM the same as a real ASM release from http://asm.objectweb.org/.  The current version is 1.6.3.

Note: Since it is not expected that people will make changes, the there is no Eclipse Project.  The following is checked in:

1. The source code - this can be used for debugging or to regenerate the ASM classes
2. The compiled classes - these are use to package in the eclipselink.jar file
3. A build script - this can be used to regenerate the class files from source if necessary.  It also generates a jar file and puts in this directory's parent directory.  If the classes are regenerated, that file should be checked in too.
4. eclipselink-asm.jar - the file referred to abvoe.  It is located in this directory's parent directory and used as a dependancy for compilation.

Replacing ASM requires to following steps
1. Get the new ASM source
2. Rename it using either the EclipseLink rename utility or some other utility.  The rename involves changing the package names from org.asm to org.eclipse.persistence.internal.libraries.asm
3. Compile the classes
4. Check in the new source and classes
5. Check in the new eclipselink-asm.jar