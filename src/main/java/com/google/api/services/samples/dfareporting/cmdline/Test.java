package com.google.api.services.samples.dfareporting.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.DimensionValueList;
import com.google.api.services.dfareporting.model.File;
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.UserProfileList;
import com.google.common.collect.ImmutableList;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Nathan.Qiu@OMD.com (Your Name Here)
 * 
 */
public class Test {
  
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/dfareporting_sample");


  private static FileDataStoreFactory dataStoreFactory;

  private static final List<String> SCOPES = ImmutableList.of(
      "https://www.googleapis.com/auth/dfareporting");

  private static HttpTransport httpTransport;
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
  private static final int MAX_LIST_PAGE_SIZE = 50;
  private static final int MAX_REPORT_PAGE_SIZE = 10;
  
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            DfaReportingSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=dfareporting "
          + "into dfareporting-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }
  
  private static Dfareporting initializeDfareporting() throws Exception {
    Credential credential = authorize();

    // Create DFA Reporting client.
    return new Dfareporting.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("APPLICATION_NAME_GOES_HERE").build();
  }
  
  public static void main(String[] args) {

    // Set up the date range we plan to use.
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.add(Calendar.DATE, -1);
    Date oneDayAgo = calendar.getTime();

    String startDate = DATE_FORMATTER.format(oneDayAgo);
    String endDate = DATE_FORMATTER.format(today);
//    String startDate = "2014-06-01";
//    String endDate = "2014-08-30";

    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      Dfareporting reporting = initializeDfareporting();

      UserProfileList userProfiles = GetAllUserProfiles.list(reporting);
      // Get an example user profile ID, so we can run the following samples.
      Long userProfileId = userProfiles.getItems().get(2).getProfileId();

      DimensionValueList advertisers = GetDimensionValues.query(reporting, "dfa:advertiser",
          userProfileId, startDate, endDate, MAX_LIST_PAGE_SIZE);

      if ((advertisers.getItems() != null) && !advertisers.getItems().isEmpty()) {
        // Get an advertiser, so we can run the rest of the samples.
        DimensionValue advertiser = advertisers.getItems().get(0);

//        Report standardReport = CreateStandardReport.insert(reporting, userProfileId,
//            advertiser, startDate, endDate);
        Report standardReport = CreateStandardReport.insert(reporting, userProfileId,
            advertiser, startDate, endDate);
//        GetCompatibleFields.run(reporting, userProfileId, standardReport);
        File file = GenerateReportFile.run(reporting, userProfileId, standardReport, true);

        if (file != null) {
          // If the report file generation did not fail, display results.
          Sqlconnection.connect(DownloadReportFile.run(reporting, file));
        }
      }

     /* DimensionValueList floodlightConfigIds = GetDimensionValues.query(reporting,
          "dfa:floodlightConfigId", userProfileId, startDate, endDate, MAX_LIST_PAGE_SIZE);

      if ((floodlightConfigIds.getItems() != null) && !floodlightConfigIds.getItems().isEmpty()) {
        // Get a Floodlight Config ID, so we can run the rest of the samples.
        DimensionValue floodlightConfigId = floodlightConfigIds.getItems().get(0);

        Report floodlightReport = CreateFloodlightReport.insert(
            reporting, userProfileId, floodlightConfigId, startDate, endDate);

        // Run this report asynchronously, since it would never run synchronously.
        File file = GenerateReportFile.run(reporting, userProfileId, floodlightReport, false);

        if (file != null) {
          // If the report file generation did not fail, display results.
          DownloadReportFile.run(reporting, file);
        }
      }*/

      GetAllReports.list(reporting, userProfileId, MAX_REPORT_PAGE_SIZE);
    } catch (GoogleJsonResponseException e) {
      // Message already includes parsed response.
      System.err.println(e.getMessage());
    } catch (HttpResponseException e) {
      // Message doesn't include parsed response.
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  
  }
}
