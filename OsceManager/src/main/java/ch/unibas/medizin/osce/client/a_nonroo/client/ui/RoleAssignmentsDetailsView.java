package ch.unibas.medizin.osce.client.a_nonroo.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface RoleAssignmentsDetailsView extends IsWidget{
	
	
	
    public interface Presenter {
        void goTo(Place place);
    }
	/**
	 * Implemented by the owner of the view.
	 */
	interface Delegate {
		// TODO define methods to be delegated!
	}
	
    //public void setValue(RoleProxy proxy); 
  
    void setDelegate(Delegate delegate);
    
    void setPresenter(Presenter osceActivity);
}
