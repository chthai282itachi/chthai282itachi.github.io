// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.request;

import java.util.Date;
import java.util.List;

import ch.unibas.medizin.osce.client.a_nonroo.client.CloudConfigurationProxy;
import ch.unibas.medizin.osce.shared.Sorting;
import ch.unibas.medizin.osce.shared.TimeBell;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("ch.unibas.medizin.osce.domain.Assignment")
public interface AssignmentRequest extends RequestContext {

    abstract InstanceRequest<ch.unibas.medizin.osce.client.managed.request.AssignmentProxy, java.lang.Void> persist();

    abstract InstanceRequest<ch.unibas.medizin.osce.client.managed.request.AssignmentProxy, java.lang.Void> remove();

    abstract Request<java.lang.Long> countAssignments();

    abstract Request<ch.unibas.medizin.osce.client.managed.request.AssignmentProxy> findAssignment(Long id);

    abstract Request<java.util.List<ch.unibas.medizin.osce.client.managed.request.AssignmentProxy>> findAllAssignments();

    abstract Request<java.util.List<ch.unibas.medizin.osce.client.managed.request.AssignmentProxy>> findAssignmentEntries(int firstResult, int maxResults);

    abstract Request<List<AssignmentProxy>> retrieveAssignmentsOfTypeStudent(Long osceId);

	abstract Request<List<AssignmentProxy>> retrieveAssignments(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeStudent(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeSP(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	abstract Request<List<AssignmentProxy>> retrieveAssignmenstOfTypeExaminer(Long osceDayId,Long osceSequenceId,Long courseId,Long oscePostId);

	abstract Request<List<AssignmentProxy>> retrieveLogicalStudentInBreak(Long osceDayId,Long courseId);

	abstract Request<List<AssignmentProxy>> retrieveAssignmentOfLogicalBreakPost(Long osceDayId,Long osceSequenceId);
	//Testing task {
	
	//Testing task }

		// Module10 Create plans		
		abstract Request<List<AssignmentProxy>> findAssignmentsBySPIdandSemesterId(long spId,long semId,long pirId);
		// E Module10 Create plans
		
		//Module 9
		abstract Request<List<StandardizedPatientProxy>> findAssignedSP(Long semesterId);
	    
	    abstract Request<List<DoctorProxy>> findAssignedExaminer(Long semesterId);
	    
		//Module 9

	// Module : 15
	abstract Request<List<AssignmentProxy>> getAssignmentsBySemester(
	Long semesterId);

	abstract Request<Integer> getCountAssignmentsBySemester(
	Long semesterId);

	abstract Request<String> getQwtBellSchedule(// List<AssignmentProxy>
														// assignmentProxies,
			Long semesterId, Integer time, TimeBell isPlusTime);

	// Module : 15
	
	abstract Request<List<AssignmentProxy>> findAssignedDoctorBySpecialisation(Long specialisationId, Long clinicId);
	
	abstract Request<Void> updateAssignmentByDiff(Long osceDayId, int diff, Date endTimeSlot, boolean isChangeStartTime);
	
	abstract Request<List<Date>> minmumStartTime(Long osceDayId,Long osceSequenceId,Long courseId);
	
	//payment 
	abstract Request<String> findAssignmentByPatinetInRole(Long semesterId);
	//payment

	//by spec change[
	abstract Request<Boolean> exchangeStudent(AssignmentProxy ass, Long studentId);
	abstract Request<Boolean> exchangeStandardizedPatient(AssignmentProxy ass, PatientInRoleProxy exchangePir);
	//by spec change]
	
	//payment change
	abstract Request<Long> countStandardizedPatientBySemester(Long semesterId);
	abstract Request<List<StandardizedPatientProxy>> findStandardizedPatientBySemester(int start, int max, String colName, Sorting sortType, Long semesterId);
	//payment change
	
	//deactivate student change
	abstract Request<Boolean> deactivateStudentFromAssignment(StudentOscesProxy stud);
	abstract Request<Boolean> activateStudentFromAssignment(StudentOscesProxy stud);
	//deactivate student change
	
	abstract Request<List<Date>> clearExaminerAssignment(Long osceDayId,Long oscePostId,Long courseId);
	
	abstract Request<Void> shiftLongBreak(AssignmentProxy currOsceDay, int nextPrevFlag);
	
	abstract Request<OsceDayProxy> updateTimeForOsceDay(Long osceDayId, int newStartTime, int newLunchTime);
	
	abstract Request<Void> moveLunchBreakOsceDay(int flag, Long osceDayId);
	
	abstract Request<Void> updateLunchBreak(Long osceDayId, int diff);
	
	abstract Request<OsceProxy> removeManualOsceAssignmentByOsceId(Long osceId);
	
	abstract Request<CloudConfigurationProxy> findCloudConfigurationFromFile();
}
