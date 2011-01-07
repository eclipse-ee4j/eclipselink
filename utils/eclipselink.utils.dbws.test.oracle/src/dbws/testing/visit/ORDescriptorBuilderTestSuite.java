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
 *     Mike Norman - May 2008, created DBWS Oracle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.util.List;
import java.util.Vector;

//JUnit4 imports
import org.junit.Test;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherWalker;
import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLORDescriptorBuilder;
import static org.eclipse.persistence.tools.dbws.oracle.PLSQLORDescriptorBuilder.ITEMS_MAPPING_ATTRIBUTE_NAME;
import static org.eclipse.persistence.tools.dbws.oracle.PLSQLORDescriptorBuilder.ITEMS_MAPPING_FIELD_NAME;

//testing imports
import static dbws.testing.visit.DBWSTestHelper.ARECORD_DESCRIPTOR_ALIAS;
import static dbws.testing.visit.DBWSTestHelper.ARECORD_DESCRIPTOR_JAVACLASSNAME;
import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC1;
import static dbws.testing.visit.DBWSTestHelper.PROC2;
import static dbws.testing.visit.DBWSTestHelper.PROC3;
import static dbws.testing.visit.DBWSTestHelper.PROC4;
import static dbws.testing.visit.DBWSTestHelper.PROC5;
import static dbws.testing.visit.DBWSTestHelper.PROC6;
import static dbws.testing.visit.DBWSTestHelper.TBL1_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL1_DESCRIPTOR_ALIAS;
import static dbws.testing.visit.DBWSTestHelper.TBL1_DESCRIPTOR_JAVACLASSNAME;
import static dbws.testing.visit.DBWSTestHelper.TBL2_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL2_DESCRIPTOR_ALIAS;
import static dbws.testing.visit.DBWSTestHelper.TBL2_DESCRIPTOR_JAVACLASSNAME;
import static dbws.testing.visit.DBWSTestHelper.TBL3_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL3_DESCRIPTOR_ALIAS;
import static dbws.testing.visit.DBWSTestHelper.TBL3_DESCRIPTOR_JAVACLASSNAME;
import static dbws.testing.visit.DBWSTestHelper.TBL4_COMPATIBLETYPE;
import static dbws.testing.visit.DBWSTestHelper.TBL4_DESCRIPTOR_ALIAS;
import static dbws.testing.visit.DBWSTestHelper.TBL4_DESCRIPTOR_JAVACLASSNAME;

public class ORDescriptorBuilderTestSuite extends BuilderTestSuite {

