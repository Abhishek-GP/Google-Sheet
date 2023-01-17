package com.tyss.googlesheet_integrationn.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tyss.googlesheet_integrationn.dto.GoogleSheetDto;
import com.tyss.googlesheet_integrationn.dto.GoogleSheetresponseDTO;
import com.tyss.googlesheet_integrationn.util.GoogleApiUtil;

@Service
public class UserService {
	@Autowired(required = true)
	GoogleApiUtil apiUtil;

	public Map<Object, Object> readDataFromGooglesheet() throws GeneralSecurityException, IOException {

		return apiUtil.getdatafromSheet();
	}

	public GoogleSheetresponseDTO creategoogleSheet(GoogleSheetDto request) throws GeneralSecurityException, IOException {
		return apiUtil.createGoogleSheet(request);
	}

}
