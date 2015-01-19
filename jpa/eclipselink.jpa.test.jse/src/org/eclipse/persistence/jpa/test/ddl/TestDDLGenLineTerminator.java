/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/13/2015 - Rick Curtis  
 *       - 438871 : Add support for writing statement terminator character(s) when generating ddl to script.
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.PUPropertiesProvider;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDDLGenLineTerminator implements PUPropertiesProvider {
    private static File terminatedScript, unterminatedScript;

    static {
        try {
            terminatedScript = File.createTempFile("TestDDLGen-terminated", ".sql.txt");
            unterminatedScript = File.createTempFile("TestDDLGen-unterminated", ".sql.txt");
            unterminatedScript.deleteOnExit();
        } catch (Exception e) {
            Assert.fail("Unable to run test due to " + e);
        }
    }

    @Emf(name = "terminated", createTables = DDLGen.NONE, classes = { Person.class, Dog.class },
            properties = {
                    @Property(name = "javax.persistence.schema-generation.scripts.action", value = "create"),
                    @Property(name = PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_TERMINATE_STATEMENTS,
                            value = "true"), })
    private EntityManagerFactory terminatedEmf;

    @Emf(name = "unterminated", createTables = DDLGen.NONE, classes = { Person.class, Dog.class },
            properties = { @Property(name = "javax.persistence.schema-generation.scripts.action", value = "create") })
    private EntityManagerFactory unterminatedEmf;

    @Override
    public Map<String, Object> getAdditionalPersistenceProperties(String puName) {
        Map<String, Object> res = new HashMap<String, Object>();
        String file;
        if (puName.equals("terminated")) {
            file = terminatedScript.getAbsolutePath();
        } else {
            file = unterminatedScript.getAbsolutePath();
        }
        res.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, file);
        res.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, file);
        return res;
    }

    @Test
    public void testGeneratedScripts() throws Exception {
        String terminatorToken = ((EntityManagerFactoryImpl) terminatedEmf).getDatabaseSession().getPlatform().getStoredProcedureTerminationToken();
        BufferedReader reader = new BufferedReader(new FileReader(terminatedScript));
        String line = reader.readLine();
        while (line != null) {
            Assert.assertTrue(line.endsWith(terminatorToken));
            line = reader.readLine();
        }
        reader.close();

        reader = new BufferedReader(new FileReader(unterminatedScript));
        line = reader.readLine();
        while (line != null) {
            Assert.assertFalse(line.endsWith(terminatorToken));
            line = reader.readLine();
        }
        reader.close();
    }
}
