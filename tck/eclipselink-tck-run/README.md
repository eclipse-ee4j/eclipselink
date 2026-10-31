[//]: # " Copyright (c) 2026 Contributors to the Eclipse Foundation. "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0, which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0. "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 "

# Running the current Persistence TCK

The EclipseLink build and this runner use the Persistence `4.0.0-SNAPSHOT` API.
The runner also uses the `4.0.0-SNAPSHOT` TCK, with JUnit 6 and the common test
framework dependency selected by that TCK. Build the current API/TCK sources with
JDK 25; the test JVM may use JDK 21 or JDK 25.

Build the API and TCK from an updated local checkout of `jakartaee/persistence`:

```sh
mvn -pl tck/spec-tests,tck/procedures -am -DskipTests install
```

Build EclipseLink from its repository root, then run this module against a
PostgreSQL test database. The normal Maven repository is sufficient; no separate
repository or copied provider jars are needed.

```sh
mvn -pl jpa/org.eclipse.persistence.jpa,jpa/org.eclipse.persistence.jpa.modelgen,moxy/org.eclipse.persistence.moxy -am -DskipTests install
mvn -f tck/eclipselink-tck-run/pom.xml verify -Ppostgres-start,test1,test2
```

To select a different test JVM, add `-Djvm=/path/to/jdk/bin/java`.

The `postgres-start` profile creates a disposable local PostgreSQL instance.
Omit that profile when using an existing test database, and supply its JDBC
configuration externally. Run this module directly when using locally built
snapshots; the sibling `eclipselink-tck-install` module downloads a milestone
distribution and is not needed for this workflow.

`test1` runs the main suite, including signature tests. `test2` runs the five
tests that require the Bean Validation provider to be absent. Their XML reports
and verification summaries are kept separately under
`target/failsafe-reports/main` and `target/failsafe-reports/no-validator`.
For a focused callback run, use:

```sh
mvn -f tck/eclipselink-tck-run/pom.xml verify -Ppostgres-start,test1 -Dit.test=Jpa40CallbacksClient
```

The provider's weaving configuration still applies. When the TCK fixtures have
been statically woven, enable it with
`-Djpa.provider.implementation.specific.additional.properties=:eclipselink.weaving=static`.
Building the TCK again recompiles those fixtures and requires reapplying static
weaving. Selecting the snapshot dependencies does not itself enable weaving.
