package com.tyss.googlesheet_integrationn.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tyss.googlesheet_integrationn.dto.GoogleSheetDto;
import com.tyss.googlesheet_integrationn.dto.GoogleSheetresponseDTO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class GoogleApiUtil {
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens/path";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GoogleApiUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
//						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						// if we get token expired or user error modify 65 th line like 67 th line
						.setDataStoreFactory(new FileDataStoreFactory(
								new java.io.File(System.getProperty("user.home"), TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public Map<Object, Object> getdatafromSheet() throws GeneralSecurityException, IOException {
		final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
		final String range = "Class Data!A2:E";
		Sheets service = getSheetService();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		Map<Object, Object> readdata = new HashMap<Object, Object>();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			System.out.println("data featched succesfully");
			for (List row : values) {
				// Print columns A and E, which correspond to indices 0 and 4.
				readdata.put(row.get(0), row.get(4));
//				System.out.printf("%s, %s\n", row.get(0), row.get(4));
			}
			return readdata;
		}
		return readdata;
	}

	private Sheets getSheetService() throws GeneralSecurityException, IOException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		return service;
	}

	public GoogleSheetresponseDTO createGoogleSheet(GoogleSheetDto request)
			throws GeneralSecurityException, IOException {
		Sheets service = getSheetService();
		SpreadsheetProperties properties = new SpreadsheetProperties();
		properties.setTitle(request.getSheetName());
		SheetProperties sheetProperties = new SheetProperties();
		// setTitle("demo") will come below the sheet
		sheetProperties.setTitle(request.getSheetName());
		Sheet sheet = new Sheet().setProperties(sheetProperties);
		Spreadsheet spreadsheet = new Spreadsheet().setProperties(properties)
				.setSheets(Collections.singletonList(sheet));

		Spreadsheet createdResponse = service.spreadsheets().create(spreadsheet).execute();
		GoogleSheetresponseDTO dto = new GoogleSheetresponseDTO();

		// to insert data
		ValueRange data = new ValueRange().setValues(request.getDataToBeUpdated());
		// A1 first row first column
		service.spreadsheets().values().update(createdResponse.getSpreadsheetId(), "A:Z", data)
				.setValueInputOption("RAW").execute();

		dto.setSspreadSheetid(createdResponse.getSpreadsheetId());
		dto.setSpreadSheetURL(createdResponse.getSpreadsheetUrl());
		return dto;

//		return service.spreadsheets().create(spreadsheet).execute().getSpreadsheetUrl(); 

	}
}
//	public void writeSomething(List<Data> myData) {
//
//	    try {
//	        String id = "INSERT_SHEET_ID";
//	        String writeRange = "INSERT_SHEET_NAME!A3:E";
//	        Sheets service = getSheetService();
//	        List<List<Object>> writeData = new ArrayList<>();
//	        for (Data someData: myData) {
//	            List<Object> dataRow = new ArrayList<>();
//	            dataRow.add(someData.data1);
//	            dataRow.add(someData.data2);
//	            dataRow.add(someData.data3);
//	            dataRow.add(someData.data4);
//	            dataRow.add(someData.data5);
//	            writeData.add(dataRow);
//	        }
//
//	        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
//	        service.spreadsheets().values()
//	                .update(id, writeRange, vr)
//	                .setValueInputOption("RAW")
//	                .execute();
//	    } catch (Exception e) {
//	        // handle exception
//	    }

/**
 * Prints the names and majors of students in a sample spreadsheet:
 * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
 */
//	public static void main(String... args) throws IOException, GeneralSecurityException {
//		// Build a new authorized API client service.
//		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//		final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//		final String range = "Class Data!A2:E";
//		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//				.setApplicationName(APPLICATION_NAME).build();
//		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
//		List<List<Object>> values = response.getValues();
//		if (values == null || values.isEmpty()) {
//			System.out.println("No data found.");
//		} else {
//			System.out.println("Name, Major");
//			for (List row : values) {
//				// Print columns A and E, which correspond to indices 0 and 4.
//				System.out.printf("%s, %s\n", row.get(0), row.get(4));
//			}
//		}
