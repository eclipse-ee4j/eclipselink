[//]: # " Copyright (c) 2026 Contributors to the Eclipse Foundation. "
[//]: # " This program and the accompanying materials are made available under the "
[//]: # " terms of the Eclipse Public License v. 2.0, which is available at "
[//]: # " http://www.eclipse.org/legal/epl-2.0. "
[//]: # " SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 "

# Running the current Persistence TCK

The EclipseLink build and this runner use the Persistence `4.0.0-SNAPSHOT` API.
The runner also uses the `4.0.0-SNAPSHOT` TCK, with JUnit 6 and the common test
framework dependency selected by that TCK. Use Maven 3.9 or newer and build the
current API/TCK sources with JDK 25. The runner and its test JVM may use JDK 21
or JDK 25.

Build the API and TCK from an updated local checkout of `jakartaee/persistence`:

```sh
mvn -pl tck/spec-tests,tck/procedures -am -DskipTests clean install
```

Build EclipseLink from its repository root, then run this module against a
PostgreSQL test database. The normal Maven repository is sufficient; no separate
repository or copied provider jars are needed.

```sh
mvn -pl jpa/org.eclipse.persistence.jpa,jpa/org.eclipse.persistence.jpa.modelgen,moxy/org.eclipse.persistence.moxy -am -DskipTests clean install
mvn -f tck/eclipselink-tck-run/pom.xml clean verify -Ppostgres-start,test1,test2
```

To select a different test JVM, add `-Djvm=/path/to/jdk/bin/java`.

The `postgres-start` profile creates a disposable local PostgreSQL instance and
requires a running Docker daemon. Include all three profiles: explicitly
activating `postgres-start` disables the default activation of `test1` and
`test2`. If Maven needs the endpoint of the selected Docker context, set it with:

```sh
export DOCKER_HOST="$(docker context inspect --format '{{.Endpoints.docker.Host}}')"
```

For an existing test database, omit `-Ppostgres-start,test1,test2` and supply its
JDBC configuration externally; plain `clean verify` activates both test
profiles. Run this module directly when using locally built snapshots; the
sibling `eclipselink-tck-install` module downloads a milestone distribution and
is not needed for this workflow.

`test1` runs the main suite, including signature tests. `test2` runs the five
tests that require the Bean Validation provider to be absent. Their XML reports
and verification summaries are kept separately under
`target/failsafe-reports/main` and `target/failsafe-reports/no-validator`.
For a focused callback run, use:

```sh
mvn -f tck/eclipselink-tck-run/pom.xml verify -Ppostgres-start,test1 -Dit.test=Jpa40CallbacksClient
```

The normal Maven lifecycle automatically prepares static weaving for the fetch
fixtures and enables it for both test profiles. It extracts the required classes
from the resolved TCK dependency, enhances them with the selected EclipseLink
version, and places them in `target/test-classes` ahead of the original TCK JAR.
Preparation uses `target/tck-weaving` and runs again even without `clean`, so
rebuilding the API/TCK requires no separate weaving step. The dependency in the
Maven repository remains unchanged.

A preflight in each test JVM verifies that the enhanced classes are actually
loaded, including their lazy accessors. Missing or unenhanced fixtures stop the
run with a preparation error. The build-only descriptor at
`src/test/resources/weaving/META-INF/persistence.xml` is read directly from source
for static weaving, including when test-resource processing is skipped. Maven's
normal test-resource processing also copies it to
`target/test-classes/weaving/META-INF/persistence.xml`. Its nested location keeps
it outside runtime classpath-root
`META-INF/persistence.xml` discovery, so it does not replace the persistence units
supplied by the TCK at runtime.
Failsafe continues to report provider test failures and errors normally and
returns a failing build when they remain.
