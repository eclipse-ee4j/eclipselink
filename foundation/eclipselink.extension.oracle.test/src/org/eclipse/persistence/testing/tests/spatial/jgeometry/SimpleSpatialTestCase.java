// Copyright (c) 2006, 2007, Oracle. All rights reserved.  
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import java.util.List;
import junit.framework.TestCase;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.MyGeometryConverter;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.testing.models.spatial.jgeometry.JGeometryProject;
import org.eclipse.persistence.testing.models.spatial.jgeometry.JGeometryTableCreator;
import org.eclipse.persistence.platform.database.oracle.converters.JGeometryConverter;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DefaultConnector;

/**
 * Base test case for the tests using the SimpleSpatial model.
 */
public abstract class SimpleSpatialTestCase extends TestCase {

    public static final String SPATIAL_SESSION_NAME = "spatial-session";
    public static final String SPATIAL_SESSIONS_XML_NAME = "org/eclipse/persistence/testing/models/spatial/jgeometry/spatial-sessions.xml";
    public static boolean isJunit = true;
    protected static int DEFAULT_SRID = 0;
    
    protected DatabaseSession session;

    public SimpleSpatialTestCase(){
        super();
    }
    
    public SimpleSpatialTestCase(String name){
        super(name);
    }
    
    public static DatabaseSession getSession() throws Exception{ 
        DatabaseSession spatialSession = null;
        if (isJunit){
            // look up the session from sessions.xml
            spatialSession = (DatabaseSession)SessionManager.getManager().getSession(SPATIAL_SESSION_NAME, SPATIAL_SESSIONS_XML_NAME);
        } else {
            // do not use sessions.xml to look up the session.  Build it from the test browser's session
            spatialSession = (DatabaseSession)SessionManager.getManager().getSessions().get(SPATIAL_SESSION_NAME);
            if (spatialSession == null){
                Project project = new JGeometryProject();
                Session configSession = SimpleJGeometryTestModel.getConfigSession();
                project.setLogin((DatabaseLogin)configSession.getLogin().clone());
                spatialSession = new org.eclipse.persistence.internal.sessions.DatabaseSessionImpl(project);
                spatialSession.setServerPlatform(configSession.getServerPlatform());
                spatialSession.getPlatform().addStructConverter(new JGeometryConverter());
                // make the MyGeometryConverter type point at a user defined type for the current user
                //Bug5837254, in case test running on the server, the user name should extract from the metadata
                //of the datasource.
                Connector connector = spatialSession.getLogin().getConnector();
                String userName="";
                if(connector instanceof DefaultConnector){
                     userName = spatialSession.getLogin().getUserName();
                }else if (connector instanceof JNDIConnector){
                     userName= ((JNDIConnector)spatialSession.getLogin().getConnector()).getDataSource().getConnection().getMetaData().getUserName();
                }
                MyGeometryConverter.MY_GEOMETRY_TYPE = userName + "." + MyGeometryConverter.MY_GEOMETRY_TYPE_NAME;
                MyGeometryConverter.MY_GEOMETRY_TYPE = MyGeometryConverter.MY_GEOMETRY_TYPE.toUpperCase();
                spatialSession.getPlatform().addStructConverter(new MyGeometryConverter());
                (spatialSession).login();
                SessionManager.getManager().addSession(SPATIAL_SESSION_NAME, spatialSession);
            }
            spatialSession.setSessionLog(SimpleJGeometryTestModel.getConfigSession().getSessionLog());
            spatialSession.setLogLevel(SimpleJGeometryTestModel.getConfigSession().getLogLevel());
        }
        return spatialSession;
    }
   
    public void setUp() throws Exception {
        session = getSession();
        session.getIdentityMapAccessor().initializeIdentityMaps();
    }
    
    public void tearDown() throws Exception{
        session = null;
    }

    public static void setIsJunit(boolean isJunitValue){
        isJunit = isJunitValue;
    }
    
    public static boolean isJunit(){
        return isJunit;
    }
    
    public static void repopulate(DatabaseSession session, boolean replaceTables) throws Exception {       
        if (replaceTables){
            replaceTables(session);
        }
        UnitOfWork uow = session.acquireUnitOfWork();
        List existing = uow.readAllObjects(SimpleSpatial.class);
        uow.deleteAllObjects(existing);
        uow.commit();

        assertEquals(0, countSimpleSpatial(session));
        session.getIdentityMapAccessor().initializeIdentityMaps();

        SampleGeometries samples = new SampleGeometries(DEFAULT_SRID);

        uow = session.acquireUnitOfWork();
        uow.registerAllObjects(samples.simplePopulation());
        uow.commit();

        assertEquals(samples.simplePopulation().size(), countSimpleSpatial(session));
        session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    public static void replaceTables(DatabaseSession session){

        session.executeNonSelectingSQL("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'SIMPLE_SPATIAL'");
        TableCreator tableCreator = new JGeometryTableCreator();
        tableCreator.replaceTables(session);
        session.executeNonSelectingSQL("INSERT INTO USER_SDO_GEOM_METADATA(TABLE_NAME, COLUMN_NAME, DIMINFO) VALUES('SIMPLE_SPATIAL', 'GEOMETRY'," +
                " mdsys.sdo_dim_array(mdsys.sdo_dim_element('X', -100, 100, 0.005), mdsys.sdo_dim_element('Y', -100, 100, 0.005)))");     
        session.executeNonSelectingSQL("delete from SIMPLE_SPATIAL where gid between 1000 and 1013");
        session.executeNonSelectingSQL("CREATE INDEX test_idx on SIMPLE_SPATIAL(geometry) indextype is mdsys.spatial_index parameters ('sdo_level=5 sdo_numtiles=6')");        
    }
    
    public static int countSimpleSpatial(DatabaseSession session) {
        ReportQuery rq = 
            new ReportQuery(SimpleSpatial.class, new ExpressionBuilder());
        rq.addCount();
        rq.setShouldReturnSingleValue(true);

        return ((Number)session.executeQuery(rq)).intValue();
    }


}
