package com.sentisum.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;

@Component
public class ElasticSearchUtil {

	public static Supplier<Query> supplierQueryForBoolQuery(String city, Integer salary) {
		Supplier<Query> supplier = () -> Query.of(q -> q.bool(boolQuery(city, salary)));
		return supplier;
	}

	public static BoolQuery boolQuery(String city, Integer salary) {
		var boolQuery = new BoolQuery.Builder();
		return boolQuery.filter(matchQuery(city)).must(rangeQuery(salary)).build();
	}

	public static List<Query> matchQuery(String city) {
		final List<Query> matches = new ArrayList<>();
		var matchQuery = new MatchQuery.Builder();
		matches.add(Query.of(q -> q.match(matchQuery.field("location").query(city).build())));
		return matches;
	}

	public static List<Query> rangeQuery(Integer salary) {
		final List<Query> matches = new ArrayList<>();
		var rangeQuery = new RangeQuery.Builder();
		matches.add(Query.of(q -> q.range(rangeQuery.field("totalCTC").gte(JsonData.of(String.valueOf(salary))).build())));
		return matches;
	}

}
