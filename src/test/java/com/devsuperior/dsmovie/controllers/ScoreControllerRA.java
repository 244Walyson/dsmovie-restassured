package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.*;

public class ScoreControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword, adminToken, clientToken, invalidToken;
	private Long existingMovieId, nonExistingMovieId;
	private Map<String, Object> postScoreInstance;


	@BeforeEach
	void setUp() throws JSONException {
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword  = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", 1);
		postScoreInstance.put("score", 0.0);
	}
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

		postScoreInstance.put("movieId", 100);
		JSONObject newMovie = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovie)
				.when()
				.put("/scores")
				.then()
				.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

		postScoreInstance.put("movieId", null);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newScore)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors[0].fieldName", equalTo("movieId"))
				.body("errors[0].message", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {

		postScoreInstance.put("score", -1);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newScore)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors[0].fieldName", equalTo("score"))
				.body("errors[0].message", equalTo("Valor mínimo 0"));
	}
}