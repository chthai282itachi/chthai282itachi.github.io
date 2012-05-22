// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.activity;

import ch.unibas.medizin.osce.client.managed.request.AdministratorProxy;
import ch.unibas.medizin.osce.client.managed.request.AdvancedSearchCriteriaProxy;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckProxy;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisChecksValueProxy;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisFormProxy;
import ch.unibas.medizin.osce.client.managed.request.ApplicationEntityTypesProcessor;
import ch.unibas.medizin.osce.client.managed.request.ApplicationRequestFactory;
import ch.unibas.medizin.osce.client.managed.request.AssignmentProxy;
import ch.unibas.medizin.osce.client.managed.request.BankaccountProxy;
import ch.unibas.medizin.osce.client.managed.request.ClinicProxy;
import ch.unibas.medizin.osce.client.managed.request.CourseProxy;
import ch.unibas.medizin.osce.client.managed.request.DescriptionProxy;
import ch.unibas.medizin.osce.client.managed.request.DoctorProxy;
import ch.unibas.medizin.osce.client.managed.request.EliminationCriterionProxy;
import ch.unibas.medizin.osce.client.managed.request.KeywordProxy;
import ch.unibas.medizin.osce.client.managed.request.LangSkillProxy;
import ch.unibas.medizin.osce.client.managed.request.LogEntryProxy;
import ch.unibas.medizin.osce.client.managed.request.MaterialListProxy;
import ch.unibas.medizin.osce.client.managed.request.MediaContentProxy;
import ch.unibas.medizin.osce.client.managed.request.MediaContentTypeProxy;
import ch.unibas.medizin.osce.client.managed.request.NationalityProxy;
import ch.unibas.medizin.osce.client.managed.request.OfficeProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OscePostProxy;
import ch.unibas.medizin.osce.client.managed.request.OscePostRoomProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInRoleProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInSemesterProxy;
import ch.unibas.medizin.osce.client.managed.request.ProfessionProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleParticipantProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTopicProxy;
import ch.unibas.medizin.osce.client.managed.request.RoomProxy;
import ch.unibas.medizin.osce.client.managed.request.ScarProxy;
import ch.unibas.medizin.osce.client.managed.request.SemesterProxy;
import ch.unibas.medizin.osce.client.managed.request.SpecialisationProxy;
import ch.unibas.medizin.osce.client.managed.request.SpokenLanguageProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedPatientProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedRoleProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentOscesProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentProxy;
import ch.unibas.medizin.osce.client.managed.request.TaskProxy;
import ch.unibas.medizin.osce.client.managed.request.UsedMaterialProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyPlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

public abstract class ApplicationDetailsActivities_Roo_Gwt implements ActivityMapper {

    protected ApplicationRequestFactory requests;

    protected PlaceController placeController;

    public Activity getActivity(Place place) {
        if (!(place instanceof ProxyPlace)) {
            return null;
        }
        final ProxyPlace proxyPlace = (ProxyPlace) place;
        return new ApplicationEntityTypesProcessor<Activity>() {

            @Override
            public void handleUsedMaterial(UsedMaterialProxy proxy) {
                setResult(new UsedMaterialActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleTask(TaskProxy proxy) {
                setResult(new TaskActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleStudent(StudentProxy proxy) {
                setResult(new StudentActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleStudentOsces(StudentOscesProxy proxy) {
                setResult(new StudentOscesActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleStandardizedRole(StandardizedRoleProxy proxy) {
                setResult(new StandardizedRoleActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleStandardizedPatient(StandardizedPatientProxy proxy) {
                setResult(new StandardizedPatientActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleSpokenLanguage(SpokenLanguageProxy proxy) {
                setResult(new SpokenLanguageActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleSpecialisation(SpecialisationProxy proxy) {
                setResult(new SpecialisationActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleSemester(SemesterProxy proxy) {
                setResult(new SemesterActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleScar(ScarProxy proxy) {
                setResult(new ScarActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleRoom(RoomProxy proxy) {
                setResult(new RoomActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleRoleTopic(RoleTopicProxy proxy) {
                setResult(new RoleTopicActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleRoleParticipant(RoleParticipantProxy proxy) {
                setResult(new RoleParticipantActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleProfession(ProfessionProxy proxy) {
                setResult(new ProfessionActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handlePatientInSemester(PatientInSemesterProxy proxy) {
                setResult(new PatientInSemesterActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handlePatientInRole(PatientInRoleProxy proxy) {
                setResult(new PatientInRoleActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleOsce(OsceProxy proxy) {
                setResult(new OsceActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleOscePostRoom(OscePostRoomProxy proxy) {
                setResult(new OscePostRoomActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleOscePost(OscePostProxy proxy) {
                setResult(new OscePostActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleOsceDay(OsceDayProxy proxy) {
                setResult(new OsceDayActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleOffice(OfficeProxy proxy) {
                setResult(new OfficeActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleNationality(NationalityProxy proxy) {
                setResult(new NationalityActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleMediaContentType(MediaContentTypeProxy proxy) {
                setResult(new MediaContentTypeActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleMediaContent(MediaContentProxy proxy) {
                setResult(new MediaContentActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleMaterialList(MaterialListProxy proxy) {
                setResult(new MaterialListActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleLogEntry(LogEntryProxy proxy) {
                setResult(new LogEntryActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleLangSkill(LangSkillProxy proxy) {
                setResult(new LangSkillActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleKeyword(KeywordProxy proxy) {
                setResult(new KeywordActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleEliminationCriterion(EliminationCriterionProxy proxy) {
                setResult(new EliminationCriterionActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleDoctor(DoctorProxy proxy) {
                setResult(new DoctorActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleDescription(DescriptionProxy proxy) {
                setResult(new DescriptionActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleCourse(CourseProxy proxy) {
                setResult(new CourseActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleClinic(ClinicProxy proxy) {
                setResult(new ClinicActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleBankaccount(BankaccountProxy proxy) {
                setResult(new BankaccountActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAssignment(AssignmentProxy proxy) {
                setResult(new AssignmentActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAnamnesisForm(AnamnesisFormProxy proxy) {
                setResult(new AnamnesisFormActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAnamnesisChecksValue(AnamnesisChecksValueProxy proxy) {
                setResult(new AnamnesisChecksValueActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAnamnesisCheck(AnamnesisCheckProxy proxy) {
                setResult(new AnamnesisCheckActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAdvancedSearchCriteria(AdvancedSearchCriteriaProxy proxy) {
                setResult(new AdvancedSearchCriteriaActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }

            @Override
            public void handleAdministrator(AdministratorProxy proxy) {
                setResult(new AdministratorActivitiesMapper(requests, placeController).getActivity(proxyPlace));
            }
        }.process(proxyPlace.getProxyClass());
    }
}
