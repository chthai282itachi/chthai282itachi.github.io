package ch.unibas.medizin.osce.client.a_nonroo.client.ui;

import ch.unibas.medizin.osce.client.managed.request.BucketInformationProxy;
import ch.unibas.medizin.osce.client.style.widgets.IconButton;
import ch.unibas.medizin.osce.shared.ExportOsceType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public interface ExportOsceView extends IsWidget {
	
	public interface Presenter {
		void goTo(Place place);
	}

	public interface Delegate {
		public void exporteOSCEButtonClicked(Boolean flag);
		public void processedClicked(ExportOsceType osceType);
		public void unprocessedClicked(ExportOsceType osceType);
		public Boolean checkSelectedValue();
		
		public void bucketSaveButtonClicked(BucketInformationProxy proxy, String bucketName, String accessKey, String secretKey, String encryptionKey, String basePath, Boolean isFTP);
		public void exportiOSCEButtonClicked(Boolean value);
		public void eOsceClicked();
		public void iOsceClicked();
	}

	public VerticalPanel getFileListPanel();

	void setDelegate(Delegate delegate);

	void setPresenter(Presenter systemStartActivity);

	boolean checkRadio();
	
	public TextBox getBucketName();
	
	public TextBox getAccessKey();
	
	public TextBox getSecretKey();
	
	public IconButton getSaveEditButton();
	
	public IconButton getCancelButton();
	
	public BucketInformationProxy getBucketInformationProxy();
	
	public void setBucketInformationProxy(BucketInformationProxy bucketInformationProxy);

	TextBox getBasePath();

	TextBox getEncryptionKey();

	RadioButton getS3();

	RadioButton getFtp();

	void typeValueChanged(boolean isFTP);
	
	public RadioButton getProcessed();
	
	public RadioButton getUnprocessed();
	
	public RadioButton geteOSCE();
	
	public RadioButton getiOSCE();
}
