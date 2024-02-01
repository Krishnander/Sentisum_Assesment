package com.sentisum.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Document(indexName = "compensation_data", writeTypeHint = WriteTypeHint.FALSE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompensationDataDTO {

	@Id
	private String id;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime timestamp;

	@Field(type = FieldType.Text, fielddata = true)
	private String jobTitle;

	@Field(type = FieldType.Integer)
	private int totalCTC;

	@Field(type = FieldType.Keyword)
	private String location;

	@Field(type = FieldType.Text, fielddata = true)
	private String company;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int basePay;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int totalBonus;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int signingBonus;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int annualBonus;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int stockValue;

	public CompensationDataDTO(String id, LocalDateTime timestamp, String jobTitle, int totalCTC, String location,
			String industry, int basePay, int totalbonus, int signingbonus, int annualbonus,
			int stockValue) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.jobTitle = jobTitle;
		this.totalCTC = totalCTC;
		this.location = location;
		this.company = industry;
		this.basePay = basePay;
		this.totalBonus = totalbonus;
		this.signingBonus = signingbonus;
		this.annualBonus = annualbonus;
		this.stockValue = stockValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public int getTotalCTC() {
		return totalCTC;
	}

	public void setTotalCTC(int totalCTC) {
		this.totalCTC = totalCTC;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String industry) {
		this.company = industry;
	}

	public int getBasePay() {
		return basePay;
	}

	public void setBasePay(int basePay) {
		this.basePay = basePay;
	}

	public int getTotalBonus() {
		return totalBonus;
	}

	public void setTotalBonus(int totalbonus) {
		this.totalBonus = totalbonus;
	}

	public int getSigningBonus() {
		return signingBonus;
	}

	public void setSigningBonus(int signingbonus) {
		this.signingBonus = signingbonus;
	}

	public int getAnnualBonus() {
		return annualBonus;
	}

	public void setAnnualBonus(int annualbonus) {
		this.annualBonus = annualbonus;
	}

	public int getStockValue() {
		return stockValue;
	}

	public void setStockValue(int stockValue) {
		this.stockValue = stockValue;
	}

	public CompensationDataDTO() {
		super();
	}

}
