package ch.unibas.medizin.osce.client.a_nonroo.client.activity;


import ch.unibas.medizin.osce.client.a_nonroo.client.place.StatisticalEvaluationDetailsPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.request.OsMaRequestFactory;
import ch.unibas.medizin.osce.shared.Operation;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

public class StatisticalEvaluationDetailsActivityMapper implements ActivityMapper {
	
	
	private OsMaRequestFactory requests;
	private PlaceController placeController;
	
	@Inject
	public StatisticalEvaluationDetailsActivityMapper(OsMaRequestFactory requests, PlaceController placeController) {
		this.requests = requests;
		this.placeController = placeController;
	}

	@Override
	public Activity getActivity(Place place) {
		
		System.out.println("========================StatisticalEvaluation getActivity()=========================");
		Log.debug("im StatisticalEvaluation.getActivity");
		if (place instanceof StatisticalEvaluationDetailsPlace) {
			Log.info("test");
			if(((StatisticalEvaluationDetailsPlace) place).getOperation() == Operation.DETAILS)
			{
				Log.info("test");
				return new StatisticalEvaluationDetailsActivity((StatisticalEvaluationDetailsPlace) place, requests, placeController);
			}
			else
			{
				Log.info("test1");
			}
			
		/*	
			if(((CircuitDetailsPlace) place).getOperation() == Operation.EDIT)
			{
				System.out.println("========================Call CircuitEditActivity getActivity() EDIT=========================");
				return new CircuitEditActivity((CircuitDetailsPlace) place, requests, placeController);
			}
			if(((CircuitDetailsPlace) place).getOperation() == Operation.CREATE)
			{
				System.out.println("========================Call CircuitCreateActivity getActivity() EDIT=========================");
				return new CircuitEditActivity((CircuitDetailsPlace) place, requests, placeController, Operation.CREATE);																
			}
		*/	//return new CircuitEditActivity((CircuitDetailsPlace) place, requests, placeController,  CircuitDetailsPlace.Operation.CREATE);
			//return new CircuitDetailsActivity((CircuitDetailsPlace) place, requests, placeController);
			
			// TODO uncomment and implement lines below!
			//if(((CircuitDetailsPlace) place).getOperation() == CircuitDetailsPlace.Operation.EDIT)
			//	return new CircuitEditActivity((CircuitDetailsPlace) place, requests, placeController);
			//if(((CircuitDetailsPlace) place).getOperation() == CircuitDetailsPlace.Operation.CREATE)
			//	return new CircuitEditActivity((CircuitDetailsPlace) place, requests, placeController,  CircuitDetailsPlace.Operation.CREATE);
		}

		return null;
	}

}
