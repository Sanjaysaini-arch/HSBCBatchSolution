/*package com.hsbc.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hsbc.dto.TransactionRecord;
import com.hsbc.exceptionHandlers.FileReaderVarificationCustomSkiper;
import com.hsbc.notification.JobCompletionNotificationListener;
import com.hsbc.processor.PersonItemProcessor;
import com.hsbc.writers.CustomLineAggregator;
import com.hsbc.writers.DbWriter;
import com.hsbc.writers.FileWriter;
import com.hsbc.writers.TransactionRecordClassifier;


@Configuration
@EnableBatchProcessing
public class SequencialFileProcessBatchConfig {
	private static final Logger logger = LoggerFactory.getLogger("SequencialFileProcessBatchConfig");
	   
		
	@Value("${com.hsbc.file.path.client}")
	private Resource[] inputResources;
	@Value("${com.hsbc.file.path.clients}")
	private Resource[] inputResources1;
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	DbWriter dbWriter;
	@Autowired
	FileWriter fileWriter;
	
	@Autowired
	private FlatFileItemReader<TransactionRecord> personItemReader;
	@Autowired
	private ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter;

	
	// tag::readerwriterprocessor[]
		@Bean
		public FlatFileItemReader<TransactionRecord> reader() {
			return new FlatFileItemReaderBuilder<TransactionRecord>()
				.name("personItemReader")
				//.resource(new ClassPathResource("client1-data.csv"))
				.linesToSkip(1)
				.delimited()
				.names(new String[]{"id","jobTitle","emailAddress","firstName","lastName","salary","amoutAddToSalary","phoneNumber","downStreamType"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<TransactionRecord>() {{
					setTargetType(TransactionRecord.class);
				}})
				.build();
		}
		
		@Bean
		public PersonItemProcessor processor() {
			return new PersonItemProcessor();
		}
		@Bean
	    public FlatFileItemWriter<TransactionRecord> jsonItemWriter() throws Exception {
	 
	        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
	        System.out.println(">> Output Path = " + customerOutputPath);
	        FlatFileItemWriter<TransactionRecord> writer = new FlatFileItemWriter<>();
	        writer.setLineAggregator(new CustomLineAggregator());
	        writer.setResource(new FileSystemResource(customerOutputPath));
	        writer.afterPropertiesSet();
	        return writer;
	    }
		
		private Resource outputResource = new FileSystemResource("output/outputData.csv");
		 
	    @Bean
	    public FlatFileItemWriter<TransactionRecord> writer() 
	    {
	        //Create writer instance
	        FlatFileItemWriter<TransactionRecord> writer = new FlatFileItemWriter<>();
	         
	        //Set output file location
	        writer.setResource(outputResource);
	         
	        //All job repetitions should "append" to same output file
	        writer.setAppendAllowed(true);
	         
	        //Name field values sequence based on object properties 
	        writer.setLineAggregator(new DelimitedLineAggregator<TransactionRecord>() {
	            {
	                setDelimiter(",");
	                setFieldExtractor(new BeanWrapperFieldExtractor<TransactionRecord>() {
	                    {
	                        setNames(new String[] { "id", "firstName", "lastName" });
	                    }
	                });
	            }
	        });
	        return writer;
	    }

		@Bean
		public ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter() throws Exception {
			ClassifierCompositeItemWriter<TransactionRecord> compositeItemWriter = new ClassifierCompositeItemWriter<>();
			compositeItemWriter.setClassifier(new TransactionRecordClassifier(dbWriter,fileWriter));
			return compositeItemWriter;
		}
		
		@Bean
		public CompositeItemWriter<TransactionRecord> compositItemWriter(DataSource dataSource) {
		CompositeItemWriter<TransactionRecord> compositeItemWriter = new CompositeItemWriter<>();
	    List<org.springframework.batch.item.ItemWriter<? super TransactionRecord>> delegates = new ArrayList<>();
	    delegates.add(dbWriter);
	    delegates.add(fileWriter);
	    compositeItemWriter.setDelegates(delegates);
	    return compositeItemWriter;
		}
		// end::readerwriterprocessor[]

		// tag::jobstep[]
		
		@Bean
		public Job job(JobCompletionNotificationListener listener, Step step1,Step step2) {
			return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(step1).next(step2)
				.build();
		}
		
		
		@Bean
		public MultiResourceItemReader<TransactionRecord> multiResourceItemReader() 
		{
		    MultiResourceItemReader<TransactionRecord> resourceItemReader = new MultiResourceItemReader<TransactionRecord>();
		    //resourceItemReader.setResources(inputResources);
		    resourceItemReader.setDelegate(reader());
		    return resourceItemReader;
		}
		
		
		
		@Bean
		public MultiResourceItemReader<TransactionRecord> multiResourceItemReader1() 
		{
		    MultiResourceItemReader<TransactionRecord> resourceItemReader = new MultiResourceItemReader<TransactionRecord>();
		    resourceItemReader.setResources(inputResources1);
		    
		    resourceItemReader.setDelegate(reader());
		    return resourceItemReader;
		}
		@Bean org.springframework.validation.Validator beanValidator() {
			return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
		}
		@Bean SpringValidator<TransactionRecord> validator()
		{
			SpringValidator sv= new SpringValidator<TransactionRecord>();
			sv.setValidator(beanValidator());
			return sv;
		}
		@Bean
		public SkipPolicy fileVerificationSkipper() {
		    return new FileReaderVarificationCustomSkiper();
		}
		
		
		@Bean
		public Step step2(ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter) {
			return stepBuilderFactory.get("step2")
				.<TransactionRecord, TransactionRecord> chunk(1000)
				.reader(multiResourceItemReader1())
				.processor(processor())
				.writer(classifierRecordCompositeItemWriter).faultTolerant().skipLimit(5).skip(FlatFileParseException.class)
				.throttleLimit(20)
				
				.startLimit(1)
				.build();
		}
		@Bean
		public Step step1(ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter) {
			return stepBuilderFactory.get("step1")
				.<TransactionRecord, TransactionRecord> chunk(1000)
				.reader(multiResourceItemReader()).faultTolerant().skipPolicy(fileVerificationSkipper())
				.processor(processor())
				.writer(classifierRecordCompositeItemWriter)
				.throttleLimit(20)
				.build();
		}

}
*/