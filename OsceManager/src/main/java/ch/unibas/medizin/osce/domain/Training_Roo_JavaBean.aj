// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.PatientInSemester;
import ch.unibas.medizin.osce.domain.Semester;
import java.lang.String;
import java.util.Date;
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
    
    public Date Training.getTrainingDate() {
        return this.trainingDate;
    }
    
    public void Training.setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }
    
    public Date Training.getTimeStart() {
        return this.timeStart;
    }
    
    public void Training.setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }
    
    public Date Training.getTimeEnd() {
        return this.timeEnd;
    }
    
    public void Training.setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public Set<PatientInSemester> Training.getPatientInSemesters() {
        return this.patientInSemesters;
    }
    
    public void Training.setPatientInSemesters(Set<PatientInSemester> patientInSemesters) {
        this.patientInSemesters = patientInSemesters;
    }
    
}