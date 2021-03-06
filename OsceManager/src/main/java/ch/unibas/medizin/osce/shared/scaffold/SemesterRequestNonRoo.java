package ch.unibas.medizin.osce.shared.scaffold;

import java.util.List;

import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OscePostProxy;
import ch.unibas.medizin.osce.client.managed.request.SemesterProxy;
import ch.unibas.medizin.osce.domain.OsceDay;
import ch.unibas.medizin.osce.domain.Semester;

import com.google.gwt.requestfactory.shared.Request;
import com.google.gwt.requestfactory.shared.RequestContext;
import com.google.gwt.requestfactory.shared.Service;

@SuppressWarnings("deprecation")
@Service(Semester.class)
public interface SemesterRequestNonRoo extends RequestContext 
{
	public abstract Request<List<SemesterProxy>> findAllSemesterOrderByYearAndSemester();	
	
	public abstract Request<List<OsceDayProxy>> findAllOsceDayBySemester(Long semesterId);

	public abstract Request<Boolean> surveyIsStartedSoPushDataToSpPortal(Long id);

	public abstract Request<Boolean> stopSurveyAndPushDateToOsceFromSpPortal(Long id);

	public abstract Request<Boolean> checkAllSpIsAssignInRole(Long semId);

	public abstract Request<List<OscePostProxy>> findTotalPostOfTheSemester(Long semId);

	public abstract Request<Boolean> findIsAssignTrainingDateAndOsceDate(Long id);

}