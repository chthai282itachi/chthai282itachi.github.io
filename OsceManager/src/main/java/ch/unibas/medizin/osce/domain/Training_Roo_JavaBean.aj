// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.PatientInSemester;
import ch.unibas.medizin.osce.domain.Semester;
import java.lang.String;
import java.util.Set;

privileged aspect Training_Roo_JavaBean {
    
    public String Training.getName() {
        return this.name;
    }
    
    public void Training.setName(String name) {
        this.name = name;
    }
    
    public Semester Training.getSemester() {
        return this.semester;
    }
    
    public void Training.setSemester(Semester semester) {
        this.semester = semester;
    }
    
    public Set<PatientInSemester> Training.getPatientInSemesters() {
        return this.patientInSemesters;
    }
    
    public void Training.setPatientInSemesters(Set<PatientInSemester> patientInSemesters) {
        this.patientInSemesters = patientInSemesters;
    }
    
}
