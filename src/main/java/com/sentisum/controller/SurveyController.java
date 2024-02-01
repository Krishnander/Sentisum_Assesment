package com.sentisum.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sentisum.dto.CompensationDataDTO;
import com.sentisum.service.DataExportService;
import com.sentisum.service.ElasticSearchService;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "Survey")
@EnableElasticsearchRepositories
@Tag(name = "Survey Playground", description = "APIs to perform operations on survey data")
public class SurveyController {

	Logger logger = LoggerFactory.getLogger(SurveyController.class);

	@Autowired
	DataExportService dataExportService;

	@Autowired
	ElasticSearchService elasticSearchService;

	@Operation(summary = "API to insert CSV Data", description = "CSV Files are present in resources folder. Running this API will insert them into elastic search")
	@GetMapping("/insert")
	public void insertDataIntoElasticSerach() {
		logger.info("CSV Data upload to elasticsearch has started");
		dataExportService.insertDataIntoElasticSerach();
		logger.info("CSV Data upload to elasticsearch has completed");
	}

	@Operation(summary = "API to get list of filtered and sorted compensation records", description = "This API will return compensation data by filtering it with city and salary greater than mentioned amount. The response is sorted in descending order by timestamp")
	@GetMapping("/compensationList")
	public List<CompensationDataDTO> fetchCompensationsByCityAndSalary(
			@RequestParam(name = "city", required = true) String city,
			@RequestParam(name = "min salary", required = true) Integer salary) throws Exception {
		SearchResponse<CompensationDataDTO> searchResponse = elasticSearchService.fetchData(city, salary);
		return searchResponse.hits().hits().stream().map(response -> response.source()).collect(Collectors.toList());
	}

	@Operation(summary = "API to get single record with sparse fieldset", description = "This API will return compensation data by filtering it with designation. The response is sparse fieldset of compensation data")
	@GetMapping("/compensation")
	public CompensationDataDTO getSpecificFieldsFromRecord(
			@RequestParam(name = "Job Title", required = true) String designation) {
		return elasticSearchService.getSparseFields(designation);
	}

}
