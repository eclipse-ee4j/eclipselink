/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.bigbad;

import java.util.*;

import java.math.*;

import java.io.*;

import org.eclipse.persistence.indirection.*;

/**
 * A very large object used for performance testing.
 * Object has 100 attributes,
 * 10 part primary key,
 * 2 blobs,
 * 10 large strings.
 */
public class BigBadObject implements Serializable {
    public long id01;
    public long id02;
    public long id03;
    public long id04;
    public long id05;
    public long id06;
    public long id07;
    public long id08;
    public long id09;
    public long id10;

    public byte[] blob;
    public List serializedBlob;

    public String largeString01;
    public String largeString02;
    public String largeString03;

    public BigDecimal number01;
    public BigDecimal number02;

    public String string01;
    public String string02;
    public String string03;
    public String string04;
    public String string05;
    public String string06;
    public String string07;
    public String string08;
    public String string09;
    public String string10;
    public String string11;
    public String string12;
    public String string13;
    public String string14;
    public String string15;
    public String string16;
    public String string17;
    public String string18;
    public String string19;
    public String string20;

    public Calendar calendar01;
    public Calendar calendar02;
    public Calendar calendar03;
    public Calendar calendar04;
    public Calendar calendar05;
    public Calendar calendar06;
    public Calendar calendar07;
    public Calendar calendar08;
    public Calendar calendar09;
    public Calendar calendar10;

    public java.sql.Date date01;
    public java.sql.Date date02;
    public java.sql.Date date03;
    public java.sql.Date date04;
    public java.sql.Date date05;
    public java.sql.Date date06;
    public java.sql.Date date07;
    public java.sql.Date date08;
    public java.sql.Date date09;
    public java.sql.Date date10;

    public java.sql.Time time01;
    public java.sql.Time time02;
    public java.sql.Time time03;
    public java.sql.Time time04;
    public java.sql.Time time05;
    public java.sql.Time time06;
    public java.sql.Time time07;
    public java.sql.Time time08;
    public java.sql.Time time09;
    public java.sql.Time time10;

    public java.sql.Timestamp timestamp01;
    public java.sql.Timestamp timestamp02;
    public java.sql.Timestamp timestamp03;
    public java.sql.Timestamp timestamp04;
    public java.sql.Timestamp timestamp05;
    public java.sql.Timestamp timestamp06;
    public java.sql.Timestamp timestamp07;
    public java.sql.Timestamp timestamp08;
    public java.sql.Timestamp timestamp09;
    public java.sql.Timestamp timestamp10;

    public ValueHolderInterface ref01;
    public ValueHolderInterface ref02;
    public ValueHolderInterface ref03;
    public ValueHolderInterface ref04;
    public ValueHolderInterface ref05;
    public ValueHolderInterface ref06;
    public ValueHolderInterface ref07;
    public ValueHolderInterface ref08;
    public ValueHolderInterface ref09;
    public ValueHolderInterface ref10;

    public BigBadAggregate agg01;
    public BigBadAggregate agg02;
    public BigBadAggregate agg03;
    public BigBadAggregate agg04;
    public BigBadAggregate agg05;
}
