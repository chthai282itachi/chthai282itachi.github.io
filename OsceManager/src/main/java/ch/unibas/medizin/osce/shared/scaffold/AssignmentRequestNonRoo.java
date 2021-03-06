
package ch.unibas.medizin.osce.shared.scaffold;

import java.util.Date;
import java.util.List;

import ch.unibas.medizin.osce.client.managed.request.AssignmentProxy;
import ch.unibas.medizin.osce.client.managed.request.DoctorProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInRoleProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedPatientProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentOscesProxy;
import ch.unibas.medizin.osce.domain.Assignment;
import ch.unibas.medizin.osce.shared.Sorting;
import ch.unibas.medizin.osce.shared.TimeBell;

import com.google.gwt.requestfactory.shared.Request;
import com.google.gwt.requestfactory.shared.RequestContext;
import com.google.gwt.requestfactory.shared.Service;

@SuppressWarnings("deprecation")
@Service(Assignment.class)
public interface AssignmentRequestNonRoo extends RequestContext{

	public abstract Request<List<AssignmentProxy>> retrieveAssignmentsOfTypeStudent(Long osceId);

	public abstract Request<List<AssignmentProxy>> retrieveAssignments(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

public abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeStudent(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	public abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeSP(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	public abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeExaminer(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	public abstract Request<List<AssignmentProxy>> retrieveLogicalStudentInBreak(Long osceDayId,Long courseId);

	public abstract Request<List<AssignmentProxy>> retrieveAssignmentOfLogicalBreakPost(Long osceDayId,Long osceSequenceId);
	//Testing task {
	
	//Testing task }

		// Module10 Create plans		
		public abstract Request<List<AssignmentProxy>> findAssignmentsBySPIdandSemesterId(long spId,long semId,long pirId);
		// E Module10 Create plans
		
		//Module 9
		abstract Request<List<StandardizedPatientProxy>> findAssignedSP(Long semesterId);
	    
	    abstract Request<List<DoctorProxy>> findAssignedExaminer(Long semesterId);
	    
		//Module 9

	// Module : 15
	public abstract Request<List<AssignmentProxy>> getAssignmentsBySemester(
			Long semesterId);

	public abstract Request<Integer> getCountAssignmentsBySemester(
			Long semesterId);

	public abstract Request<String> getQwtBellSchedule(// List<AssignmentProxy>
														// assignmentProxies,
			Long semesterId, Integer time, TimeBell isPlusTime);

	// Module : 15
	
	public abstract Request<List<AssignmentProxy>> findAssignedDoctorBySpecialisation(Long specialisationId, Long clinicId);
	
	public abstract Request<Void> updateAssignmentByDiff(Long osceDayId, int diff, Date endTimeSlot, boolean isChangeStartTime);
	
	public abstract Request<List<Date>> minmumStartTime(Long osceDayId,Long osceSequenceId,Long courseId);
	
	//payment 
	public abstract Request<String> findAssignmentByPatinetInRole(Long semesterId);
	//payment

	//by spec change[
	public abstract Request<Boolean> exchangeStudent(AssignmentProxy ass, Long studentId);
	public abstract Request<Boolean> exchangeStandardizedPatient(AssignmentProxy ass, PatientInRoleProxy exchangePir);
	//by spec change]
	
	//payment change
	public abstract Request<Long> countStandardizedPatientBySemester(Long semesterId);
	public abstract Request<List<StandardizedPatientProxy>> findStandardizedPatientBySemester(int start, int max, String colName, Sorting sortType, Long semesterId);
	//payment change
	
	//deactivate student change
	public abstract Request<Boolean> deactivateStudentFromAssignment(StudentOscesProxy stud);
	public abstract Request<Boolean> activateStudentFromAssignment(StudentOscesProxy stud);
	//deactivate student change
	
	public abstract Request<List<Date>> clearExaminerAssignment(Long osceDayId,Long oscePostId,Long courseId);
	
	public abstract Request<Void> shiftLongBreak(AssignmentProxy currOsceDay, int nextPrevFlag);
	
	public abstract Request<OsceDayProxy> updateTimeForOsceDay(Long osceDayId, int newStartTime, int newLunchTime);
	
	public abstract Request<Void> moveLunchBreakOsceDay(int flag, Long osceDayId);
	
	public abstract Request<Void> updateLunchBreak(Long osceDayId, int diff);
	
	public abstract Request<OsceProxy> removeManualOsceAssignmentByOsceId(Long osceId);
}
