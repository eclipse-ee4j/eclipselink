/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import static java.lang.Integer.MIN_VALUE;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import static org.eclipse.persistence.internal.helper.Helper.buildHexStringFromBytes;

/**
 * <b>PUBLIC</b>: Interface used to categorize arguments to Stored Procedures as either
 * 'simple' (use subclass SimpleDatabaseType) or 'complex' (use subclass ComplexDatabaseType) 
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
@SuppressWarnings("unchecked")
public interface DatabaseType {

    public static final String TARGET_SHORT_PREFIX = "T_";
    public static final String TARGET_SUFFIX = "TARGET";
    public static final String COMPAT_SHORT_PREFIX = "C_";
    public static final String COMPAT_SUFFIX = "COMPAT";
    public static final int ARGNAME_SIZE_LIMIT = 30 - TARGET_SUFFIX.length();

    public boolean isComplexDatabaseType();

    public boolean isJDBCType();
    
    public int getSqlCode();

    public int getConversionCode();
    
    public String getTypeName();

    public int computeInIndex(PLSQLargument inArg, int newIndex,
        ListIterator<PLSQLargument> i);

    public int computeOutIndex(PLSQLargument outArg, int newIndex,
        ListIterator<PLSQLargument> i);

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg);
    
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg);

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call);
    
    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call);

    public void translate(PLSQLargument arg, AbstractRecord translationRow,
        AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
        List<DatabaseField> translationRowFields, List translationRowValues,
        StoredProcedureCall call);

    public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
        DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues);

    public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
        AbstractRecord translationRow, DatabasePlatform platform);
    
    public enum DatabaseTypeHelper {
        databaseTypeHelper;

        static String getTruncatedSHA1Hash(String s) {
        	StringBuilder sb = new StringBuilder(28);
        	try {
        		byte[] longIdentifierBytes = s.getBytes();
        		MessageDigest md = MessageDigest.getInstance("SHA-1");
        		md.update(longIdentifierBytes, 0, longIdentifierBytes.length);
        		byte[] digest = md.digest(); //produces a 160-bit hash
        		//truncate to 112 bits, which is about the same java.util.UUID;
        		//HMAC-SHA1-96 is only 96 bits and that's good enough for IPSEC work
        		//TL;DR - probability of collision quite small
        		byte[] truncDigest = new byte[14];
                System.arraycopy(digest, 0, truncDigest, 0, 14);
                sb.append(buildHexStringFromBytes(truncDigest));
    		}
        	catch (NoSuchAlgorithmException e) {
    			//ignore: should never happen
    		}
        	return sb.toString();
        }
        
        protected String getTruncatedSHA1Name(String argName, String prefix) {
        	if (argName.length() >= ARGNAME_SIZE_LIMIT) {
        		StringBuilder sb = new StringBuilder();
        		//the truncated SHA is great, but a PL/SQL identifier
        		//can't start with a number, so use prefix
        		sb.append(prefix);
        		sb.append(getTruncatedSHA1Hash(argName));
        		return sb.toString();
        	}
        	return argName;
        }
        
        public String buildTarget(PLSQLargument arg) {
        	StringBuilder sb = new StringBuilder();
        	if (arg.name.length() >= ARGNAME_SIZE_LIMIT) {
        		sb.append(getTruncatedSHA1Name(arg.name, TARGET_SHORT_PREFIX));
        	}
        	else {
        		sb.append(arg.name);
        		sb.append(TARGET_SUFFIX);
        	}
            return sb.toString();
        }

        public String buildCompatible(PLSQLargument arg) {
        	StringBuilder sb = new StringBuilder();
        	if (arg.name.length() >= ARGNAME_SIZE_LIMIT) {
        		sb.append(getTruncatedSHA1Name(arg.name, COMPAT_SHORT_PREFIX));
        	}
        	else {
        		sb.append(arg.name);
        		sb.append(COMPAT_SUFFIX);
        	}
            return sb.toString();
        }
        
        public void declareTarget(StringBuilder sb, PLSQLargument arg,
            DatabaseType databaseType) {
            sb.append("  ");
            sb.append(buildTarget(arg));
            sb.append(" ");
            sb.append(databaseType.getTypeName());
        }

        public int computeInIndex(PLSQLargument inArg, int newIndex) {
            inArg.inIndex = newIndex;
            return ++newIndex;
        }
        
        public int computeOutIndex(PLSQLargument outArg, int newIndex) {
            outArg.outIndex = newIndex;
            return ++newIndex;
        }
        public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
            sb.append("  :");
            sb.append(outArg.outIndex);
            sb.append(" := ");
            sb.append(buildTarget(outArg));
            sb.append(";");
            sb.append(NL);
        }

        public void translate(PLSQLargument arg, AbstractRecord translationRow,
                AbstractRecord copyOfTranslationRow, List copyOfTranslationFields,
                List translationRowFields, List translationRowValues,
                StoredProcedureCall call) {
            DatabaseField field = null;
            for (Iterator i = copyOfTranslationFields.iterator(); i.hasNext(); ) {
                DatabaseField f = (DatabaseField)i.next();
                if (f.getName().equals(arg.name)) {
                    field = f;
                    break;
                }
            }
            if (arg.length != MIN_VALUE) {
                field.setLength(arg.length);
            }
            if (arg.precision != MIN_VALUE) {
                field.setPrecision(arg.precision);
            }
            if (arg.scale != MIN_VALUE) {
                field.setScale(arg.scale);
            }
            translationRowFields.set(arg.inIndex - 1, field);
            Object value = copyOfTranslationRow.get(field);
            translationRowValues.set(arg.inIndex - 1, value);
        }

        public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
                DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
            DatabaseField field = null;
            for (Iterator i = outputRowFields.iterator(); i.hasNext(); ) {
                DatabaseField f = (DatabaseField)i.next();
                if (f.getName().equals(outArg.name)) {
                    field = f;
                    break;
                }
            }
            Object value = outputRow.get(field);
            newOutputRow.add(field, value);
        }
        
        public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
            AbstractRecord translationRow, DatabasePlatform platform) {
            if (direction == IN && arg.inIndex != MIN_VALUE) {
                sb.append(":");
                sb.append(arg.inIndex);
                sb.append(" => ");
                sb.append(platform.convertToDatabaseType(translationRow.get(arg.name)));
            }
            if (direction == OUT && arg.outIndex != MIN_VALUE) {
                sb.append(arg.name);
                sb.append(" => :");
                sb.append(arg.outIndex);
            }
        }
    }
}
