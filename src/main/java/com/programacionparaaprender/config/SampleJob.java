package com.programacionparaaprender.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.programacionparaaprender.app.SpringBatchApplication;
import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentXml;
import com.programacionparaaprender.processor.FirstItemProcessor;
import com.programacionparaaprender.reader.FirstItemReader;
import com.programacionparaaprender.service.FirstJobListener;
import com.programacionparaaprender.service.FirstStepListener;
import com.programacionparaaprender.service.SecondTasklet;
import com.programacionparaaprender.writer.FirstItemWriter;
import com.programacionparaaprender.writer.FirstItemWriterCsv;
import com.programacionparaaprender.writer.FirstItemWriterJson;
import com.programacionparaaprender.writer.FirstItemWriterXml;

import org.springframework.context.annotation.Bean;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SecondTasklet secondTasklet;
	
	@Autowired
	FirstJobListener firstJobListener;
	
	@Autowired
	FirstStepListener firstStepListener;
	
	@Autowired
	FirstItemReader firstItemReader;
	
	@Autowired
	FirstItemProcessor firstItemProcessor; 
	
	@Autowired
	FirstItemWriter firstItemWriter; 
	
	@Autowired
	FirstItemWriterCsv firstItemWriterCsv; 
	
	@Autowired
	FirstItemWriterJson firstItemWriterJson; 
	
	@Autowired
	FirstItemWriterXml firstItemWriterXml; 
	
	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")
		.incrementer(new RunIdIncrementer())
		.start(firstChunkStepNew2())
		//.next(secondStep())
		.build();
	}
	
	private Step firstChunkStepNew2() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentXml, StudentXml>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(staxEventItemReader(null))
				//.processor(firstItemProcessor)
				.writer(firstItemWriterXml)
				.build();
	}
	
	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(
			@Value("#{jobParameters['inputFileXml']}") FileSystemResource fileSystemResource) {
		StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<StudentXml>();
		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});
		return staxEventItemReader;
	}
	
	
	private Step firstChunkStepNew() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentJson, StudentJson>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jsonItemReaderNew(null))
				//.processor(firstItemProcessor)
				.writer(firstItemWriterJson)
				.build();
	}
	
	
	
	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReaderNew(
			@Value("#{jobParameters['inputFileJson']}") FileSystemResource fileSystemResource) {
		
		FileSystemResource fileSystemResource1 = new FileSystemResource(
			new File("C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\inputFiles\\students.json"));
		
		JsonItemReader<StudentJson> jsonItemReader = 
				new JsonItemReader<StudentJson>();
		
		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(
				new JacksonJsonObjectReader<>(StudentJson.class));
		
		jsonItemReader.setMaxItemCount(8);
		jsonItemReader.setCurrentItemCount(2);
		
		return jsonItemReader;
	}
	
	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentJson, StudentJson>chunk(3)
				.reader(jsonItemReader(null))
				//.processor(firstItemProcessor) //se puede usar sin processor
				.writer(firstItemWriterJson)
				.build();
	}
	
	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(@Value("#{jobParameters['inputFileJson']}") FileSystemResource fileSystemResource){
		JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<StudentJson>();
		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(
				new JacksonJsonObjectReader<>(StudentJson.class)
				);
		return jsonItemReader;
	}
	
	private Step firstChunkStepAntiguo2() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentCsv, StudentCsv>chunk(3)
				.reader(flatFileItemReader(null))
				//.processor(firstItemProcessor) //se puede usar sin processor
				.writer(firstItemWriterCsv)
				.build();
	}
	
	//@StepScope
	//@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemReader<StudentCsv> flatFileItemReader = 
				new FlatFileItemReader<StudentCsv>();
		
		flatFileItemReader.setResource(fileSystemResource);
		
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");
					}
				});
				
				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				});
				
			}
		});
		
		/*
		DefaultLineMapper<StudentCsv> defaultLineMapper = 
				new DefaultLineMapper<StudentCsv>();
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		
		BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper = 
				new BeanWrapperFieldSetMapper<StudentCsv>();
		fieldSetMapper.setTargetType(StudentCsv.class);
		
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		flatFileItemReader.setLineMapper(defaultLineMapper);
		*/
		
		flatFileItemReader.setLinesToSkip(1);
		
		return flatFileItemReader;
	}
	
	//@StepScope
	//@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReaderAntiguo(
			@Value("#{jobParameters['inputFile']}") 
			FileSystemResource fileSystemResource){
		FlatFileItemReader<StudentCsv> flatFileItemReader =
				new FlatFileItemReader<StudentCsv>();
		
		flatFileItemReader.setResource(fileSystemResource);
		
		//flatFileItemReader.setResource(new FileSystemResource(
		//		new File("C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\inputFiles\\students.csv")));
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");	
						//en caso de no usar comillas si usa |
						//setDelimiter("|");
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				});
			}
		});
		flatFileItemReader.setLinesToSkip(1);
		return flatFileItemReader;
	}
	
	private Step firstChunkStepAntiguo() {
		return stepBuilderFactory.get("First Chunk Step")
				.<Integer, Long>chunk(3)
				.reader(firstItemReader)
				.processor(firstItemProcessor) //se puede usar sin processor
				.writer(firstItemWriter)
				.build();
	}
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
		.incrementer(new RunIdIncrementer())
		.start(firstStep())
		.next(secondStep())
		.listener(firstJobListener)
		.build();
	}
	
	private Step firstStep() {
		return stepBuilderFactory.get("First step")
		.tasklet(firstTasklet())
		.listener(firstStepListener)
		.build();
	}
	
	private Step secondStep() {
		return stepBuilderFactory.get("Second step")
		//.tasklet(secondTaskletMetodo(1))
		.tasklet(secondTasklet)
		.build();
	}
	
	private Tasklet firstTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("This is first tasklet");
				System.out.println(chunkContext.getStepContext().getJobExecutionContext());
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	private Tasklet secondTaskletMetodo(int id) {
		return new SecondTasklet(id);
	}

	
	
}
