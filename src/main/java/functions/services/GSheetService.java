package functions.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import functions.api.FormLabelValue;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GSheetService {

    private static final Dotenv dotenv =
            Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    private static final String APPLICATION_NAME = "Google-Sheets-API-Java-Quickstart";
    private static final String SHEET_ID = dotenv.get("DEFAULT_SHEET_ID");
    private static final String CORPORATE_EVENT_TAB = "CorporateEventRequest";
    private static final String FIELD_TRIP_EVENT_TAB = "FieldTripEventRequest";
    private static final String CELEBRATION_EVENT_TAB = "CelebrationEventRequest";

    private static final String EMAIL_SIGNUP_TAB = "EmailSignups";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static GoogleCredentials getCredentials() throws IOException {
        GoogleCredentials googleCredentials;
        System.out.println("file path: " + CREDENTIALS_FILE_PATH);
        googleCredentials = GoogleCredentials.getApplicationDefault().createScoped(SCOPES);
        return googleCredentials;
    }

    // Populate ValueRange
    static ValueRange requestBuilder(List<FormLabelValue> values, String range) {

        return new ValueRange()
                .setValues(
                        Arrays.asList(values.stream().map(FormLabelValue::getValue).collect(Collectors.toList())))
                .setMajorDimension("ROWS")
                .setRange(range);
    }

    public static Sheets getSheetInstance() throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public void addEmailRow(String emailValue, String source, String id) {
        StringBuilder rangeBuilder = new StringBuilder();
        rangeBuilder.append(EMAIL_SIGNUP_TAB);
        rangeBuilder.append("!A:Z");
        final String range = rangeBuilder.toString();

        LocalDate currentDate = LocalDate.now();

        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        List<Object> emailRow = Arrays.asList(formattedDate, source, emailValue, id);
        ValueRange valueRange = new ValueRange()
                .setValues(
                        Arrays.asList(emailRow))
                .setMajorDimension("ROWS")
                .setRange(range);
        addToSheetRow(range, valueRange);
    }

    public Sheets addSheetRow(String eventRequestType, List<FormLabelValue> r) {
        String tab = null;

        switch (eventRequestType) {
            case "corporate":
                tab = CORPORATE_EVENT_TAB;
                break;
            case "fieldtrip":
                tab = FIELD_TRIP_EVENT_TAB;
                break;
            case "celebration":
                tab = CELEBRATION_EVENT_TAB;
        }

        if (tab == null) {
            return null;
        }
        // Build a new authorized API client service.
        StringBuilder rangeBuilder = new StringBuilder();

        rangeBuilder.append(tab);
        rangeBuilder.append("!A:Z");

        final String range = rangeBuilder.toString();

        ValueRange requestBody = requestBuilder(r, range);

        var s = addToSheetRow(range, requestBody);
        return s;
    }

    private Sheets addToSheetRow(String range, ValueRange requestBody) {
        try {
            Sheets s = getSheetInstance();
            s.spreadsheets()
                    .values()
                    .append(SHEET_ID, range, requestBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            return s;
        } catch (IOException ex) {
            System.out.println("IO");
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
            return null;
        } catch (GeneralSecurityException ex) {
            System.out.println("SEC");
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
            return null;
        }
    }
}
