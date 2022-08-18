[//]: # " Copyright (c) 2020, 2022 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0 which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0, "
[//]: # " or the Eclipse Distribution License v. 1.0 which is available at "
[//]: # " http://www.eclipse.org/org/documents/edl-v10.php. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause "

[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/eclipse-ee4j/eclipselink.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/eclipse-ee4j/eclipselink/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/eclipse-ee4j/eclipselink.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/eclipse-ee4j/eclipselink/alerts/)

EclipseLink master (3.1.x)
[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse.persistence/eclipselink.svg?versionPrefix=3.1&label=Maven%20Central)](https://mvnrepository.com/artifact/org.eclipse.persistence/eclipselink)
[![Jakarta Staging (Snapshots)](https://img.shields.io/nexus/s/https/jakarta.oss.sonatype.org/org.eclipse.persistence/eclipselink.svg)](https://jakarta.oss.sonatype.org/content/repositories/staging/org/eclipse/persistence/eclipselink)
[![Master Build Status](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-master/badge/icon)](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-master)

EclipseLink 3.0.0
[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse.persistence/eclipselink.svg?versionPrefix=3.0&label=Maven%20Central)](https://mvnrepository.com/artifact/org.eclipse.persistence/eclipselink)
[![3.0 Build Status](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-3.0/badge/icon)](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-3.0)

EclipseLink 2.7.x
[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse.persistence/eclipselink.svg?versionPrefix=2.7&label=Maven%20Central)](https://mvnrepository.com/artifact/org.eclipse.persistence/eclipselink)
[![2.7.x  Build Status](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-2.7/badge/icon)](https://ci.eclipse.org/eclipselink/job/eclipselink-nightly-2.7)


# EclipseLink

The EclipseLink project's goal is to provide a complete persistence framework that is both
comprehensive and universal. It will run in any Java environment and read and write objects
to virtually any type of data source, including relational databases, XML, JSON, or EIS systems.
EclipseLink will focus on providing leading edge support, including advanced feature extensions,
for the dominant persistence standards for each target data source;
Jakarta Persistence for relational databases, Jakarta XML Binding for XML,
Jakarta Connectors for EIS and other types of legacy systems, and Service Data Objects (SDO).


This project is part of [the EclipseLink project](https://projects.eclipse.org/projects/ee4j.eclipselink).


## License

EclipseLink is dual licensed under an [EPL 2.0](LICENSE.md) and [EDL 1.0](LICENSE.md) license.


## Contributing

We use [contribution policy](CONTRIBUTING.md), which means we can only accept contributions under
the terms of [Eclipse Contributor Agreement](http://www.eclipse.org/legal/ECA.php).


## Links

* [Home page](http://eclipse.org/eclipselink)
* [Documentation](https://www.eclipse.org/eclipselink/documentation/) 
* [Javadoc](https://javadoc.io/doc/org.eclipse.persistence/eclipselink/latest/index.html)
* [Mailing list](https://accounts.eclipse.org/mailing-list/eclipselink-users)
* [Wiki](https://wiki.eclipse.org/EclipseLink)  
* [Examples](https://wiki.eclipse.org/EclipseLink/Examples)
* [Nightly builds](https://www.eclipse.org/eclipselink/downloads/nightly.php)
* [Maven staging repository](https://jakarta.oss.sonatype.org/content/groups/staging/org/eclipse/persistence/)
* [Maven snapshots repository](https://jakarta.oss.sonatype.org/content/repositories/snapshots/org/eclipse/persistence/), another output from the nightly builds
* [EclipseLink Persistence Test Applications](https://github.com/eclipse-ee4j/eclipselink/tree/master/jpa/eclipselink.jpa.testapps/README.md)

## Issue/bug trackers

* [GitHub Issues](https://github.com/eclipse-ee4j/eclipselink/issues), this is preferred bug tracker
* [Eclipse.org Bugzilla](https://bugs.eclipse.org/bugs/buglist.cgi?product=EclipseLink)

Hint: If it's possible, it helps if some test reproducer is attached to the new bug.
If it's not possible, attached entities and persistence.xml file should help too for JPA related bugs.
For MOXy, entities and XML or JSON resources/files are welcome.
