// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.PatientInSemester;
import ch.unibas.medizin.osce.domain.Semester;
import ch.unibas.medizin.osce.domain.SemesterDataOnDemand;
import ch.unibas.medizin.osce.domain.StandardizedPatient;
import ch.unibas.medizin.osce.domain.StandardizedPatientDataOnDemand;
import java.lang.Boolean;
import java.lang.Integer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

privileged aspect PatientInSemesterDataOnDemand_Roo_DataOnDemand {
    
    declare @type: PatientInSemesterDataOnDemand: @Component;
    
    private Random PatientInSemesterDataOnDemand.rnd = new SecureRandom();
    
    private List<PatientInSemester> PatientInSemesterDataOnDemand.data;
    
    @Autowired
    private SemesterDataOnDemand PatientInSemesterDataOnDemand.semesterDataOnDemand;
    
    @Autowired
    private StandardizedPatientDataOnDemand PatientInSemesterDataOnDemand.standardizedPatientDataOnDemand;
    
    public PatientInSemester PatientInSemesterDataOnDemand.getNewTransientPatientInSemester(int index) {
        PatientInSemester obj = new PatientInSemester();
        setAccepted(obj, index);
        setSemester(obj, index);
        setStandardizedPatient(obj, index);
        setValue(obj, index);
        return obj;
    }
    
    public void PatientInSemesterDataOnDemand.setAccepted(PatientInSemester obj, int index) {
        Boolean accepted = Boolean.TRUE;
        obj.setAccepted(accepted);
    }
    
    public void PatientInSemesterDataOnDemand.setSemester(PatientInSemester obj, int index) {
        Semester semester = semesterDataOnDemand.getRandomSemester();
        obj.setSemester(semester);
    }
    
    public void PatientInSemesterDataOnDemand.setStandardizedPatient(PatientInSemester obj, int index) {
        StandardizedPatient standardizedPatient = standardizedPatientDataOnDemand.getRandomStandardizedPatient();
        obj.setStandardizedPatient(standardizedPatient);
    }
    
    public void PatientInSemesterDataOnDemand.setValue(PatientInSemester obj, int index) {
        Integer value = 0;
        obj.setValue(value);
    }
    
    public PatientInSemester PatientInSemesterDataOnDemand.getSpecificPatientInSemester(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        PatientInSemester obj = data.get(index);
        return PatientInSemester.findPatientInSemester(obj.getId());
    }
    
    public PatientInSemester PatientInSemesterDataOnDemand.getRandomPatientInSemester() {
        init();
        PatientInSemester obj = data.get(rnd.nextInt(data.size()));
        return PatientInSemester.findPatientInSemester(obj.getId());
    }
    
    public boolean PatientInSemesterDataOnDemand.modifyPatientInSemester(PatientInSemester obj) {
        return false;
    }
    
    public void PatientInSemesterDataOnDemand.init() {
        data = PatientInSemester.findPatientInSemesterEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'PatientInSemester' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<ch.unibas.medizin.osce.domain.PatientInSemester>();
        for (int i = 0; i < 10; i++) {
            PatientInSemester obj = getNewTransientPatientInSemester(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                    ConstraintViolation<?> cv = it.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}
