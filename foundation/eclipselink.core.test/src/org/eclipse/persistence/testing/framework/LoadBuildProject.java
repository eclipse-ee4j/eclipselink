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
package org.eclipse.persistence.testing.framework;

import java.util.*;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sequencing.NativeSequence;

/**
 * TopLink project for storing test results.
 */
public class LoadBuildProject extends org.eclipse.persistence.sessions.Project {

    public LoadBuildProject() {
        applyPROJECT();
        applyLOGIN();
        buildLoadBuildSummaryDescriptor();
        buildTestResultDescriptor();
        buildTestResultsSummaryDescriptor();
    }

    protected void applyLOGIN() {
        org.eclipse.persistence.sessions.DatabaseLogin login = new org.eclipse.persistence.sessions.DatabaseLogin();

        login.setUserName("spec");
        login.setPassword("spec");
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        login.setConnectionString("jdbc:oracle:thin:@qaott46.ca.oracle.com:1521:spec");
        login.setPlatformClassName("org.eclipse.persistence.platform.database.OraclePlatform");
        NativeSequence sequence = new NativeSequence();
        sequence.setPreallocationSize(500);
        login.setDefaultSequence(sequence);
        setLogin(login);
    }

    protected void applyPROJECT() {
        setName("LoadBuild");
    }

    protected void buildLoadBuildSummaryDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.framework.LoadBuildSummary.class);
        Vector vector = new Vector();
        vector.addElement("LOADBUILD");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("LOADBUILD.id");
        //descriptor.addPrimaryKeyFieldName("LOADBUILD.lbtimestamp");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("RESULTSUM_SEQ");
        descriptor.setSequenceNumberFieldName("id");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("LOADBUILD.id");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("errors");
        directtofieldmapping.setFieldName("LOADBUILD.lberrors");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("fatalErrors");
        directtofieldmapping1.setFieldName("LOADBUILD.fatalErrors");
        descriptor.addMapping(directtofieldmapping1);

        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("loginChoice");
        directtofieldmapping2.setFieldName("LOADBUILD.loginChoice");
        descriptor.addMapping(directtofieldmapping2);

        DirectToFieldMapping os = new DirectToFieldMapping();
        os.setAttributeName("os");
        os.setFieldName("LOADBUILD.os");
        descriptor.addMapping(os);

        DirectToFieldMapping jvm = new DirectToFieldMapping();
        jvm.setAttributeName("jvm");
        jvm.setFieldName("LOADBUILD.jvm");
        descriptor.addMapping(jvm);

        DirectToFieldMapping toplinkVersion = new DirectToFieldMapping();
        toplinkVersion.setAttributeName("toplinkVersion");
        toplinkVersion.setFieldName("LOADBUILD.toplink_version");
        descriptor.addMapping(toplinkVersion);

        DirectToFieldMapping machine = new DirectToFieldMapping();
        machine.setAttributeName("machine");
        machine.setFieldName("LOADBUILD.machine");
        descriptor.addMapping(machine);

        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("numberOfTests");
        directtofieldmapping3.setFieldName("LOADBUILD.numberOfTests");
        descriptor.addMapping(directtofieldmapping3);

        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("timestamp");
        directtofieldmapping4.setFieldName("LOADBUILD.lbtimestamp");
        descriptor.addMapping(directtofieldmapping4);

        DirectToFieldMapping directtofieldmapping5 = new DirectToFieldMapping();
        directtofieldmapping5.setAttributeName("userName");
        directtofieldmapping5.setFieldName("LOADBUILD.lbuserName");
        descriptor.addMapping(directtofieldmapping5);

        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("results");
        onetomanymapping.setGetMethodName("getResultsHolder");
        onetomanymapping.setSetMethodName("setResultsHolder");
        onetomanymapping.setReferenceClass(org.eclipse.persistence.testing.framework.TestResult.class);
        onetomanymapping.setIsPrivateOwned(true);
        //onetomanymapping.addTargetForeignKeyFieldName("RESULT.lbuildTimestamp", "LOADBUILD.lbtimestamp");
        onetomanymapping.addTargetForeignKeyFieldName("RESULT.lbuildId", "LOADBUILD.id");
        descriptor.addMapping(onetomanymapping);

        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping1 = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping1.setAttributeName("summaries");
        onetomanymapping1.setGetMethodName("getSummariesHolder");
        onetomanymapping1.setSetMethodName("setSummariesHolder");
        onetomanymapping1.setReferenceClass(org.eclipse.persistence.testing.framework.TestResultsSummary.class);
        onetomanymapping1.setIsPrivateOwned(true);
        //onetomanymapping1.addTargetForeignKeyFieldName("SUMMARY.lbuildTimestamp", "LOADBUILD.lbtimestamp");
        onetomanymapping1.addTargetForeignKeyFieldName("SUMMARY.lbuildId", "LOADBUILD.id");
        descriptor.addMapping(onetomanymapping1);

        addDescriptor(descriptor);
    }

    protected void buildTestResultDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.framework.TestResult.class);
        Vector vector = new Vector();
        vector.addElement("RESULT");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("RESULT.id");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("RESULT_SEQ");
        descriptor.setSequenceNumberFieldName("id");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("description");
        directtofieldmapping.setGetMethodName("getDescription");
        directtofieldmapping.setSetMethodName("setDescription");
        directtofieldmapping.setFieldName("RESULT.description");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping testTimemapping = new DirectToFieldMapping();
        testTimemapping.setAttributeName("testTime");
        testTimemapping.setFieldName("RESULT.test_time");
        descriptor.addMapping(testTimemapping);

        DirectToFieldMapping totalTimemapping = new DirectToFieldMapping();
        totalTimemapping.setAttributeName("totalTime");
        totalTimemapping.setFieldName("RESULT.total_time");
        descriptor.addMapping(totalTimemapping);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("exception");
        directtofieldmapping1.setGetMethodName("getExceptionStackTraceForDatabase");
        directtofieldmapping1.setSetMethodName("setExceptionStackTrace");
        directtofieldmapping1.setFieldName("RESULT.exception");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("id");
        directtofieldmapping2.setGetMethodName("getId");
        directtofieldmapping2.setSetMethodName("setId");
        directtofieldmapping2.setFieldName("RESULT.id");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("name");
        directtofieldmapping3.setGetMethodName("getName");
        directtofieldmapping3.setSetMethodName("setName");
        directtofieldmapping3.setFieldName("RESULT.name");
        descriptor.addMapping(directtofieldmapping3);

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("outcome");
        directtofieldmapping4.setGetMethodName("getOutcome");
        directtofieldmapping4.setSetMethodName("setOutcome");
        directtofieldmapping4.setFieldName("RESULT.outcome");
        descriptor.addMapping(directtofieldmapping4);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("loadBuildSummary");
        onetoonemapping.setGetMethodName("getLoadBuildSummaryHolder");
        onetoonemapping.setSetMethodName("setLoadBuildSummaryHolder");
        onetoonemapping.setReferenceClass(org.eclipse.persistence.testing.framework.LoadBuildSummary.class);
        //onetoonemapping.addForeignKeyFieldName("RESULT.lbuildTimestamp", "LOADBUILD.lbtimestamp");
        onetoonemapping.addForeignKeyFieldName("RESULT.lbuildId", "LOADBUILD.id");
        descriptor.addMapping(onetoonemapping);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping1 = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping1.setAttributeName("summary");
        onetoonemapping1.setGetMethodName("getSummaryHolder");
        onetoonemapping1.setSetMethodName("setSummaryHolder");
        onetoonemapping1.setReferenceClass(org.eclipse.persistence.testing.framework.TestResultsSummary.class);
        onetoonemapping1.addForeignKeyFieldName("RESULT.summaryId", "SUMMARY.id");
        descriptor.addMapping(onetoonemapping1);
        addDescriptor(descriptor);
    }

    protected void buildTestResultsSummaryDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.framework.TestResultsSummary.class);
        Vector vector = new Vector();
        vector.addElement("SUMMARY");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("SUMMARY.id");

        // SECTION: PROPERTIES
        descriptor.setSequenceNumberName("RESULTSUM_SEQ");
        descriptor.setSequenceNumberFieldName("id");

        // SECTION: DIRECTTOFIELDMAPPING
        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("description");
        directtofieldmapping.setGetMethodName("getDescription");
        directtofieldmapping.setSetMethodName("setDescription");
        directtofieldmapping.setFieldName("SUMMARY.description");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping testTimeMapping = new DirectToFieldMapping();
        testTimeMapping.setAttributeName("totalTime");
        testTimeMapping.setFieldName("SUMMARY.total_time");
        descriptor.addMapping(testTimeMapping);

        DirectToFieldMapping setupFailuresMapping = new DirectToFieldMapping();
        setupFailuresMapping.setAttributeName("setupFailures");
        setupFailuresMapping.setFieldName("SUMMARY.setup_failures");
        descriptor.addMapping(setupFailuresMapping);

        DirectToFieldMapping directtofieldmapping1 = new DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("errors");
        directtofieldmapping1.setGetMethodName("getErrors");
        directtofieldmapping1.setSetMethodName("setErrors");
        directtofieldmapping1.setFieldName("SUMMARY.errors");
        descriptor.addMapping(directtofieldmapping1);

        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("fatalErrors");
        directtofieldmapping2.setGetMethodName("getFatalErrors");
        directtofieldmapping2.setSetMethodName("setFatalErrors");
        directtofieldmapping2.setFieldName("SUMMARY.fatalErrors");
        descriptor.addMapping(directtofieldmapping2);

        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("id");
        directtofieldmapping3.setGetMethodName("getId");
        directtofieldmapping3.setSetMethodName("setId");
        directtofieldmapping3.setFieldName("SUMMARY.id");
        descriptor.addMapping(directtofieldmapping3);

        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("name");
        directtofieldmapping4.setGetMethodName("getName");
        directtofieldmapping4.setSetMethodName("setName");
        directtofieldmapping4.setFieldName("SUMMARY.name");
        descriptor.addMapping(directtofieldmapping4);

        DirectToFieldMapping directtofieldmapping5 = new DirectToFieldMapping();
        directtofieldmapping5.setAttributeName("passed");
        directtofieldmapping5.setGetMethodName("getPassed");
        directtofieldmapping5.setSetMethodName("setPassed");
        directtofieldmapping5.setFieldName("SUMMARY.passed");
        descriptor.addMapping(directtofieldmapping5);

        DirectToFieldMapping directtofieldmapping6 = new DirectToFieldMapping();
        directtofieldmapping6.setAttributeName("problems");
        directtofieldmapping6.setGetMethodName("getProblems");
        directtofieldmapping6.setSetMethodName("setProblems");
        directtofieldmapping6.setFieldName("SUMMARY.problems");
        descriptor.addMapping(directtofieldmapping6);

        DirectToFieldMapping directtofieldmapping7 = new DirectToFieldMapping();
        directtofieldmapping7.setAttributeName("setupException");
        directtofieldmapping7.setGetMethodName("getSetupExceptionStackTrace");
        directtofieldmapping7.setSetMethodName("setSetupExceptionStackTrace");
        directtofieldmapping7.setFieldName("SUMMARY.setupException");
        descriptor.addMapping(directtofieldmapping7);

        DirectToFieldMapping directtofieldmapping8 = new DirectToFieldMapping();
        directtofieldmapping8.setAttributeName("totalTests");
        directtofieldmapping8.setGetMethodName("getTotalTests");
        directtofieldmapping8.setSetMethodName("setTotalTests");
        directtofieldmapping8.setFieldName("SUMMARY.totalTests");
        descriptor.addMapping(directtofieldmapping8);

        DirectToFieldMapping directtofieldmapping9 = new DirectToFieldMapping();
        directtofieldmapping9.setAttributeName("warnings");
        directtofieldmapping9.setGetMethodName("getWarnings");
        directtofieldmapping9.setSetMethodName("setWarnings");
        directtofieldmapping9.setFieldName("SUMMARY.warnings");
        descriptor.addMapping(directtofieldmapping9);

        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("results");
        onetomanymapping.setGetMethodName("getResultsHolder");
        onetomanymapping.setSetMethodName("setResultsHolder");
        onetomanymapping.setReferenceClass(org.eclipse.persistence.testing.framework.TestResult.class);
        onetomanymapping.setIsPrivateOwned(true);
        onetomanymapping.addTargetForeignKeyFieldName("RESULT.summaryId", "SUMMARY.id");
        descriptor.addMapping(onetomanymapping);

        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("loadBuildSummary");
        onetoonemapping.setGetMethodName("getLoadBuildSummaryHolder");
        onetoonemapping.setSetMethodName("setLoadBuildSummaryHolder");
        onetoonemapping.setReferenceClass(org.eclipse.persistence.testing.framework.LoadBuildSummary.class);
        //onetoonemapping.addForeignKeyFieldName("SUMMARY.lbuildTimestamp", "LOADBUILD.lbtimestamp");
        onetoonemapping.addForeignKeyFieldName("SUMMARY.lbuildId", "LOADBUILD.id");
        descriptor.addMapping(onetoonemapping);

        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping1 = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping1.setAttributeName("parent");
        onetoonemapping1.setGetMethodName("getParentHolder");
        onetoonemapping1.setSetMethodName("setParentHolder");
        onetoonemapping1.setReferenceClass(org.eclipse.persistence.testing.framework.TestResultsSummary.class);
        onetoonemapping1.addForeignKeyFieldName("SUMMARY.parentId", "SUMMARY.id");
        descriptor.addMapping(onetoonemapping1);
        addDescriptor(descriptor);
    }
}
