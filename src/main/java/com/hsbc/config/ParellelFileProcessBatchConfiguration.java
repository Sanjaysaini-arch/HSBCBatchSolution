package com.hsbc.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.hsbc.dto.TransactionRecord;
import com.hsbc.exceptionHandlers.FileReaderVarificationCustomSkiper;
import com.hsbc.listener.CustomItemProcessorListener;
import com.hsbc.listener.CustomItemReaderListener;
import com.hsbc.listener.CustomItemStepListener;
import com.hsbc.listener.CustomItemWriterListener;
import com.hsbc.notification.JobCompletionNotificationListener;
import com.hsbc.writers.KafkaWriterDownStreamTopic1;
import com.hsbc.writers.KafkaWriterDownStreamTopic2;
import com.hsbc.writers.TransactionRecordClassifier;



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
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	
	KafkaWriterDownStreamTopic1 kafkaWriterDownStreamTopic1;
	KafkaWriterDownStreamTopic2 kafkaWriterDownStreamTopic2;

	
	@Bean
    public ProducerFactory<Long, TransactionRecord> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }


    @Bean
    public KafkaTemplate<Long, TransactionRecord> kafkaTemplate() {
        return new KafkaTemplate<Long, TransactionRecord>(producerFactory());
    
    }
	
	
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
	
	
	@Bean
	public ClassifierCompositeItemWriter<TransactionRecord> classifierRecordCompositeItemWriter() throws Exception {
		ClassifierCompositeItemWriter<TransactionRecord> compositeItemWriter = new ClassifierCompositeItemWriter<>();
		compositeItemWriter.setClassifier(new TransactionRecordClassifier(kafkaWriterDownStreamTopic2 ,kafkaWriterDownStreamTopic1));
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
