package ch.unibas.medizin.osce.client.a_nonroo.client.ui.roleAssignment;

import java.util.Set;

import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInSemesterProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedRoleProxy;
import ch.unibas.medizin.osce.shared.OSCESecurityStatus;
import ch.unibas.medizin.osce.shared.PatientAveragePerPost;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author spec
 *
 */
public interface OsceDaySubView extends IsWidget{
	
    public interface Presenter {
        void goTo(Place place);
    }
	/**
	 * Implemented by the owner of the view.
	 */
	interface Delegate {

		public void showApplicationLoading(Boolean show);

		void discloserPanelOpened(OsceDayProxy osceDayProxy,
				OsceDaySubViewImpl osceDaySubViewImpl);

		void discloserPanelClosed(OsceDayProxy osceDayProxy,
				OsceDaySubViewImpl osceDaySubViewImpl);
		
		// module 3 d {
		
				void patientInSemesterSelected(PatientInSemesterProxy patientInSemesterProxy, Set<OsceDayProxy> setOsceDayProxy,OsceDaySubViewImpl osceDaySubViewImpl);

				void roleSelectedevent(StandardizedRoleProxy standardizedRoleProxy,
						OsceDaySubViewImpl osceDaySubViewImpl);
				
				// module 3 d }
		
		void onOSCESecurityChange(OsceProxy osceProxy,OSCESecurityStatus osceSecurityStatus,PatientAveragePerPost patientAveragePerPost,boolean isSecurityChange);
		
		public OsceProxy assignSPForHalfDayIsClicked(OsceProxy osceProxy, Boolean isToAssignSPForHalfDay);
		
		
	}

    String[] getPaths();
    
    void setDelegate(Delegate delegate);
    
    void setPresenter(Presenter systemStartActivity);
    
    //Modlue 3: Assignemnt D[
    public VerticalPanel getSequenceVP();
    //Modlue 3: Assignemnt D]

}
