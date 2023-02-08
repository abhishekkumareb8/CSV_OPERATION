package com.ty.project.csvoperation.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ty.project.csvoperation.dto.Employee;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JobBuilderFactory factory;

	@Autowired
	private StepBuilderFactory builderFactory;

	@Bean
	public FlatFileItemReader<Employee> reader() {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
		reader.setResource(new ClassPathResource("Example.csv"));
		reader.setLineMapper(null);
		reader.setLinesToSkip(1);
		return reader;
	}

	private LineMapper<Employee> getLineMapper() {
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[] { "id", "name", "desc", "sal" });
		lineTokenizer.setIncludedFields(new int[] { 0, 1, 2, 3 });

		BeanWrapperFieldSetMapper<Employee> fieldSetter = new BeanWrapperFieldSetMapper<Employee>();
		fieldSetter.setTargetType(Employee.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetter);
		
		return lineMapper;
	}
	
	@Bean
	public EmployeeProcess process() {
		return new EmployeeProcess();
	}
	
	@Bean
	public JdbcBatchItemWriter<Employee> writer(){
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<Employee>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		writer.setSql("insert into employee(id,name,desc,sal) values (:id,:name,:desc,:sal");
		writer.setDataSource(this.dataSource);
		return writer;
	}
	
	@Bean
	public Job job() {
		
		return (Job) this.factory.get("Employee-Import-Job") 
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();			
	}

	@Bean
	public Step step1() {
		return this.builderFactory.get("step1")
		.<Employee, Employee> chunk(10)
		.reader(reader())
		.processor(process())
		.writer(writer())
		.build();
	}
	
}
