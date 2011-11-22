package ch.unibas.medizin.osce.client.a_nonroo.client.activity;

import ch.unibas.medizin.osce.client.a_nonroo.client.place.AnamnesisCheckDetailsPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.place.AnamnesisCheckPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.request.OsMaRequestFactory;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckDetailsView;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckDetailsViewImpl;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckProxy;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.requestfactory.shared.Receiver;
import com.google.gwt.requestfactory.shared.ServerFailure;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.SingleSelectionModel;

public class AnamnesisCheckDetailsActivity extends AbstractActivity implements
AnamnesisCheckDetailsView.Presenter, AnamnesisCheckDetailsView.Delegate {

	private OsMaRequestFactory requests;
	private PlaceController placeController;
	private AcceptsOneWidget widget;
	private AnamnesisCheckDetailsView view;
	private CellTable<AnamnesisCheckProxy> table;
	private SingleSelectionModel<AnamnesisCheckProxy> selectionModel;
	private HandlerRegistration rangeChangeHandler;

	private AnamnesisCheckDetailsPlace place;
	private AnamnesisCheckProxy anamnesisCheckProxy;


	public AnamnesisCheckDetailsActivity(AnamnesisCheckDetailsPlace place, OsMaRequestFactory requests, PlaceController placeController) {
		this.place = place;
		this.requests = requests;
		this.placeController = placeController;
	}

	public void onStop(){

	}
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Log.info("AnamnesisCheckDetailsActivity.start()");
		AnamnesisCheckDetailsView anamnesisCheckDetailsView = new AnamnesisCheckDetailsViewImpl();
		anamnesisCheckDetailsView.setPresenter(this);
		this.widget = panel;
		this.view = anamnesisCheckDetailsView;
		widget.setWidget(anamnesisCheckDetailsView.asWidget());

		view.setDelegate(this);

		requests.find(place.getProxyId()).fire(new Receiver<Object>() {

			public void onFailure(ServerFailure error){
				Log.error(error.getMessage());
			}
			@Override
			public void onSuccess(Object response) {
				if(response instanceof AnamnesisCheckProxy){
					Log.info(((AnamnesisCheckProxy) response).getId().toString());
					init((AnamnesisCheckProxy) response);
				}
			}
		});
	}

	private void init(AnamnesisCheckProxy anamnesisCheckProxy) {
		this.anamnesisCheckProxy = anamnesisCheckProxy;
		view.setValue(anamnesisCheckProxy);
	}

	@Override
	public void goTo(Place place) {
		placeController.goTo(place);
	}

	@Override
	public void editClicked() {
		Log.info("edit clicked");
		goTo(new AnamnesisCheckDetailsPlace(anamnesisCheckProxy.stableId(),
				AnamnesisCheckDetailsPlace.Operation.EDIT));
	}

	@Override
	public void deleteClicked() {
		if (!Window.confirm("Really delete this entry? You cannot undo this change.")) {
			return;
		}
		requests.anamnesisCheckRequest().remove().using(anamnesisCheckProxy).fire(new Receiver<Void>() {

			public void onSuccess(Void ignore) {
				if (widget == null) {
					return;
				}
				placeController.goTo(new AnamnesisCheckPlace("AnamnesisCheckPlace!DELETED"));
			}
		});
	}
}
