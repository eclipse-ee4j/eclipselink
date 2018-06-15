/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types
package org.eclipse.persistence.jpa.converter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.converter.converters.BooleanToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.ByteToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.CharToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.DoubleToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.FloatToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.IntToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.LongToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.converters.ShortToStringAutoApplyConverter;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityB2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityBW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityBy2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityByW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityC2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityCW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityD2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityDW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityF2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityFW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityI2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityIW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityL2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityLW2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntitySh2S;
import org.eclipse.persistence.jpa.converter.model.autoapply.ConvertEntityShW2S;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestAutoApplyConverter {
    @Emf(createTables = DDLGen.DROP_CREATE, 
         classes = { ConvertEntityB2S.class, ConvertEntityBW2S.class, ConvertEntityBy2S.class, ConvertEntityByW2S.class,
                 ConvertEntityC2S.class, ConvertEntityCW2S.class, ConvertEntityD2S.class, ConvertEntityDW2S.class, 
                 ConvertEntityF2S.class, ConvertEntityFW2S.class, ConvertEntityI2S.class, ConvertEntityIW2S.class, 
                 ConvertEntityL2S.class, ConvertEntityLW2S.class, ConvertEntitySh2S.class, ConvertEntityShW2S.class,
                 BooleanToStringAutoApplyConverter.class, ByteToStringAutoApplyConverter.class,
                 CharToStringAutoApplyConverter.class, DoubleToStringAutoApplyConverter.class,
                 FloatToStringAutoApplyConverter.class, IntToStringAutoApplyConverter.class,
                 LongToStringAutoApplyConverter.class, ShortToStringAutoApplyConverter.class },
         properties = {@Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emfAutoApplyConverters;

    @Test
    public void persistTestBooleanPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);

        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        boolean valueConvert = true;
        boolean valueNoConvert = false;
        try {
            BooleanToStringAutoApplyConverter.reset();
            ConvertEntityB2S entity = new ConvertEntityB2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(BooleanToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Boolean(valueConvert), BooleanToStringAutoApplyConverter.ctdcVal);
            BooleanToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityB2S findEntity = em.find(ConvertEntityB2S.class, id);
            Assert.assertTrue(BooleanToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Boolean(valueConvert).toString(), BooleanToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestBooleanWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Boolean valueConvert = Boolean.TRUE;
        Boolean valueNoConvert = Boolean.FALSE;
        try {
            BooleanToStringAutoApplyConverter.reset();
            ConvertEntityBW2S entity = new ConvertEntityBW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(BooleanToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, BooleanToStringAutoApplyConverter.ctdcVal);
            BooleanToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityBW2S findEntity = em.find(ConvertEntityBW2S.class, id);
            Assert.assertTrue(BooleanToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), BooleanToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestBytePrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        byte valueConvert = (byte) 10;
        byte valueNoConvert = (byte) 15;
        try {
            ByteToStringAutoApplyConverter.reset();
            ConvertEntityBy2S entity = new ConvertEntityBy2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(ByteToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Byte(valueConvert), ByteToStringAutoApplyConverter.ctdcVal);
            ByteToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityBy2S findEntity = em.find(ConvertEntityBy2S.class, id);
            Assert.assertTrue(ByteToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Byte(valueConvert).toString(), ByteToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestByteWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Byte valueConvert = new Byte((byte) 10);
        Byte valueNoConvert = new Byte((byte) 14);
        try {
            ByteToStringAutoApplyConverter.reset();
            ConvertEntityByW2S entity = new ConvertEntityByW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(ByteToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, ByteToStringAutoApplyConverter.ctdcVal);
            ByteToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityByW2S findEntity = em.find(ConvertEntityByW2S.class, id);
            Assert.assertTrue(ByteToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), ByteToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestCharPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        char valueConvert = 'A';
        char valueNoConvert = 'z';
        try {
            CharToStringAutoApplyConverter.reset();
            ConvertEntityC2S entity = new ConvertEntityC2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(CharToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Character(valueConvert), CharToStringAutoApplyConverter.ctdcVal);
            CharToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityC2S findEntity = em.find(ConvertEntityC2S.class, id);
            Assert.assertTrue(CharToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Character(valueConvert).toString(), CharToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestCharWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Character valueConvert = new Character('A');
        Character valueNoConvert = new Character('z');
        try {
            CharToStringAutoApplyConverter.reset();
            ConvertEntityCW2S entity = new ConvertEntityCW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(CharToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, CharToStringAutoApplyConverter.ctdcVal);
            CharToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityCW2S findEntity = em.find(ConvertEntityCW2S.class, id);
            Assert.assertTrue(CharToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), CharToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestDoublePrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        double valueConvert = 4200.0;
        double valueNoConvert = 10000.0;
        try {
            DoubleToStringAutoApplyConverter.reset();
            ConvertEntityD2S entity = new ConvertEntityD2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(DoubleToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Double(valueConvert), DoubleToStringAutoApplyConverter.ctdcVal);
            DoubleToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityD2S findEntity = em.find(ConvertEntityD2S.class, id);
            Assert.assertTrue(DoubleToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Double(valueConvert).toString(), DoubleToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert(), 0.01);
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert(), 0.01);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestDoubleWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Double valueConvert = new Double(42.0);
        Double valueNoConvert = new Double(100.0);
        try {
            DoubleToStringAutoApplyConverter.reset();
            ConvertEntityDW2S entity = new ConvertEntityDW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(DoubleToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, DoubleToStringAutoApplyConverter.ctdcVal);
            DoubleToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityDW2S findEntity = em.find(ConvertEntityDW2S.class, id);
            Assert.assertTrue(DoubleToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), DoubleToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestFloatPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        float valueConvert = 4200.0f;
        float valueNoConvert = 10000.0f;
        try {
            FloatToStringAutoApplyConverter.reset();
            ConvertEntityF2S entity = new ConvertEntityF2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(FloatToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Float(valueConvert), FloatToStringAutoApplyConverter.ctdcVal);
            FloatToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityF2S findEntity = em.find(ConvertEntityF2S.class, id);
            Assert.assertTrue(FloatToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Float(valueConvert).toString(), FloatToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert(), 0.01);
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert(), 0.01);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestFloatWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Float valueConvert = new Float(42.0f);
        Float valueNoConvert = new Float(100.0f);
        try {
            FloatToStringAutoApplyConverter.reset();
            ConvertEntityFW2S entity = new ConvertEntityFW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(FloatToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, FloatToStringAutoApplyConverter.ctdcVal);
            FloatToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityFW2S findEntity = em.find(ConvertEntityFW2S.class, id);
            Assert.assertTrue(FloatToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), FloatToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestIntegerPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        int valueConvert = 4200;
        int valueNoConvert = 10000;
        try {
            IntToStringAutoApplyConverter.reset();
            ConvertEntityI2S entity = new ConvertEntityI2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(IntToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Integer(valueConvert), IntToStringAutoApplyConverter.ctdcVal);
            IntToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityI2S findEntity = em.find(ConvertEntityI2S.class, id);
            Assert.assertTrue(IntToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Integer(valueConvert).toString(), IntToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestIntegerWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Integer valueConvert = new Integer(42);
        Integer valueNoConvert = new Integer(100);
        try {
            IntToStringAutoApplyConverter.reset();
            ConvertEntityIW2S entity = new ConvertEntityIW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(IntToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, IntToStringAutoApplyConverter.ctdcVal);
            IntToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityIW2S findEntity = em.find(ConvertEntityIW2S.class, id);
            Assert.assertTrue(IntToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), IntToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestLongPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        long valueConvert = 4200;
        long valueNoConvert = 10000;
        try {
            LongToStringAutoApplyConverter.reset();
            ConvertEntityL2S entity = new ConvertEntityL2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(LongToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Long(valueConvert), LongToStringAutoApplyConverter.ctdcVal);
            LongToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityL2S findEntity = em.find(ConvertEntityL2S.class, id);
            Assert.assertTrue(LongToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Long(valueConvert).toString(), LongToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestLongWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Long valueConvert = new Long((long) 42);
        Long valueNoConvert = new Long((long) 100);
        try {
            LongToStringAutoApplyConverter.reset();
            ConvertEntityLW2S entity = new ConvertEntityLW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(LongToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, LongToStringAutoApplyConverter.ctdcVal);
            LongToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityLW2S findEntity = em.find(ConvertEntityLW2S.class, id);
            Assert.assertTrue(LongToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), LongToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestShortPrimitive() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        short valueConvert = 4200;
        short valueNoConvert = 10000;
        try {
            ShortToStringAutoApplyConverter.reset();
            ConvertEntitySh2S entity = new ConvertEntitySh2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(ShortToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(new Short(valueConvert), ShortToStringAutoApplyConverter.ctdcVal);
            ShortToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntitySh2S findEntity = em.find(ConvertEntitySh2S.class, id);
            Assert.assertTrue(ShortToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(new Short(valueConvert).toString(), ShortToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
    
    @Test
    public void persistTestShortWrapper() {
        Assert.assertNotNull(emfAutoApplyConverters);
        
        EntityManager em = emfAutoApplyConverters.createEntityManager();
        long id = System.currentTimeMillis();
        Short valueConvert = new Short((short) 42);
        Short valueNoConvert = new Short((short) 100);
        try {
            ShortToStringAutoApplyConverter.reset();
            ConvertEntityShW2S entity = new ConvertEntityShW2S(id, valueConvert, valueNoConvert);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Assert.assertTrue(ShortToStringAutoApplyConverter.convertToDatabaseTriggered);
            Assert.assertEquals(valueConvert, ShortToStringAutoApplyConverter.ctdcVal);
            ShortToStringAutoApplyConverter.reset();
            
            em.clear();
            
            ConvertEntityShW2S findEntity = em.find(ConvertEntityShW2S.class, id);
            Assert.assertTrue(ShortToStringAutoApplyConverter.convertToEntityTriggered);
            Assert.assertEquals(valueConvert.toString(), ShortToStringAutoApplyConverter.cteaVal);
            Assert.assertNotNull(findEntity);
            Assert.assertNotNull(findEntity.getValueConvert());
            Assert.assertNotNull(findEntity.getValueNoConvert());
            Assert.assertEquals(valueConvert, findEntity.getValueConvert());
            Assert.assertEquals(valueNoConvert, findEntity.getValueNoConvert());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
