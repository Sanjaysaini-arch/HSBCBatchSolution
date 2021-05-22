package com.hsbc.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.hsbc.dto.Person;
import com.hsbc.notification.JobCompletionNotificationListener;
import com.hsbc.processor.PersonItemProcessor;
import com.hsbc.writers.DbWriter;
import com.hsbc.writers.FileWriter;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Value("client1set1*.csv")
	private Resource[] inputResources;
	@Value("client1set2*.csv")
	private Resource[] inputResources1;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	DbWriter dbWriter;
	@Autowired
	FileWriter fileWriter;
	// end::setup[]

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			//.resource(new ClassPathResource("client1-data.csv"))
			.linesToSkip(1)
			.delimited()
			.names(new String[]{"id","jobTitle","emailAddress","firstName","lastName","salary","amoutAddToSalary","phoneNumber"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}
	
	/*@Bean
	public DbWriter dbWriter() {
		return new DbWriter();
	}
	@Bean
	public FileWriter fileWriter() {
		return new FileWriter();
	}*/
	
	

	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (jobTitle,emailAddress,firstName,lastName,salary,amoutAddToSalary,phoneNumber) VALUES (:jobTitle,:emailAddress,:firstName,:lastName,:salary,:amoutAddToSalary,:phoneNumber)")
			.dataSource(dataSource)
			.build();
	}
	@Bean
	public CompositeItemWriter<Person> compositItemWriter(DataSource dataSource) {
	CompositeItemWriter<Person> compositeItemWriter = new CompositeItemWriter<>();
    List<org.springframework.batch.item.ItemWriter<? super Person>> delegates = new ArrayList<>();
    delegates.add(dbWriter);
    delegates.add(fileWriter);
    delegates.add(writer(dataSource));
    compositeItemWriter.setDelegates(delegates);
    return compositeItemWriter;
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}
	@Bean
	public MultiResourceItemReader<Person> multiResourceItemReader() 
	{
	    MultiResourceItemReader<Person> resourceItemReader = new MultiResourceItemReader<Person>();
	    resourceItemReader.setResources(inputResources);
	    resourceItemReader.setDelegate(reader());
	    return resourceItemReader;
	}
	
	@Bean
	public MultiResourceItemReader<Person> multiResourceItemReader1() 
	{
	    MultiResourceItemReader<Person> resourceItemReader = new MultiResourceItemReader<Person>();
	    resourceItemReader.setResources(inputResources1);
	    resourceItemReader.setDelegate(reader());
	    return resourceItemReader;
	}

	@Bean
	public Step step1(CompositeItemWriter<Person> compositItemWriter) {
		return stepBuilderFactory.get("step1")
			.<Person, Person> chunk(1000)
			.reader(multiResourceItemReader())
			.processor(processor())
			.writer(compositItemWriter)
			.throttleLimit(20)
			.build();
	}
	@Bean
	public Step step2(CompositeItemWriter<Person> compositItemWriter) {
		return stepBuilderFactory.get("step2")
			.<Person, Person> chunk(1000)
			.reader(multiResourceItemReader1())
			.processor(processor())
			.writer(compositItemWriter)
			.throttleLimit(20)
			.build();
	}
	
	
	// end::jobstep[]
}
