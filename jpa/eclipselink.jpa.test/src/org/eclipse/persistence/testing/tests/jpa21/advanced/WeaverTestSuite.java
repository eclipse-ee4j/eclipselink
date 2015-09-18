package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.Arrays;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.Athlete;
import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WeaverTestSuite extends JUnitTestCase {

    public WeaverTestSuite(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu-with-mappedsuperclass";
    }


    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeaverTestSuite");

        suite.addTest(new WeaverTestSuite("testMappedSuperclassWeaving"));

        return suite;
    }

    //bug #466271 - @MappedSuperclass with no implementations should be woven
    public void testMappedSuperclassWeaving() {
        EntityManagerFactory emf = getEntityManagerFactory();
        ManagedType<Athlete> managedType = emf.getMetamodel().managedType(Athlete.class);
        Class<Athlete> javaClass = emf.getMetamodel().managedType(Athlete.class).getJavaType();
        Assert.assertTrue(Arrays.asList(javaClass.getInterfaces()).contains(PersistenceEntity.class));
    }
}
