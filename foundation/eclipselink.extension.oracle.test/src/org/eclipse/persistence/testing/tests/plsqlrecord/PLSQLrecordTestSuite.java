package org.eclipse.persistence.testing.tests.plsqlrecord;

// JUnit imports
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    PLSQLrecordInTestSet.class,
    PLSQLrecordOutTestSet.class,
    PLSQLrecordInOutTestSet.class,
    PLSQLrecordWithCompatibleTypeInTestSet.class,
    PLSQLrecordWithCompatibleTypeOutTestSet.class,
    PLSQLrecordWithCompatibleTypeInOutTestSet.class
})
public class PLSQLrecordTestSuite {
}
