package com.sentisum.repo;

import org.springframework.data.elasticsearch.annotations.SourceFilters;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.sentisum.dto.CompensationDataDTO;

@Repository
public interface DataRepository extends ElasticsearchRepository<CompensationDataDTO, String> {

	@SourceFilters(includes = { "location", "totalCTC", "jobTitle" })
	CompensationDataDTO findFirstByJobTitle(String designation);
}
