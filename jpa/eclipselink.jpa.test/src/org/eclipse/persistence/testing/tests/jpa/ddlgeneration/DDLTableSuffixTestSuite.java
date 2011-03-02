/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle, Frank Schwarz. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/20/2008-1.0.1 Nathan Beyer (Cerner) 
 *       - 241308: Primary key is incorrectly assigned to embeddable class 
 *                 field with the same name as the primary key field's name
 *     01/12/2009-1.1 Daniel Lo, Tom Ware, Guy Pelletier
 *       - 247041: Null element inserted in the ArrayList 
 *     07/17/2009 - tware -  added tests for DDL generation of maps
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     09/15/2010-2.2 Chris Delahunt
 *       - 322233 - AttributeOverrides and AssociationOverride dont change field type info
 *     11/17/2010-2.2.0 Chris Delahunt 
 *       - 214519: Allow appending strings to CREATE TABLE statements
 *     11/23/2010-2.2 Frank Schwarz 
 *       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
 *     01/04/2011-2.3 Guy Pelletier 
 *       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
 *     01/06/2011-2.3 Guy Pelletier
 *       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
 *     01/11/2011-2.3 Guy Pelletier  
 *       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
 ******************************************************************************/   
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import javax.persistence.EntityManager;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLTableSuffixTestSuite extends JUnitTestCase {
    // This is the persistence unit name on server as for persistence unit name "ddlTableSuffix" in J2SE
    private static final String DDL_TABLE_CREATION_SUFFIX_PU = "MulitPU-3";
    private static final String DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME = "createDDL_ddlTableSuffix.jdbc";

    public DDLTableSuffixTestSuite() {
        super();
    }

    public DDLTableSuffixTestSuite(String name) {
        super(name);
        setPuName(DDL_TABLE_CREATION_SUFFIX_PU);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DDLTableSuffixTestSuite.class);
        
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Trigger DDL generation
        EntityManager em = createEntityManager(DDL_TABLE_CREATION_SUFFIX_PU);
        closeEntityManager(em);
        clearCache(DDL_TABLE_CREATION_SUFFIX_PU);
    }

    /**
     * 214519 - Allow appending strings to CREATE TABLE statements 
     * This test uses PU ddlTableSuffix to create file  DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME, which it then
     * reads back in to verify the CREATE TABLE statements have the correct strings appended to them
     */
    public void testDDLTableCreationWithSuffix(){
      //strings searched for:
        String property_suffix = " propertyCreationSuffix";
        String xml_suffix = "creationSuffixString";
        final String CREATE_TABLE = "CREATE TABLE ";

      //results:
        //used for checking eclipselink-orm.xml settings:
        ArrayList<String> statements = new ArrayList();//used to output the create statement should it not have the suffix set in the orm.xml
        ArrayList<Boolean> results = new ArrayList();

        //used for checking settings from properties:
        boolean has_property_suffix = true; //set to false when we find one create table statement missing the value
        String property_statement = "";//used to output the create statement should it not have the suffix set through properties

        EntityManager em = createEntityManager(DDL_TABLE_CREATION_SUFFIX_PU);

        List<String> strings = getDDLFile(DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME);
        for (String statement: strings){
            if (statement.startsWith(CREATE_TABLE)) {
                if( !statement.endsWith(property_suffix)){
                    has_property_suffix = false;
                    property_statement = statement;
                }
                if (statement.startsWith(CREATE_TABLE+"DDL_SALARY")){
                    statements.add(statement);
                    //xml_suffix will be right before property_suffix in the statement.  Should be enough to check that its there
                    results.add(statement.contains(xml_suffix+"1"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_RESPONS")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"2"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_COMMENT")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"3"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_MANY_MANY")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"4"));
                }
            }
        }

        assertTrue("missing creation suffix from properties on statement: "+property_statement, has_property_suffix);
        
        int size = statements.size();
        assertTrue("Missing some create Table statements, only got "+size+" of the 4 expected", size==4);
        for(int i=0;i<size; i++) {
            assertTrue("missing creation suffix "+i+" from eclipselink-orm.xml on statement: "+statements.get(i), results.get(i));
        }
    }

    /**
     * Returns a List of strings representing the lines within the fileName. 
     * @param fileName
     * @return List<String>
     */
    public List<String> getDDLFile(String fileName){
        ArrayList<String> array = new ArrayList();
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (fileStream == null) {
            File file = new File(fileName);
            try {
                fileStream = new FileInputStream(file);
            } catch (FileNotFoundException exception) {
                this.warning("cannot load file "+fileName+ " due to error: "+exception);
                throw ValidationException.fatalErrorOccurred(exception);
            }
        }
        InputStreamReader reader = null;
        BufferedReader buffReader = null;
        try {
            try {
                // Bug2631348  Only UTF-8 is supported
                reader = new InputStreamReader(fileStream, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }
            buffReader = new BufferedReader(reader);
            boolean go = true;
            while (go){
                String line = buffReader.readLine();
                if (line==null) {
                    go=false;
                } else {
                    array.add(line);
                }
            }

        }catch (Exception e) {
            //ignore exception
        } finally {
            try {
                if (buffReader != null) {
                    buffReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
        return array;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
