package ch.unibas.medizin.osce.shared.scaffold;



import com.google.gwt.requestfactory.shared.LoggingRequest;
import com.google.gwt.requestfactory.shared.RequestFactory;

/**
 * The base request factory interface for this app. Add
 * new custom request types here without fear of them
 * being managed away by Roo.
 */
@SuppressWarnings("deprecation")
public interface ScaffoldRequestFactory extends RequestFactory {
	
	/**
	 * Return a GWT logging request.
	 */
	LoggingRequest loggingRequest();
	AdministratorRequestNonRoo administratorRequestNonRoo();
	ClinicRequestNonRoo clinicRequestNonRoo();
	DoctorRequestNonRoo doctorRequestNonRoo();
	ScarRequestNonRoo scarRequestNonRoo();
	ProfessionRequestNonRoo professionRequestNonRoo();
	SpokenLanguageRequestNonRoo languageRequestNonRoo();
	NationalityRequestNonRoo nationalityRequestNonRoo();
	StandardizedPatientRequestNonRoo standardizedPatientRequestNonRoo();
	AnamnesisCheckRequestNonRoo anamnesisCheckRequestNonRoo();
	AnamnesisChecksValueRequestNonRoo anamnesisChecksValueRequestNonRoo();
	RoomRequestNonRoo roomRequestNonRoo();
	LogEntryRequestNonRoo logEntryRequestNonRoo();
	LangSkillRequestNonRoo langSkillRequestNonRoo();
	SpecialisationRequestNonRoo specialisationRequestNonRoo();
	RoleTopicRequestNonRoo roleTopicRequestNonRoo();
	MaterialListRequestNonRoo materialListRequestNonRoo();
	FileRequestNonRoo fileRequestNooRoo();
	UsedMaterialRequestNonRoo usedMaterialRequestNonRoo();
    RoleTemplateRequestNonRoo roleTemplateRequestNonRoo();
	RoleTableItemRequestNoonRoo roleTableItemRequestNoonRoo();
	RoleBaseItemRequestNoonRoo roleBaseItemRequestNoonRoo();
	SimpleSearchCriteriaRequestNonRoo simpleSearchCriteriaRequestNonRoo();
	AdvancedSearchCriteriaNonRoo advancedSearchCriteriaNonRoo();

	StandardizedRoleRequestNonRoo  standardizedRoleRequestNonRoo();
	KeywordRequestNonRoo keywordRequestNonRoo();
	RoleParticipantRequestNonRoo roleParticipantRequestNonRoo();
	ChecklistTopicRequestNonRoo checklistTopicRequestNonRoo();
	ChecklistQuestionRequestNonRoo checklistQuestionRequestNonRoo();
	RoleTableItemValueRequestNonRoo roleTableItemValueRequestNonRoo();
	OsceRequestNonRoo osceRequestNonRoo();
	
	AnamnesisCheckTitleRequestNonRoo anamnesisCheckTitleRequestNonRoo();
	OsceSequenceRequestNonRoo osceSequenceRequestNonRoo();
	PatientInSemesterRequestNonRoo patientInSemesterRequestNonRoo();
	OsceDayRequestNooRoo osceDayRequestNooRoo();

	StudentOsceRequestNonRoo studentOsceRequestNonRoo();
	StudentRequestNonRoo studentRequestNonRoo();
	
	PatientInRoleRequestNonRoo patientInRoleRequestNonRoo();
	
	MainSkillRequestNonRoo mainSkillRequestNonRoo();
	MinorSkillRequestNonRoo minorSkillRequestNonRoo();
	ClassificationTopicRequestNonRoo classificationTopicRequestNonRoo();
	TopicRequestNonRoo topicRequestNonRoo();
	SkillRequestNonRoo skillRequestNonRoo();
	OscePostBluePrintRequestNonRoo oscePostBluePrintRequestNonRoo();
	AssignmentRequestNonRoo assignmentRequestNonRoo();
	OscePostRoomRequestNonRoo oscePostRoomRequestNonRoo();
	SkillHasApplianceRequestNonRoo skillHasApplianceRequestNonRoo();
	
	OscePostRequestNonRoo oscePostRequestNonRoo();
	
	ChecklistOptionRequestNonRooo checklistOptionRequestNonRooo();
	
	ChecklistCriteriaRequestNonRooo checklistCriteriaRequestNonRooo();
	
	AnswerRequestNonRoo answerRequestNonRoo();
	CourseRequestNonRoo courseRequestNonRoo();
	
	SemesterRequestNonRoo semesterRequestNonRoo();
	
	BucketInformationRequestNonRoo bucketInformationRequestNonRoo();
	
	ItemAnalysisNonRoo itemAnalysisNonRoo();
	
	SpPortalPersonRequestNonRoo spPortalPersonRequestNonRoo();
	
	SpStandardizedPatientRequestNonRoo spStandardizedPatientRequestNonRoo();
	
	OsceDateRequestNonRoo osceDateRequestNonRoo();
	
	TrainingDateRequestNonRoo trainingDateRequestNonRoo();
	
	TrainingBlockRequestNonRoo trainingBlockRequestNonRoo();
	
	TrainingSuggestionRequestNonRoo trainingSuggestionRequestNonRoo();
	
	TrainingRequestNonRoo trainingRequestNonRoo();
	
	ChecklistItemRequestNonRoo checklistItemRequestNonRoo();
	
	OsceSettingsRequestNonRoo osceSettingsRequestNonRoo();
}
