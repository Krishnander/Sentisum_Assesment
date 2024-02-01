package com.sentisum.service.impl;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sentisum.dto.CompensationDataDTO;
import com.sentisum.dto.Reader;
import com.sentisum.repo.DataRepository;
import com.sentisum.service.DataExportService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class DataExportServiceImpl implements DataExportService {

	Logger logger = LoggerFactory.getLogger(DataExportServiceImpl.class);

	@Autowired
	DataRepository dataRepository;

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@Override
	public void insertDataIntoElasticSerach() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL resource = classLoader.getResource("csv");
			List<File> files = Files.walk(Paths.get(resource.toURI())).filter(Files::isRegularFile).map(x -> x.toFile())
					.collect(Collectors.toList());
			for (var file : files) {
				CSVReader csvReader = new CSVReader(Files.newBufferedReader(file.toPath()));
				List<Reader> resultData = new CsvToBeanBuilder<Reader>(csvReader).withType(Reader.class).build()
						.parse();
				var compensationList = new ArrayList<CompensationDataDTO>();
				resultData.forEach(r -> {
					CompensationDataDTO compensationDataDTO = new CompensationDataDTO();
					r.getData().entries().forEach(e -> {
						if (StringUtils.isNotBlank(e.getKey()) && StringUtils.isNotBlank(e.getValue())) {
							if (StringUtils.containsAnyIgnoreCase(e.getKey(), "city")) {
								compensationDataDTO.setLocation(e.getValue());
							}
							if (StringUtils.containsAnyIgnoreCase(e.getKey(), "timestamp")) {
								compensationDataDTO
										.setTimestamp(LocalDateTime.parse(dateConverter(e, compensationDataDTO),
												DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
							}
							if (StringUtils.equalsAnyIgnoreCase(e.getKey(), "Job Title", "Job Title In Company")) {
								compensationDataDTO.setJobTitle(e.getValue());
							}
							if ((StringUtils.containsAnyIgnoreCase(e.getKey(), "Employer")
									|| StringUtils.containsAnyIgnoreCase(e.getKey(), "Company Name"))) {
								compensationDataDTO.setCompany(e.getValue());
							}
							mapCompensationFields(e, compensationDataDTO);
						}
					});
					if (compensationDataDTO.getTotalCTC() == 0) {
						var totalCTC = compensationDataDTO.getBasePay() + compensationDataDTO.getTotalBonus()
								+ compensationDataDTO.getSigningBonus() + compensationDataDTO.getAnnualBonus()
								+ compensationDataDTO.getStockValue();
						compensationDataDTO.setTotalCTC(totalCTC);
					}
					compensationList.add(compensationDataDTO);
				});
				dataRepository.saveAll(compensationList);
			}
		} catch (Exception e) {
			logger.error("Exception occured while insering csv data into elasticsearch due to : {}", e.getMessage());
		}
	}

	private void mapCompensationFields(Entry<String, String> e, CompensationDataDTO compensationDataDTO) {
		if (StringUtils.containsAnyIgnoreCase(e.getKey(), "annual salary")) {
			compensationDataDTO.setTotalCTC(extractNumber(e.getValue()));
		} else {
			if (StringUtils.containsAnyIgnoreCase(e.getKey(), "Base")) {
				compensationDataDTO.setBasePay(extractNumber(e.getValue()));
			}
			if (StringUtils.containsAnyIgnoreCase(e.getKey(), "Total Bonus")) {
				compensationDataDTO.setTotalBonus(extractNumber(e.getValue()));
			}
			if (StringUtils.containsAnyIgnoreCase(e.getKey(), "Signing Bonus")) {
				compensationDataDTO.setSigningBonus(extractNumber(e.getValue()));
			}
			if (StringUtils.containsAnyIgnoreCase(e.getKey(), "Annual Bonus")) {
				compensationDataDTO.setAnnualBonus(extractNumber(e.getValue()));
			}
			if (StringUtils.containsAnyIgnoreCase(e.getKey(), "Stock")) {
				compensationDataDTO.setStockValue(extractNumber(e.getValue()));
			}
		}
	}

	// This function is used to convert the input timestamp present in different
	// formats to a standard output format
	private String dateConverter(Entry<String, String> e, CompensationDataDTO compensationDataDTO) {
		String outputFormat = "yyyy-MM-dd'T'HH:mm:ss";
		String inputDate = e.getValue();
		String convertedDate = "";
		final DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
		try {
			dtfb.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy H:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy HH:m:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy HH:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy H:mm:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy HH:m:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy H:m:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:m:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy H:m:ss"))
					.appendOptional(DateTimeFormatter.ofPattern("yyyy-mm-dd"))
					.appendOptional(DateTimeFormatter.ofPattern("yyyy-dd-mm"))
					.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm:ss"));
			DateTimeFormatter inputFormatter = dtfb.toFormatter();
			LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
			convertedDate = dateTime.format(outputFormatter);
		} catch (Exception ex) {
			logger.error("Exception occured while parsing date due to : {}", ex.getMessage());
		}
		return convertedDate;
	}

	// Salary data such as base pay and several bonus components have a lot of
	// alphanumeric values in many different formats. This function is used to clean
	// them at a basic level. The data that couldn't be sanitized it returned as 0
	private int extractNumber(String input) {
		var cleaned = input.replace('$', ' ').replace(',', ' ').replace('.', ' ').replaceAll(" ", "").trim().strip();
		try {
			if (!input.matches("^[a-zA-Z]+$")) {
				return Integer.parseInt(cleaned);
			}
		} catch (Exception e) {
			logger.error("Exception occured while parsing salary data into integer due to : " + e.getMessage());
		}
		return 0;
	}

}
