package ch.unibas.medizin.osce.server;

public class OsMaFilePathConstant {
		
		// Module 9 Start
		
		public static String DEFAULT_MAIL_TEMPLATE_PATH = "usr/oscemanager/Templates/mailTemplates/";
		//public static String DEFAULT_MAIL_TEMPLATE_PATH = "C:\\Templates\\mailTemplates\\";
		public static String DEFAULT_MAIL_TEMPLATE = "./osMaEntry/gwt/unibas/templates/defaultTemplate.txt";
		public static String FROM_MAIL_ID = "userId@gmail.com";
		public static String FROM_NAME = "Mail Sender";
		public static String MAIL_SUBJECT = "Invitaion from OSCE";
				
		//Module 9 End
		public static String DEFAULT_IMPORT_EOSCE_PATH = "usr/oscemanager/eOSCE/import/";
		//public static String DEFAULT_IMPORT_EOSCE_PATH = "C:\\oscemanager\\eOSCE\\imporst\\";
		
		//CSV File Upload Path
		  public static String CSV_FILEPATH = "osMaEntry/gwt/unibas/role/images/";
		 // public static String CSV_FILEPATH = "osMaEntry\\gwt\\unibas\\role\\images\\";
		  
		  //EXCEL File Upload Path For Learning Objective
		  public static String EXCEL_FILEPATH = "osMaEntry/gwt/unibas/role/images/";
		  //public static String EXCEL_FILEPATH = "osMaEntry\\gwt\\unibas\\role\\images\\";
		  
		//Role Module
		public static String ROLE_IMAGE_FILEPATH = "usr/oscemanager/role/images/";
		  //public static String ROLE_IMAGE_FILEPATH = "c:\\oscemanager\\role\\images\\";
				
		public static String PRINT_SCHEDULE_TEMPLATE = "usr/oscemanager/TEMPLATES/PRINTPLAN_TEMPLATE/";
		//  public static final String PRINT_SCHEDULE_TEMPLATE="C:\\TEMPLATES\\PRINTPLAN_TEMPLATE\\";
		
		//Export OSCE File Path
		public static String EXPORT_OSCE_PROCESSED_FILEPATH = "usr/oscemanager/eosce/export/processed/";
		//public static String EXPORT_OSCE_PROCESSED_FILEPATH = "c:\\oscemanager\\eosce\\export\\processed\\";
		public static String EXPORT_OSCE_UNPROCESSED_FILEPATH = "usr/oscemanager/eosce/export/unprocessed/";
		//public static String EXPORT_OSCE_UNPROCESSED_FILEPATH = "c:\\oscemanager\\eosce\\export\\unprocessed\\";
		
		//Module 8 (Assessment Plan)[
		
				//image path under webapps
				public static String appImageUploadDirectory="/osMaEntry/gwt/unibas/sp/images";
				
				//path of outside of webapps for parmanent storage of images
				//linux
				public static String localImageUploadDirectory="usr/oscemanager/sp/images/";				
				//windows
				//public static String localImageUploadDirectory="d://sp//images/";
				
				
				public static String realImagePath="";
				//public static String imagesrcPath="/osMaEntry/gwt/unibas/sp/images/";
				
				//video path under webapps
				public static String appVideoUploadDirectory="/osMaEntry/gwt/unibas/sp/videos";
				
				//path of outside of webapps for parmanent storage of images
				//linux
				public static String localVideoUploadDirectory="usr/oscemanager/sp/images/";
				//windows
				//public static String localVideoUploadDirectory="d://sp//videos/";
				
				public static String realVideoPath="";//
				//public static String videosrcPath="/osMaEntry/gwt/unibas/sp/videos/";
				
				
				//Module 8]
	

}