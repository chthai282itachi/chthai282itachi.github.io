// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.OsceDayDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect OsceDayIntegrationTest_Roo_IntegrationTest {
    
    declare @type: OsceDayIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: OsceDayIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: OsceDayIntegrationTest: @Transactional;
    
    @Autowired
    private OsceDayDataOnDemand OsceDayIntegrationTest.dod;
    
    @Test
    public void OsceDayIntegrationTest.testCountOsceDays() {
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", dod.getRandomOsceDay());
        long count = ch.unibas.medizin.osce.domain.OsceDay.countOsceDays();
        org.junit.Assert.assertTrue("Counter for 'OsceDay' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void OsceDayIntegrationTest.testFindOsceDay() {
        ch.unibas.medizin.osce.domain.OsceDay obj = dod.getRandomOsceDay();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.OsceDay.findOsceDay(id);
        org.junit.Assert.assertNotNull("Find method for 'OsceDay' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'OsceDay' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void OsceDayIntegrationTest.testFindAllOsceDays() {
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", dod.getRandomOsceDay());
        long count = ch.unibas.medizin.osce.domain.OsceDay.countOsceDays();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'OsceDay', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<ch.unibas.medizin.osce.domain.OsceDay> result = ch.unibas.medizin.osce.domain.OsceDay.findAllOsceDays();
        org.junit.Assert.assertNotNull("Find all method for 'OsceDay' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'OsceDay' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void OsceDayIntegrationTest.testFindOsceDayEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", dod.getRandomOsceDay());
        long count = ch.unibas.medizin.osce.domain.OsceDay.countOsceDays();
        if (count > 20) count = 20;
        java.util.List<ch.unibas.medizin.osce.domain.OsceDay> result = ch.unibas.medizin.osce.domain.OsceDay.findOsceDayEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'OsceDay' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'OsceDay' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void OsceDayIntegrationTest.testFlush() {
        ch.unibas.medizin.osce.domain.OsceDay obj = dod.getRandomOsceDay();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.OsceDay.findOsceDay(id);
        org.junit.Assert.assertNotNull("Find method for 'OsceDay' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyOsceDay(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'OsceDay' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void OsceDayIntegrationTest.testMerge() {
        ch.unibas.medizin.osce.domain.OsceDay obj = dod.getRandomOsceDay();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.OsceDay.findOsceDay(id);
        boolean modified =  dod.modifyOsceDay(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        ch.unibas.medizin.osce.domain.OsceDay merged =  obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'OsceDay' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void OsceDayIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", dod.getRandomOsceDay());
        ch.unibas.medizin.osce.domain.OsceDay obj = dod.getNewTransientOsceDay(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'OsceDay' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'OsceDay' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void OsceDayIntegrationTest.testRemove() {
        ch.unibas.medizin.osce.domain.OsceDay obj = dod.getRandomOsceDay();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'OsceDay' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.OsceDay.findOsceDay(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'OsceDay' with identifier '" + id + "'", ch.unibas.medizin.osce.domain.OsceDay.findOsceDay(id));
    }
    
}
