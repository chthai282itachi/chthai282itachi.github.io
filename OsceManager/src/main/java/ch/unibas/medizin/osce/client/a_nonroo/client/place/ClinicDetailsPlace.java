package ch.unibas.medizin.osce.client.a_nonroo.client.place;


import ch.unibas.medizin.osce.client.a_nonroo.client.place.ClinicDetailsPlace.Operation;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.requestfactory.shared.EntityProxyId;
import com.google.gwt.requestfactory.shared.RequestFactory;

public class ClinicDetailsPlace extends Place {
	
	
	private String token;
	
	public enum Operation {
		DETAILS, EDIT, CREATE
	}
	private static final String SEPARATOR = "!";

	private EntityProxyId<?> proxyId;
	private Operation operation = null;
	
	public EntityProxyId<?> getProxyId() {
		return proxyId;
	}

	public ClinicDetailsPlace(){
		this.token = "SystemStartPlace";
	}
	
	public ClinicDetailsPlace(EntityProxyId<?> record) {
		this(record, Operation.DETAILS);
	}
	
	public ClinicDetailsPlace(Operation operation) {
		this.operation = operation;
	}

    public ClinicDetailsPlace(EntityProxyId<?> stableId, Operation operation) {
		this.operation = operation;
		proxyId = stableId;
	}

	public Operation getOperation() {
		return operation;
	}
	
	public ClinicDetailsPlace(String token){
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Tokenizer.
	 */

	public static class Tokenizer implements PlaceTokenizer<ClinicDetailsPlace> {
		private final RequestFactory requests;

		public Tokenizer(RequestFactory requests) {
			Log.debug("ClinicDetailsPlace.Tokenizer");
			this.requests = requests;
		}

		public ClinicDetailsPlace getPlace(String token) {
			Log.debug("ClinicDetailsPlace.Tokenizer.getPlace");
			String bits[] = token.split(SEPARATOR);
			Operation operation = Operation.valueOf(bits[1]);
			if (Operation.DETAILS == operation) {
				return new ClinicDetailsPlace(requests.getProxyId(bits[0]), Operation.DETAILS);
			}
			if (Operation.EDIT == operation) {
				return new ClinicDetailsPlace(requests.getProxyId(bits[0]), Operation.EDIT);
			}
			if (Operation.CREATE == operation) {
				return new ClinicDetailsPlace(/*requests.getProxyId(bits[0]), */Operation.CREATE);
			}

			return new ClinicDetailsPlace(token);
		}

		public String getToken(ClinicDetailsPlace place) {
			Log.debug("ClinicDetailsPlace.Tokenizer.getToken");
			if (Operation.DETAILS == place.getOperation()) {
				return place.getProxyId() + SEPARATOR + ClinicDetailsPlace.Operation.DETAILS;
			}
			if (Operation.EDIT == place.getOperation()) {
				return place.getProxyId() + SEPARATOR + ClinicDetailsPlace.Operation.EDIT;
			}
			if (Operation.CREATE == place.getOperation()) {
				return /*place.getProxyId() + SEPARATOR + */ClinicDetailsPlace.Operation.CREATE.toString();
			}
        
			return place.getToken();
		}
	}


	

}
