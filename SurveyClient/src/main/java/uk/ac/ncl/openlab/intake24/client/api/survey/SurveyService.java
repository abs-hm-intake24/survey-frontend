package uk.ac.ncl.openlab.intake24.client.api.survey;

import com.google.gwt.core.client.GWT;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;
import uk.ac.ncl.openlab.intake24.client.api.auth.AccessDispatcher;
import uk.ac.ncl.openlab.intake24.client.survey.CompletedSurvey;

import javax.ws.rs.*;

@Options(dispatcher=AccessDispatcher.class, serviceRootKey = "intake24-api")
public interface SurveyService extends RestService {

    SurveyService INSTANCE = GWT.create(SurveyService.class);

    @GET
    @Path("/surveys/{id}/parameters")
    void getSurveyParameters(@PathParam("id") String surveyId, MethodCallback<SurveyParameters> callback);

    @GET
    @Path("/surveys/{id}/follow-up")
    void getFollowUpUrl(@PathParam("id") String surveyId, MethodCallback<SurveyFollowUp> callback);

    @POST
    @Path("/surveys/{id}/submissions")
    void submitSurvey(@PathParam("id") String surveyId, CompletedSurvey survey, MethodCallback<SurveySubmissionResponse> callback);

    @GET
    @Path("/surveys/{id}/user-info")
    void getUserData(@PathParam("id") String surveyId, @QueryParam("tz") String timeZone, MethodCallback<UserData> callback);
}
