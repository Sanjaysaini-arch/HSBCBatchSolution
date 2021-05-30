package com.hsbc.config;

import java.io.File;
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
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import org.springframework.web.client.RestTemplate;

import com.hsbc.dto.TransactionRecord;
import com.hsbc.exceptionHandlers.FileReaderVarificationCustomSkiper;
import com.hsbc.listener.CustomItemProcessorListener;
import com.hsbc.listener.CustomItemReaderListener;
import com.hsbc.listener.CustomItemStepListener;
import com.hsbc.listener.CustomItemWriterListener;
import com.hsbc.notification.JobCompletionNotificationListener;
import com.hsbc.processor.PersonItemProcessor;
import com.hsbc.writers.CustomLineAggregator;
import com.hsbc.writers.DbWriter;
import com.hsbc.writers.FileWriter;
import com.hsbc.writers.RestApiDownStreamWriter;
import com.hsbc.writers.TransactionRecordClassifier;

import io.micrometer.core.instrument.config.validate.ValidationException;



// tag::setup[]

@Configuration
@EnableBatchProcessing
public class ParellelFileProcessBatchConfiguration {
	 private static final Logger logger = LoggerFactory.getLogger("BatchConfiguration");
	   
	
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
	RestApiDownStreamWriter restApiDownStreamWriter;
	
	@Autowired
	private FlatFileItemReader<TransactionRecord> personItemReader;
	@Autowired
	private ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter;
	// end::setup[]

	@Bean org.springframework.validation.Validator validator() {
		return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
	}
	@Bean Validator<TransactionRecord> springValidator()
	{
		SpringValidator<TransactionRecord> sv= new SpringValidator<TransactionRecord>();
		sv.setValidator(validator());
		return sv;
	}

	@Bean
	public BeanValidatingItemProcessor<TransactionRecord> processor() {
		BeanValidatingItemProcessor<TransactionRecord> validator =new BeanValidatingItemProcessor<TransactionRecord>();
		//validator.setFilter(true);
		
		return  validator;
		
	}
	/*@Bean
    public FlatFileItemWriter<TransactionRecord> jsonItemWriter() throws Exception {
 
        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> Output Path = " + customerOutputPath);
        FlatFileItemWriter<TransactionRecord> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new CustomLineAggregator());
        writer.setResource(new FileSystemResource(customerOutputPath));
        writer.afterPropertiesSet();
        return writer;
    }*/
	
	/*private Resource outputResource = new FileSystemResource("output/outputData.csv");
	 
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
*/
	@Bean
	public ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter() throws Exception {
		ClassifierCompositeItemWriter<TransactionRecord> compositeItemWriter = new ClassifierCompositeItemWriter<>();
		compositeItemWriter.setClassifier(new TransactionRecordClassifier(dbWriter,restApiDownStreamWriter));
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

	
	@Bean
	public SkipPolicy fileVerificationSkipper() {
	    return new FileReaderVarificationCustomSkiper();
	}
	@Bean("partitioner")
	@StepScope
	public Partitioner partitioner() {
		logger.info("In Partitioner");

		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = resolver.getResources("/*.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		partitioner.setResources(resources);
		partitioner.partition(10);
		return partitioner;
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(masterStep())
				.end()
				.build();
	}
	 @Bean
	  public CustomItemStepListener customStepListener() {
	    return new CustomItemStepListener();
	  }
	 
	  @Bean
	  public CustomItemWriterListener itemWriterListener() {
	    return new CustomItemWriterListener();
	  }
	 
	  @Bean
	  public CustomItemReaderListener itemReaderListener() {
	    return new CustomItemReaderListener();
	  }
	  @Bean
	  public CustomItemProcessorListener itemProcessorListener() {
	    return new CustomItemProcessorListener();
	  }
	 
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<TransactionRecord, TransactionRecord>chunk(10)
				.processor(processor())
				.writer(classifierRecordCompositeItemWriter)
				.reader(personItemReader).faultTolerant()
				.skipPolicy(fileVerificationSkipper())
				.listener(itemProcessorListener()).listener(itemReaderListener())
		        .listener(itemWriterListener()).listener(customStepListener())
				.build();
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Bean
	@Qualifier("masterStep")
	public Step masterStep() {
		return stepBuilderFactory.get("masterStep")
				.partitioner("step1", partitioner())
				.step(step1())
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	@StepScope
	@Qualifier("personItemReader")
	@DependsOn("partitioner")
	public FlatFileItemReader<TransactionRecord> personItemReader(@Value("#{stepExecutionContext['fileName']}") String filename)
			throws MalformedURLException {
		logger.info("In Reader");
		return new FlatFileItemReaderBuilder<TransactionRecord>().name("personItemReader")
				.linesToSkip(1)
				.delimited()
				.names(new String[]{"id","jobTitle","emailAddress","firstName","lastName","salary","amoutAddToSalary","phoneNumber","downStreamType"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<TransactionRecord>() {
					{
						setTargetType(TransactionRecord.class);
					}
				})
				.resource(new UrlResource(filename))
				.build();
	}
	
	// end::jobstep[]
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
