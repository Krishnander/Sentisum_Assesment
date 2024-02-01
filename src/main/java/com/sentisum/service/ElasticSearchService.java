package com.sentisum.service;

import org.springframework.stereotype.Component;

import com.sentisum.dto.CompensationDataDTO;

import co.elastic.clients.elasticsearch.core.SearchResponse;

@Component
public interface ElasticSearchService {

	SearchResponse<CompensationDataDTO> fetchData(String city, Integer salary)
			throws Exception;

	CompensationDataDTO getSparseFields(String designation);

}
