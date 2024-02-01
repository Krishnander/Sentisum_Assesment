package com.sentisum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {
	
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
        .connectedTo("localhost:9200")
        .build();
    }

    @Override
    protected boolean writeTypeHints() {
        return false;
    }

}