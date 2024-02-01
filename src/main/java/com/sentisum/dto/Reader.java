package com.sentisum.dto;

import org.apache.commons.collections4.MultiValuedMap;

import com.opencsv.bean.CsvBindAndJoinByName;

public class Reader {

	@CsvBindAndJoinByName(column = ".*", elementType = String.class)
	private MultiValuedMap<String, String> data;

	public MultiValuedMap<String, String> getData() {
		return data;
	}

	public void setData(MultiValuedMap<String, String> data) {
		this.data = data;
	}

	public Reader(MultiValuedMap<String, String> data) {
		super();
		this.data = data;
	}

	public Reader() {
		super();
	}

	@Override
	public String toString() {
		return "Reader [data=" + data + "]";
	}
	
	
}
