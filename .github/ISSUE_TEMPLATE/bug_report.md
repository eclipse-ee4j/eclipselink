---
name: Bug report
about: Create a report to help us improve
title: ''
labels: ''
assignees: ''

---

**Describe the bug**
A clear and concise description of what the bug is.
Useful resources are
- Log messages
- Stack trace
- Heap dump or some Java object histogram

**To Reproduce**
Steps/resources to reproduce the behavior:

- EclipseLink version
- Java/JDK version
- JPA context like ``persistence.xml`` settings or related system properties (in case of JPA)
- Database provider/version
- JDBC driver provider/version (it should be useful if bug is related with some "specific" datatype e.g. JSON)
- JAXBContext creation (in case of MOXy)
- Other context description in case of SDO, DBWS or others
- Other additional context like AspectJ pre-compiler, Spring FW, JEE Server....
- Entity source (mainly applied annotations)
- Code example which leads into bug like:

```
...
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-jpa-pu");
EntityManager em = entityManagerFactory.createEntityManager();
em.persist(myEntity);
TestEntityMaster entity = new TestEntityMaster(10, "Persist Master 1");
em.persist(entity);
em.getTransaction().commit();
...
```

**Expected behavior**
A clear and concise description of what you expected to happen.

**Additional context**
Add any other context about the problem here.

**Note**
The best way for us how to describe bug is create some test case.
To simplify this process there some Maven archetypes in `etc/archetypes/bug_test_case` project directory which should be locally build by "mvn install".
Initial test project should be generated from these archetypes by:

`mvn archetype:generate  -DarchetypeGroupId=org.eclipse.persistence -DarchetypeArtifactId=org.eclipse.persistence.bug.jpa-archetype -DarchetypeVersion=5.0.0-SNAPSHOT -DgroupId=eclipselink.bug.testcase -DartifactId=jpa-testcase`

`mvn archetype:generate  -DarchetypeGroupId=org.eclipse.persistence -DarchetypeArtifactId=org.eclipse.persistence.bug.moxy-archetype -DarchetypeVersion=5.0.0-SNAPSHOT -DgroupId=eclipselink.bug.testcase -DartifactId=moxy-testcase`
