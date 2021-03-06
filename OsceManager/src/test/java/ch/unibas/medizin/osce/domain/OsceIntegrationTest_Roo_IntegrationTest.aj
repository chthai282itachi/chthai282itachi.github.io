// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.OsceDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect OsceIntegrationTest_Roo_IntegrationTest {
    
    declare @type: OsceIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: OsceIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: OsceIntegrationTest: @Transactional;
    
    @Autowired
    private OsceDataOnDemand OsceIntegrationTest.dod;
    
    @Test
    public void OsceIntegrationTest.testCountOsces() {
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", dod.getRandomOsce());
        long count = ch.unibas.medizin.osce.domain.Osce.countOsces();
        org.junit.Assert.assertTrue("Counter for 'Osce' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void OsceIntegrationTest.testFindOsce() {
        ch.unibas.medizin.osce.domain.Osce obj = dod.getRandomOsce();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.Osce.findOsce(id);
        org.junit.Assert.assertNotNull("Find method for 'Osce' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Osce' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void OsceIntegrationTest.testFindAllOsces() {
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", dod.getRandomOsce());
        long count = ch.unibas.medizin.osce.domain.Osce.countOsces();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Osce', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<ch.unibas.medizin.osce.domain.Osce> result = ch.unibas.medizin.osce.domain.Osce.findAllOsces();
        org.junit.Assert.assertNotNull("Find all method for 'Osce' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Osce' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void OsceIntegrationTest.testFindOsceEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", dod.getRandomOsce());
        long count = ch.unibas.medizin.osce.domain.Osce.countOsces();
        if (count > 20) count = 20;
        java.util.List<ch.unibas.medizin.osce.domain.Osce> result = ch.unibas.medizin.osce.domain.Osce.findOsceEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Osce' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Osce' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void OsceIntegrationTest.testFlush() {
        ch.unibas.medizin.osce.domain.Osce obj = dod.getRandomOsce();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.Osce.findOsce(id);
        org.junit.Assert.assertNotNull("Find method for 'Osce' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyOsce(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Osce' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void OsceIntegrationTest.testMerge() {
        ch.unibas.medizin.osce.domain.Osce obj = dod.getRandomOsce();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.Osce.findOsce(id);
        boolean modified =  dod.modifyOsce(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        ch.unibas.medizin.osce.domain.Osce merged =  obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Osce' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void OsceIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", dod.getRandomOsce());
        ch.unibas.medizin.osce.domain.Osce obj = dod.getNewTransientOsce(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Osce' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Osce' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void OsceIntegrationTest.testRemove() {
        ch.unibas.medizin.osce.domain.Osce obj = dod.getRandomOsce();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Osce' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.Osce.findOsce(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Osce' with identifier '" + id + "'", ch.unibas.medizin.osce.domain.Osce.findOsce(id));
    }
    
}
