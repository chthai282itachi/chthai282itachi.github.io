// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.PatientInRoleDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PatientInRoleIntegrationTest_Roo_IntegrationTest {
    
    declare @type: PatientInRoleIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: PatientInRoleIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");
    
    declare @type: PatientInRoleIntegrationTest: @Transactional;
    
    @Autowired
    private PatientInRoleDataOnDemand PatientInRoleIntegrationTest.dod;
    
    @Test
    public void PatientInRoleIntegrationTest.testCountPatientInRoles() {
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", dod.getRandomPatientInRole());
        long count = ch.unibas.medizin.osce.domain.PatientInRole.countPatientInRoles();
        org.junit.Assert.assertTrue("Counter for 'PatientInRole' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testFindPatientInRole() {
        ch.unibas.medizin.osce.domain.PatientInRole obj = dod.getRandomPatientInRole();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRole(id);
        org.junit.Assert.assertNotNull("Find method for 'PatientInRole' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'PatientInRole' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testFindAllPatientInRoles() {
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", dod.getRandomPatientInRole());
        long count = ch.unibas.medizin.osce.domain.PatientInRole.countPatientInRoles();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'PatientInRole', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<ch.unibas.medizin.osce.domain.PatientInRole> result = ch.unibas.medizin.osce.domain.PatientInRole.findAllPatientInRoles();
        org.junit.Assert.assertNotNull("Find all method for 'PatientInRole' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'PatientInRole' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testFindPatientInRoleEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", dod.getRandomPatientInRole());
        long count = ch.unibas.medizin.osce.domain.PatientInRole.countPatientInRoles();
        if (count > 20) count = 20;
        java.util.List<ch.unibas.medizin.osce.domain.PatientInRole> result = ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRoleEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'PatientInRole' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'PatientInRole' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testFlush() {
        ch.unibas.medizin.osce.domain.PatientInRole obj = dod.getRandomPatientInRole();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRole(id);
        org.junit.Assert.assertNotNull("Find method for 'PatientInRole' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPatientInRole(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'PatientInRole' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testMerge() {
        ch.unibas.medizin.osce.domain.PatientInRole obj = dod.getRandomPatientInRole();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRole(id);
        boolean modified =  dod.modifyPatientInRole(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        ch.unibas.medizin.osce.domain.PatientInRole merged =  obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'PatientInRole' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", dod.getRandomPatientInRole());
        ch.unibas.medizin.osce.domain.PatientInRole obj = dod.getNewTransientPatientInRole(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'PatientInRole' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'PatientInRole' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void PatientInRoleIntegrationTest.testRemove() {
        ch.unibas.medizin.osce.domain.PatientInRole obj = dod.getRandomPatientInRole();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'PatientInRole' failed to provide an identifier", id);
        obj = ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRole(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'PatientInRole' with identifier '" + id + "'", ch.unibas.medizin.osce.domain.PatientInRole.findPatientInRole(id));
    }
    
}
