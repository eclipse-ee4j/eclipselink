/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.sessionsxml;

//javase imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

//domain-specific (testing) imports

public class ComicsPopulateAndQueryTestSuite {

    public static final String PACKAGE_PATH = 
        ComicsPopulateAndQueryTestSuite.class.getPackage().getName().replace('.', '/');
    
    // test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static Map<Integer, DynamicEntity> publishers = null;
    static Map<Integer, DynamicEntity> titles = null;
    static Map<Integer, DynamicEntity> issues = null;
    @BeforeClass
    public static void setUp() {
        session = ComicsConfigTestSuite.buildComicsSession();
        assertNotNull(session);
        dynamicHelper = new DynamicHelper(session);
        SchemaManager sm = new SchemaManager(session);
        sm.replaceDefaultTables();
        sm.replaceSequences();
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE ISSUE");
        session.executeNonSelectingSQL("DROP TABLE TITLE");
        session.executeNonSelectingSQL("DROP TABLE PUBLISHER");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @Test
    public void populate() {
        UnitOfWork uow = null;
        try {
            uow = session.acquireUnitOfWork();
            URL publisherFileURL = dynamicHelper.getDynamicClassLoader().getResource(
                PACKAGE_PATH + "/publisher.tab");
            publishers = loadPublishers(session, publisherFileURL);
            persist(uow, publishers);

            URL titleFileURL = getClass().getClassLoader().getResource(
                PACKAGE_PATH + "/title.tab");
            titles = loadTitles(session, titleFileURL, publishers);
            persist(uow, titles);

            URL issueFileURL = getClass().getClassLoader().getResource(
                PACKAGE_PATH + "/issue.tab");
            issues = loadIssues(session, issueFileURL, titles);
            persist(uow, issues);
            uow.commit();
        }
        finally {
            if (uow != null && uow.isActive()) {
                uow.release();
            }
        }
    }
    
    @Test
    public void countComics() {
        ReportQuery countQuery = new ReportQuery(dynamicHelper.getType("Publisher").getJavaClass(), 
            new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(publishers.size(), ((Number)session.executeQuery(countQuery)).intValue());

        countQuery = new ReportQuery(dynamicHelper.getType("Title").getJavaClass(),
            new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(titles.size(), ((Number)session.executeQuery(countQuery)).intValue());

        countQuery = new ReportQuery(dynamicHelper.getType("Issue").getJavaClass(),
            new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(issues.size(), ((Number)session.executeQuery(countQuery)).intValue());
    }

    @Test
    public void readAll() {
        session.readAllObjects(session.getDescriptorForAlias("Issue").getJavaClass());
        session.readAllObjects(session.getDescriptorForAlias("Publisher").getJavaClass());
        session.readAllObjects(session.getDescriptorForAlias("Title").getJavaClass());
    }

    protected void persist(UnitOfWork uow, Map<Integer, DynamicEntity> entities) {
        for (DynamicEntity entity : entities.values()) {
            uow.registerNewObject(entity);
        }
    }

    protected Map<Integer, DynamicEntity> loadIssues(Session server, URL fileURL, 
        Map<Integer, DynamicEntity> titles) {
        DynamicType type = dynamicHelper.getType("Issue");
        Map<Integer, DynamicEntity> issues = new HashMap<Integer, DynamicEntity>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fileURL.openStream()));
            String nextLine = null;
            while ((nextLine = reader.readLine()) != null) {
                DynamicEntity issue = buildIssue(type, nextLine, titles);
                issues.put(issue.<Integer> get("id"), issue);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    // ignore
                }
            }
        }
        return issues;
    }

    protected DynamicEntity buildIssue(DynamicType issueType, String line, Map<Integer, DynamicEntity> titles) {
        // TITLE,ISSUE_NUMBER,STORY_ARC,CONDITION,COMMENTS,COPIES,ID,TITLE_ID
        String[] columns = line.split("\t");
        DynamicEntity issue = issueType.newDynamicEntity();
        issue.set("number", Integer.valueOf(columns[1]));
        issue.set("condition", columns[3]);
        issue.set("comments", columns[4]);
        String numCopiesString = columns[5];
        if (numCopiesString.length() > 0) {
            issue.set("copies", Integer.valueOf(numCopiesString));
        }
        issue.set("id", Integer.valueOf(columns[6]));
        issue.set("title", titles.get(Integer.valueOf(columns[7])));
        return issue;
    }

    protected Map<Integer, DynamicEntity> loadPublishers(Session server, URL fileURL) {
        DynamicType type = dynamicHelper.getType("Publisher");
        Map<Integer, DynamicEntity> publishers = new HashMap<Integer, DynamicEntity>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fileURL.openStream()));
            String nextLine = null;
            while ((nextLine = reader.readLine()) != null) {
                DynamicEntity publisher = buildPublisher(type, nextLine);
                publishers.put(publisher.<Integer> get("id"), publisher);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    // ignoree
                }
            }
        }
        return publishers;
    }

    protected DynamicEntity buildPublisher(DynamicType publisherType, String line) {
        // NAME ID
        String[] columns = line.split("\t");
        assert columns.length == 2;
        DynamicEntity publisher = publisherType.newDynamicEntity();
        publisher.set("name", columns[0]);
        publisher.set("id", Integer.valueOf(columns[1]));
        return publisher;
    }

    protected Map<Integer, DynamicEntity> loadTitles(Session server, URL fileURL, 
        Map<Integer, DynamicEntity> publishers) {
        DynamicType type = dynamicHelper.getType("Title");
        Map<Integer, DynamicEntity> titles = new HashMap<Integer, DynamicEntity>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fileURL.openStream()));
            String nextLine = null;
            while ((nextLine = reader.readLine()) != null) {
                DynamicEntity title = buildTitle(type, nextLine, publishers);
                titles.put(title.<Integer> get("id"), title);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    // ignore
                }
            }
        }
        return titles;
    }

    protected DynamicEntity buildTitle(DynamicType type, String line, Map<Integer, DynamicEntity> publishers) {
        // NAME,PUBLISHER,FORMAT,SUBSCRIBED,ID,PUBLISHER_ID
        String[] columns = line.split("\t");

        DynamicEntity title = type.newDynamicEntity();
        title.set("name", columns[0]);
        title.set("format", columns[2]);
        title.set("id", Integer.valueOf(columns[4]));
        title.set("publisher", publishers.get(Integer.valueOf(columns[5])));
        return title;
    }

}