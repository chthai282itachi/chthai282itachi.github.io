package ch.unibas.medizin.osce.client.a_nonroo.client.dmzsync;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface eOSCESyncServiceAsync {
	
	void deleteAmzonS3Object(List<String> fileList, String bucketName, String accessKey, String secretKey, AsyncCallback<Void> cb);
	void processedFileList(Long semesterID, AsyncCallback<List<String>> cb);
	void unprocessedFileList(Long semesterID, AsyncCallback<List<String>> cb);
	void importFileList(List<String> fileList, Boolean flag, String bucketName, String accessKey, String secretKey, AsyncCallback<Void> cb);
	
	//export
	void exportOsceFile(Long semesterID, AsyncCallback<Void> cb);
	void exportProcessedFileList(AsyncCallback<List<String>> cb);
	void exportUnprocessedFileList(AsyncCallback<List<String>> cb);
	void putAmazonS3Object(String bucketName, String accessKey, String secretKey, List<String> fileList, Boolean flag, AsyncCallback<Void> cb);
}
