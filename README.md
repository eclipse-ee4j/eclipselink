[//]: # " Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0 which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0, "
[//]: # " or the Eclipse Distribution License v. 1.0 which is available at "
[//]: # " http://www.eclipse.org/org/documents/edl-v10.php. "
[//]: # "  "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause "

EclipseLink 3.0.0 [![Master Build Status](https://travis-ci.org/eclipse-ee4j/eclipselink.svg?branch=master)](https://travis-ci.org/eclipse-ee4j/eclipselink)

EclipseLink 2.7.x [![2.7.x  Build Status](https://travis-ci.org/eclipse-ee4j/eclipselink.svg?branch=2.7)](https://travis-ci.org/eclipse-ee4j/eclipselink)


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


## Issue/bug trackers

* [GitHub Issues](https://github.com/eclipse-ee4j/eclipselink/issues), this is preferred bug tracker
* [Eclipse.org Bugzilla](https://bugs.eclipse.org/bugs/buglist.cgi?product=EclipseLink)

Hint: If it's possible, it helps if some test reproducer is attached to the new bug.
If it's not possible, attached entities and persistence.xml file should help too for JPA related bugs.
For MOXy, entities and XML or JSON resources/files are welcome.
