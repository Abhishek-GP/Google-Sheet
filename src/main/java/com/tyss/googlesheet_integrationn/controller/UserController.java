package com.tyss.googlesheet_integrationn.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tyss.googlesheet_integrationn.dto.GoogleSheetDto;
import com.tyss.googlesheet_integrationn.dto.GoogleSheetresponseDTO;
import com.tyss.googlesheet_integrationn.service.UserService;

@RestController
public class UserController {
	@Autowired
	UserService userservice;

	@GetMapping("/getdata")
	public Map<Object, Object> readDataFromGooglesheet() throws GeneralSecurityException, IOException {
		return userservice.readDataFromGooglesheet();
	}

	@PostMapping("/createsheet")
	public GoogleSheetresponseDTO createGoogleSheet(@RequestBody GoogleSheetDto dto)
			throws GeneralSecurityException, IOException {
		return userservice.creategoogleSheet(dto);
	}

}
