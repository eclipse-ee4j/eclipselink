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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.nchar;

import java.sql.Connection;

import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.tests.unwrappedconnection.TestOracleConnection;

public class ReadNcharTest extends BaseNcharTest {

    public ReadNcharTest() {
        super();
        setDescription("Tests reading NCHAR, NVARCHAR2, NCLOB from Oracle database.");
    }

    @SuppressWarnings("deprecation")
    protected void setup() {
        super.setup();
    //#Bug5200836, the added line ensures the connection is available when tests being switched
    //from the corresponding unwrapConnectionTestModel to this model.
    ((AbstractSession)getSession()).getAccessor().incrementCallCount((AbstractSession)getSession());
        java.sql.ResultSet resultSet = null;
        try {
            java.sql.DatabaseMetaData metaData = ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData(); /*"TEST"*/
                resultSet = metaData.getColumns(null, null, "CHARNCHAR", null);

            int i = 1;
            while (resultSet.next()) {
                resultSet.getString("COLUMN_NAME");

                //TYPE_NAME String => Data source dependent type name,
                resultSet.getString("TYPE_NAME");

                i++;
            }
        } catch (java.sql.SQLException sqlException) {
        }

        // insert directly an NCHAR-containing record into the db

        // obtain an id to use
        int id = getSession().getNextSequenceNumberValue(CharNchar.class).intValue();
        char ch = 'a';
        char nCh = '\u4010';
        int stringSize = 5;
        // It is important that clobSize1 < 4K (or 5.9K?)
        int clobSize1 = 5;
        // clobSize2 is arbitrary
        int clobSize2 = 8000;
        controlObject = new CharNchar(ch, nCh, stringSize, clobSize1, clobSize2);
        controlObject.setId(id);

        try {
        Connection originConnection = ((AbstractSession)getSession()).getAccessor().getConnection();
        Connection unWrappedConnection;
        if(originConnection instanceof TestOracleConnection){
            unWrappedConnection = ((TestOracleConnection)originConnection).getPhysicalConnection();
        }  else {
            unWrappedConnection = originConnection;
        }
            oracle.jdbc.OraclePreparedStatement pstmt;
        pstmt = (oracle.jdbc.OraclePreparedStatement)
                unWrappedConnection.prepareStatement(
                "insert into CHARNCHAR (ID,CH,NCH,STR,NSTR,CLB,NCLB,CLB2,NCLB2) values(?,?,?,?,?,?,?,?,?)");

            pstmt.setInt(1, controlObject.getId());

            pstmt.setObject(2, controlObject.getChar().toString());

            pstmt.setFormOfUse(3, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
            pstmt.setObject(3, controlObject.getNchar().toString());

            pstmt.setObject(4, controlObject.getStr());

            pstmt.setFormOfUse(5, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
            pstmt.setObject(5, controlObject.getNstr());

            // Also possible:
            //    pstmt.setCharacterStream(6, new java.io.CharArrayReader(controlObject.getClob()), controlObject.getClob().length);
            pstmt.setObject(6, new String(controlObject.getClob()));

            pstmt.setFormOfUse(7, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
            // Also possible:
            //    pstmt.setCharacterStream(7, new java.io.CharArrayReader(controlObject.getNclob()), controlObject.getNclob().length);
            pstmt.setObject(7, new String(controlObject.getNclob()));

            // insert a dummy empty CLOB
            pstmt.setObject(8, " ");

            // insert a dummy empty NCLOB
            pstmt.setFormOfUse(9, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
            pstmt.setObject(9, " ");

            pstmt.execute();
            pstmt.close();

            // Handle clob2, nClob2
            getAbstractSession().beginTransaction();
            java.sql.Statement stmt = ((AbstractSession)getSession()).getAccessor().getConnection().createStatement();
            java.sql.ResultSet clobs = stmt.executeQuery("select CLB2, NCLB2 from CHARNCHAR " + "where ID = '" + controlObject.getId() + "' for update");

            if (clobs.next()) {
                oracle.sql.CLOB clob = (oracle.sql.CLOB)clobs.getObject("CLB2");
                java.io.Writer clobWriter = clob.getCharacterOutputStream();
                clobWriter.write(controlObject.getClob2(), 0, controlObject.getClob2().length);
                clobWriter.close();

                oracle.sql.CLOB nclob = (oracle.sql.CLOB)clobs.getObject("NCLB2");
                java.io.Writer nclobWriter = nclob.getCharacterOutputStream();
                nclobWriter.write(controlObject.getNclob2(), 0, controlObject.getNclob2().length);
                nclobWriter.close();
            }

            clobs.close();
            stmt.close();
            getAbstractSession().commitTransaction();
        } catch (java.sql.SQLException ex) {
            // attempt to write a "big" CLOB (>4K? >5.9K?) in "small" way may cause disconnection
            boolean disconnected = false;
            try {
                if (!getAbstractSession().isInTransaction()) {
                    getAbstractSession().beginTransaction();
                }
                getAbstractSession().rollbackTransaction();
            } catch (org.eclipse.persistence.exceptions.DatabaseException ex2) {
                disconnected = true;
            }
            if (disconnected) {
                ((AbstractSession)getSession()).setAccessor(getSession().getLogin().buildAccessor());
                ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
            }
            throw new TestWarningException("Failed to insert a new record into the db. Internal java.sql.Exception: " + ex.getMessage());
        } catch (java.io.IOException exIO) {
            if (getAbstractSession().isInTransaction()) {
                getAbstractSession().rollbackTransaction();
            }
            throw new TestWarningException("Failed to insert a new record into the db. Internal java.io.IOException: " + exIO.getMessage());
        }
    ((AbstractSession)getSession()).getAccessor().decrementCallCount();
    }

    protected void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = (builder.get("id").equal(controlObject.getId()));
        object = (CharNchar)getSession().readObject(CharNchar.class, exp);
    }

    /*  protected void insertSmallCLOB() throws java.sql.SQLException {
    oracle.jdbc.OraclePreparedStatement pstmt;
    pstmt = (oracle.jdbc.OraclePreparedStatement)
             ((AbstractSession)getSession()).getAccessor().getConnection().prepareStatement(
             "insert into CHARNCHAR (ID,CH,NCH,STR,NSTR,CLOB,NCLOB) values(?,?,?,?,?,?,?)");

    pstmt.setInt(1, controlObject.getId());

    pstmt.setObject(2, (new Character(controlObject.getChar())).toString());

    pstmt.setFormOfUse(3, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
    pstmt.setObject(3, (new Character(controlObject.getNchar())).toString());

    pstmt.setObject(4, controlObject.getStr());

    pstmt.setFormOfUse(5, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
    pstmt.setObject(5, controlObject.getNstr());

// Also possible:
//    pstmt.setCharacterStream(6, new java.io.CharArrayReader(controlObject.getClob()), controlObject.getClob().length);
    pstmt.setObject(6, new String(controlObject.getClob()));

    pstmt.setFormOfUse(7, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
// Also possible:
//    pstmt.setCharacterStream(7, new java.io.CharArrayReader(controlObject.getNclob()), controlObject.getNclob().length);
    pstmt.setObject(7, new String(controlObject.getNclob()));

    pstmt.execute();
    pstmt.close();
  }

  protected void insertBigCLOB() throws java.sql.SQLException, java.io.IOException {
    oracle.jdbc.OraclePreparedStatement pstmt;
    pstmt = (oracle.jdbc.OraclePreparedStatement)
             ((AbstractSession)getSession()).getAccessor().getConnection().prepareStatement(
             "insert into CHARNCHAR (ID,CH,NCH,STR,NSTR,CLOB,NCLOB) values(?,?,?,?,?,?,?)");

    pstmt.setInt(1, controlObject.getId());

    pstmt.setObject(2, (new Character(controlObject.getChar())).toString());

    pstmt.setFormOfUse(3, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
    pstmt.setObject(3, (new Character(controlObject.getNchar())).toString());

    pstmt.setObject(4, controlObject.getStr());

    pstmt.setFormOfUse(5, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
    pstmt.setObject(5, controlObject.getNstr());

//    pstmt.setObject(6, "EMPTY_CLOB()");
    pstmt.setObject(6, " ");

    pstmt.setFormOfUse(7, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
//    pstmt.setObject(7, "EMPTY_CLOB()");
    pstmt.setObject(7, " ");

    pstmt.execute();
    pstmt.close();

    // need autoCommit flag set to false
    getAbstractSession().beginTransaction();
    try {
        java.sql.Statement stmt = ((AbstractSession)getSession()).getAccessor().getConnection().createStatement();
        java.sql.ResultSet clobs = stmt.executeQuery(
                                    "select CLOB, NCLOB from CHARNCHAR "+
                                    "where ID = '" + controlObject.getId() + "' for update");

        if(clobs.next()) {
            oracle.sql.CLOB clob = (oracle.sql.CLOB)clobs.getObject("CLOB");
            java.io.Writer clobWriter = clob.getCharacterOutputStream();
            clobWriter.write(controlObject.getClob(), 0, controlObject.getClob().length);
            clobWriter.close();

            oracle.sql.CLOB nclob = (oracle.sql.CLOB)clobs.getObject("NCLOB");
            java.io.Writer nclobWriter = nclob.getCharacterOutputStream();
            nclobWriter.write(controlObject.getNclob(), 0, controlObject.getNclob().length);
            nclobWriter.close();
        }

        clobs.close();
        stmt.close();

        getAbstractSession().commitTransaction();
    } catch (java.sql.SQLException ex) {
        getAbstractSession().rollbackTransaction();
        throw ex;
    } catch (java.io.IOException ioEx) {
        getAbstractSession().rollbackTransaction();
        throw ioEx;
    }
  }*/
}
