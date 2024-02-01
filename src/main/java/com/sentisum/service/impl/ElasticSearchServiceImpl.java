package com.sentisum.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sentisum.dto.CompensationDataDTO;
import com.sentisum.repo.DataRepository;
import com.sentisum.service.ElasticSearchService;
import com.sentisum.util.ElasticSearchUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

	Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@Autowired
	DataRepository dataRepository;

	@Override
	public SearchResponse<CompensationDataDTO> fetchData(String city, Integer salary) throws Exception {
		Supplier<Query> supplier = ElasticSearchUtil.supplierQueryForBoolQuery(city, salary);
		List<SortOptions> sortList = new ArrayList<SortOptions>();
		SortOptions sort = new SortOptions.Builder().field(f -> f.field("timestamp").order(SortOrder.Asc)).build();
		sortList.add(sort);
		try {
			return elasticsearchClient.search(s -> s.index("compensation_data").query(supplier.get()).sort(sortList),
					CompensationDataDTO.class);
		} catch (Exception e) {
			throw new Exception("Exception occured while fetching data due to : {}", e.getCause());
		}
	}

	@Override
	public CompensationDataDTO getSparseFields(String designation) {
		return dataRepository.findFirstByJobTitle(designation);
	}

}