    @Test
    public void p1test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC1);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC1, descriptors.size() == 1);
        tbl1Asserts(descriptors.get(0));
    }

    protected void tbl1Asserts(ObjectRelationalDataTypeDescriptor tbl1Descriptor) {
        assertTrue("wrong tbl1Descriptor alias",
            tbl1Descriptor.getAlias().equals(TBL1_DESCRIPTOR_ALIAS));
        assertTrue("wrong tbl1Descriptor Java className",
            tbl1Descriptor.getJavaClassName().equals(TBL1_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl1Descriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            mapping.getAttributeName().equals(ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("incorrect mapping field name",
            mapping.getField().getName().equals(ITEMS_MAPPING_FIELD_NAME));
        assertTrue("mapping is not AbstractCompositeDirectCollection mapping",
            mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertTrue("mapping incorrent structure name",
            arrayMapping.getStructureName().equals(TBL1_COMPATIBLETYPE));
    }

    @Test
    public void p2test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC2);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC2, descriptors.size() == 1);
        tbl2Asserts(descriptors.get(0));
    }

    protected void tbl2Asserts(ObjectRelationalDataTypeDescriptor tbl2Descriptor) {
        assertTrue("wrong tbl2Descriptor alias",
            tbl2Descriptor.getAlias().equals(TBL2_DESCRIPTOR_ALIAS));
        assertTrue("wrong tbl2Descriptor Java className",
            tbl2Descriptor.getJavaClassName().equals(TBL2_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl2Descriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            mapping.getAttributeName().equals(ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("incorrect mapping field name",
            mapping.getField().getName().equals(ITEMS_MAPPING_FIELD_NAME));
        assertTrue("mapping is not AbstractCompositeDirectCollection mapping",
            mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertTrue("mapping incorrent structure name",
            arrayMapping.getStructureName().equals(TBL2_COMPATIBLETYPE));
    }

    @Test
    public void p3test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC3);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC3, descriptors.size() == 4);
        tbl3Asserts(descriptors.get(3));
        aRecordAsserts(descriptors.get(0));
        tbl1Asserts(descriptors.get(1));
        tbl2Asserts(descriptors.get(2));
    }

    protected void tbl3Asserts(ObjectRelationalDataTypeDescriptor tbl3Descriptor) {
        assertTrue("wrong tbl3Descriptor Java className",
            tbl3Descriptor.getAlias().equals(TBL3_DESCRIPTOR_ALIAS));
        assertTrue("wrong tbl3Descriptor Java className",
            tbl3Descriptor.getJavaClassName().equals(TBL3_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl3Descriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            mapping.getAttributeName().equals(ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("incorrect mapping field name",
            mapping.getField().getName().equals(ITEMS_MAPPING_FIELD_NAME));
        assertTrue("mapping is not AbstractCompositeCollection mapping",
            mapping.isAbstractCompositeCollectionMapping());
        ObjectArrayMapping oArrayMapping = (ObjectArrayMapping)mapping;
        assertTrue("mapping incorrent structure name",
            oArrayMapping.getStructureName().equals(TBL3_COMPATIBLETYPE));
        assertTrue("mapping incorrent reference class name",
            oArrayMapping.getReferenceClassName().equals(ARECORD_DESCRIPTOR_JAVACLASSNAME));
    }

    protected void aRecordAsserts(ObjectRelationalDataTypeDescriptor aRecordDescriptor) {
        assertTrue("wrong aRecordDescriptor alias",
            aRecordDescriptor.getAlias().equals(ARECORD_DESCRIPTOR_ALIAS));
        assertTrue("wrong aRecordDescriptor Java className",
            aRecordDescriptor.getJavaClassName().equals(ARECORD_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = aRecordDescriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 3);
        DatabaseMapping dm1 = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            dm1.getAttributeName().equals("t1"));
        assertTrue("mapping is not AbstractCompositeDirectCollection mapping",
            dm1.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping1 = (ArrayMapping)dm1;
        assertTrue("mapping incorrent structure name",
            arrayMapping1.getStructureName().equals(TBL1_COMPATIBLETYPE));
        DatabaseMapping dm2 = mappings.get(1);
        assertTrue("incorrect mapping attribute name",
            dm2.getAttributeName().equals("t2"));
        assertTrue("mapping is not AbstractCompositeDirectCollection mapping",
            dm2.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)dm2;
        assertTrue("mapping incorrent structure name",
            arrayMapping2.getStructureName().equals(TBL2_COMPATIBLETYPE));
        DatabaseMapping dm3 = mappings.get(2);
        assertTrue("incorrect mapping attribute name",
            dm3.getAttributeName().equals("t3"));
        assertTrue("mapping is not DirectToField mapping",
            dm3.isDirectToFieldMapping());
    }

    @Test
    public void p4test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC4);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC4, descriptors.size() == 3);
        aRecordAsserts(descriptors.get(0));
        tbl1Asserts(descriptors.get(1));
        tbl2Asserts(descriptors.get(2));
    }

    @Test
    public void p5test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC5);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC5, descriptors.size() == 3);
        aRecordAsserts(descriptors.get(0));
        tbl1Asserts(descriptors.get(1));
        tbl2Asserts(descriptors.get(2));
    }

    @Test
    public void p6test() {
        PLSQLProcedureOperationModel pModel = new PLSQLProcedureOperationModel();
        pModel.setCatalogPattern(PACKAGE_NAME);
        pModel.setSchemaPattern(username.toUpperCase());
        pModel.setProcedurePattern(PROC6);
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, pModel);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        pModel.getJPubType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC6, descriptors.size() == 2);
        tbl2Asserts(descriptors.get(0));
        tbl4Asserts(descriptors.get(1));
    }

    protected void tbl4Asserts(ObjectRelationalDataTypeDescriptor tbl4Descriptor) {
        assertTrue("wrong tbl4Descriptor alias",
            tbl4Descriptor.getAlias().equals(TBL4_DESCRIPTOR_ALIAS));
        assertTrue("wrong tbl4Descriptor Java className",
            tbl4Descriptor.getJavaClassName().equals(TBL4_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl4Descriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            mapping.getAttributeName().equals(ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("incorrect mapping field name",
            mapping.getField().getName().equals(ITEMS_MAPPING_FIELD_NAME));
        assertTrue("mapping is not isAbstractCompositeDirectCollection mapping",
            mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)mapping;
        assertTrue("mapping incorrent structure name",
            arrayMapping2.getStructureName().equals(TBL4_COMPATIBLETYPE));
    }
/*
    @Test
    public void p7test() {
        OracleHelper.buildStoredProcedure(conn, username, ora11Platform, PACKAGE_NAME,
            username.toUpperCase(), PROC7, builder);
        PLSQLORDescriptorBuilder orDescriptorBuilder = new PLSQLORDescriptorBuilder();
        PublisherWalker walker = new PublisherWalker(orDescriptorBuilder);
        builder.getSqlType().accept(walker);
        List<ObjectRelationalDataTypeDescriptor> descriptors =
            orDescriptorBuilder.getDescriptors();
        assertTrue("wrong number of descriptors for " + PROC7, descriptors.size() == 5);
        aRecordAsserts(descriptors.get(0));
        tbl1Asserts(descriptors.get(1));
        tbl2Asserts(descriptors.get(2));
        tbl4Asserts(descriptors.get(3));
        bRecordAsserts(descriptors.get(4));
    }

    protected void bRecordAsserts(ObjectRelationalDataTypeDescriptor bRecordDescriptor) {
        assertTrue("wrong bRecordDescriptor alias",
            bRecordDescriptor.getAlias().equals(BRECORD_DESCRIPTOR_ALIAS));
        assertTrue("wrong bRecordDescriptor Java className",
            bRecordDescriptor.getJavaClassName().equals(BRECORD_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = bRecordDescriptor.getMappings();
        assertTrue("wrong number of mappings", mappings.size() == 2);
        DatabaseMapping b1Mapping = mappings.get(0);
        assertTrue("incorrect mapping attribute name",
            b1Mapping.getAttributeName().equals("b1"));
        assertTrue("mapping is not isAbstractCompositeObject mapping",
            b1Mapping.isAbstractCompositeObjectMapping());
        StructureMapping b1Mapping2 = (StructureMapping)b1Mapping;
        assertTrue("mapping incorrent reference class name",
            b1Mapping2.getReferenceClassName().equals(ARECORD_DESCRIPTOR_JAVACLASSNAME));
        DatabaseMapping b2Mapping = mappings.get(1);
        assertTrue("incorrect mapping attribute name",
            b2Mapping.getAttributeName().equals("b2"));
        assertTrue("mapping is not isAbstractCompositeCollection mapping",
            b2Mapping.isAbstractCompositeCollectionMapping());
        ObjectArrayMapping b2Mapping2 = (ObjectArrayMapping)b2Mapping;
        assertTrue("mapping incorrent structure name",
            b2Mapping2.getStructureName().equals(TBL4_COMPATIBLETYPE));
        assertTrue("mapping incorrent reference class name",
            b2Mapping2.getReferenceClassName().equals(TBL4_DESCRIPTOR_JAVACLASSNAME));
    }
*/
}