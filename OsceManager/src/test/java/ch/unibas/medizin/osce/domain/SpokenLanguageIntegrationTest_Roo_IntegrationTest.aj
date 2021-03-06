// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.SpokenLanguageDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SpokenLanguageIntegrationTest_Roo_IntegrationTest {
    
    declare @type: SpokenLanguageIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: SpokenLanguageIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: SpokenLanguageIntegrationTest: @Transactional;
    
    @Autowired
    private SpokenLanguageDataOnDemand SpokenLanguageIntegrationTest.dod;
    
    @Test
    public void SpokenLanguageIntegrationTest.testCountSpokenLanguages() {
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", dod.getRandomSpokenLanguage());
        long count = ch.unibas.medizin.osce.domain.SpokenLanguage.countSpokenLanguages();
        org.junit.Assert.assertTrue("Counter for 'SpokenLanguage' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testFindSpokenLanguage() {
        ch.unibas.medizin.osce.domain.SpokenLanguage obj = dod.getRandomSpokenLanguage();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguage(id);
        org.junit.Assert.assertNotNull("Find method for 'SpokenLanguage' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'SpokenLanguage' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testFindAllSpokenLanguages() {
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", dod.getRandomSpokenLanguage());
        long count = ch.unibas.medizin.osce.domain.SpokenLanguage.countSpokenLanguages();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'SpokenLanguage', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<ch.unibas.medizin.osce.domain.SpokenLanguage> result = ch.unibas.medizin.osce.domain.SpokenLanguage.findAllSpokenLanguages();
        org.junit.Assert.assertNotNull("Find all method for 'SpokenLanguage' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'SpokenLanguage' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testFindSpokenLanguageEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", dod.getRandomSpokenLanguage());
        long count = ch.unibas.medizin.osce.domain.SpokenLanguage.countSpokenLanguages();
        if (count > 20) count = 20;
        java.util.List<ch.unibas.medizin.osce.domain.SpokenLanguage> result = ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguageEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'SpokenLanguage' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'SpokenLanguage' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testFlush() {
        ch.unibas.medizin.osce.domain.SpokenLanguage obj = dod.getRandomSpokenLanguage();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguage(id);
        org.junit.Assert.assertNotNull("Find method for 'SpokenLanguage' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifySpokenLanguage(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'SpokenLanguage' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testMerge() {
        ch.unibas.medizin.osce.domain.SpokenLanguage obj = dod.getRandomSpokenLanguage();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguage(id);
        boolean modified =  dod.modifySpokenLanguage(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        ch.unibas.medizin.osce.domain.SpokenLanguage merged =  obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'SpokenLanguage' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", dod.getRandomSpokenLanguage());
        ch.unibas.medizin.osce.domain.SpokenLanguage obj = dod.getNewTransientSpokenLanguage(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'SpokenLanguage' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'SpokenLanguage' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void SpokenLanguageIntegrationTest.testRemove() {
        ch.unibas.medizin.osce.domain.SpokenLanguage obj = dod.getRandomSpokenLanguage();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'SpokenLanguage' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguage(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'SpokenLanguage' with identifier '" + id + "'", ch.unibas.medizin.osce.domain.SpokenLanguage.findSpokenLanguage(id));
    }
    
}
