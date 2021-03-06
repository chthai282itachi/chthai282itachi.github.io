package ch.unibas.medizin.osce.client.a_nonroo.client.activity;

import ch.unibas.medizin.osce.client.a_nonroo.client.place.DoctorDetailsPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.request.OsMaRequestFactory;
import ch.unibas.medizin.osce.shared.Operation;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

public class DoctorDetailsActivityMapper  implements ActivityMapper{

	
	   private OsMaRequestFactory requests;
	   private PlaceController placeController;

		@Inject
		    public DoctorDetailsActivityMapper(OsMaRequestFactory requests, PlaceController placeController) {
		        this.requests = requests;
		        this.placeController = placeController;
		    }

	@Override
	public Activity getActivity(Place place) {
		Log.debug("im DoctorDetailsActivityMapper.getActivity");
		 if (place instanceof DoctorDetailsPlace){
			 if(((DoctorDetailsPlace) place).getOperation() == Operation.DETAILS)
				 return new DoctorDetailsActivity((DoctorDetailsPlace) place, requests, placeController);
			 if(((DoctorDetailsPlace) place).getOperation() == Operation.EDIT)
				 return new DoctorEditActivity((DoctorDetailsPlace) place, requests, placeController);
			 if(((DoctorDetailsPlace) place).getOperation() == Operation.CREATE)
				 return new DoctorEditActivity((DoctorDetailsPlace) place, requests, placeController,  Operation.CREATE);
			 if(((DoctorDetailsPlace) place).getOperation() == Operation.NEW)
				 return new DoctorDetailsActivity((DoctorDetailsPlace) place, requests, placeController);
		 }
		return null;
	}
}
