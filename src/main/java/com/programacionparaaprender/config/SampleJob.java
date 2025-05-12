package com.programacionparaaprender.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.programacionparaaprender.app.SpringBatchApplication;
import com.programacionparaaprender.listener.SkipListener;
import com.programacionparaaprender.listener.SkipListenerImpl;
import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJdbc;
import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentResponse;
import com.programacionparaaprender.model.StudentXml;
import com.programacionparaaprender.processor.FirstItemProcessor;
import com.programacionparaaprender.processor.FirstItemProcessorChunk;
import com.programacionparaaprender.processor.FirstItemProcessorChunkError;
import com.programacionparaaprender.processor.FirstItemProcessorJson;
import com.programacionparaaprender.processor.FirstItemProcessorXml;
import com.programacionparaaprender.reader.FirstItemReader;
import com.programacionparaaprender.service.FirstJobListener;
import com.programacionparaaprender.service.FirstStepListener;
import com.programacionparaaprender.service.SecondTasklet;
import com.programacionparaaprender.service.StudentService;
import com.programacionparaaprender.writer.FirstItemWriter;
import com.programacionparaaprender.writer.FirstItemWriterCsv;
import com.programacionparaaprender.writer.FirstItemWriterJdbc;
import com.programacionparaaprender.writer.FirstItemWriterJson;
import com.programacionparaaprender.writer.FirstItemWriterResponse;
import com.programacionparaaprender.writer.FirstItemWriterXml;

import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

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

	@Autowired
	FirstItemWriterJdbc firstItemWriterJdbc;

	@Autowired
	FirstItemWriterResponse firstItemWriterResponse;

	@Autowired
	private DataSource datasource;

	@Autowired
	private StudentService studentService;

	@Autowired
	private FirstItemProcessorJson firstItemProcessorJson;

	@Autowired
	private FirstItemProcessorXml firstItemProcessorXml;

	@Autowired
	private FirstItemProcessorChunk firstItemProcessorChunk;
	
	@Autowired
	private FirstItemProcessorChunkError firstItemProcessorChunkError;

	@Autowired
	private SkipListener skipListener;


	@Autowired
	private SkipListenerImpl skipListenerImpl;
	
	private String base = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\";
	private String baseinput = "C:/Users/luis1/Documents/htdocs/telefonica/spring-batch/inputFile/";
	
	/*
	 * @Bean
	 * 
	 * @ConfigurationProperties(prefix = "spring.datasource")
	 * 
	 * @Primary public DataSource datasource() { return
	 * DataSourceBuilder.create().build(); }
	 * 
	 * @Bean
	 * 
	 * @ConfigurationProperties(prefix = "spring.universitydatasource") public
	 * DataSource universitydatasource() { return
	 * DataSourceBuilder.create().build(); }
	 */

	@Bean
	public Job mysqlToXmlJob() {
		// mysqlToXmlJob
		return jobBuilderFactory.get("mysqlToXmlJob").incrementer(new RunIdIncrementer()).start(stepMysqlToXml())
				// .next(secondStep())
				.build();
	}

	@Bean
	public Job mysqlToJsonJob() {
		return jobBuilderFactory.get("Job mysql a json").incrementer(new RunIdIncrementer()).start(stepMysqlToJson())
				// .next(secondStep())
				.build();
	}

	@Bean
	public Job mysqlToCsvJob() {
		return jobBuilderFactory.get("Job mysql a csv").incrementer(new RunIdIncrementer()).start(StepMysqlToCsv())
				// .next(secondStep())
				.build();
	}
	
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job").incrementer(new RunIdIncrementer()).start(firstStep())
				.next(secondStep()).listener(firstJobListener).build();
	}
	
	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("secondJob").incrementer(new RunIdIncrementer())
				// .start(firstChunkStepNew10())
				.start(stepCreateJsonError())
				// .next(secondStep())
				.build();
	}
	
	
	@Bean
	public Job csvToJsonJob() {
		return jobBuilderFactory
				.get("Job csv a json")
				.incrementer(new RunIdIncrementer())
				.start(csvToJsonStep())
				// .next(secondStep())
				.build();
	}
	
	
	private Step csvToJsonStep() {
		return stepBuilderFactory.get("First Chunk nuevo Step1").<StudentCsv, StudentJson>chunk(3)
				.reader(flatFileItemReader(null))
				.processor(firstItemProcessorChunk)
				.writer(jsonFileItemWriterCsvToJson(null))
				//.faultTolerant()
				//.skip(Throwable.class)
				// .skip(NullPointerException.class)
				//.skipLimit(Integer.MAX_VALUE)
				//.skipLimit(100)
				//.skipPolicy(new AlwaysSkipItemSkipPolicy())
				//.retryLimit(3)
				//.retry(Throwable.class)
				//.listener(skipListener)
				//.listener(skipListenerImpl)
				.build();
	}
	
	private Step StepMysqlToCsv() {
		return stepBuilderFactory.get("First Chunk Step").<StudentJdbc, StudentJdbc>chunk(3)
				// .reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				// .processor(firstItemProcessor)
				.writer(flatFileItemWriter(null)).build();
	}
	
	private Step stepMysqlToJson() {
		return stepBuilderFactory.get("First Chunk Step Json").<StudentJdbc, StudentJson>chunk(3)
				// .reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader()).processor(firstItemProcessorJson).writer(jsonFileItemWriterJson(null))
				.build();
	}
	
	private Step stepMysqlToXml() {
		return stepBuilderFactory.get("First Chunk Step Json").<StudentJdbc, StudentXml>chunk(3)
				// .reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader()).processor(firstItemProcessorXml).writer(staxEventItemWriter(null))
				.build();
	}
	
	@StepScope
	@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<StudentCsv>();

		// String fileName = fileSystemResource.getPath();
		String fileName = baseinput + "students.csv";
		FileSystemResource fileSystemResource1 = new FileSystemResource(new File(fileName));
		
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
		 * DefaultLineMapper<StudentCsv> defaultLineMapper = new
		 * DefaultLineMapper<StudentCsv>();
		 * 
		 * DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		 * delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");
		 * 
		 * defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		 * 
		 * BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper = new
		 * BeanWrapperFieldSetMapper<StudentCsv>();
		 * fieldSetMapper.setTargetType(StudentCsv.class);
		 * 
		 * defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		 * 
		 * flatFileItemReader.setLineMapper(defaultLineMapper);
		 */

		flatFileItemReader.setLinesToSkip(1);

		return flatFileItemReader;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriterCsvToJson(
			@Value("#{jobParameters['outFilesJson']}") FileSystemResource fileSystemResource) {
		// String fileName = fileSystemResource.getPath();
		String fileName = base + "students_csv.json";
		File myFile = new File(fileName);

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileSystemResource fileSystemResource1 = new FileSystemResource(new File(fileName));
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter<>(fileSystemResource1,
				new JacksonJsonObjectMarshaller<StudentJson>());

		return jsonFileItemWriter;
	}
	
	@StepScope
	@Bean
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
			@Value("#{jobParameters['outFiles']}") FileSystemResource fileSystemResource) {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<StudentJdbc>();

		// String fileName = fileSystemResource.getPath();
		String fileName = base + "students.csv";
		File myFile = new File(fileName);

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileSystemResource fileSystemResource1 = new FileSystemResource(new File(fileName));
		flatFileItemWriter.setResource(fileSystemResource1);

		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name,Last Name,Email");
			}
		});

		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			{
				// setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] { "id", "firstName", "lastName", "email" });
					}
				});
			}
		});

		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
			@Override
			public void writeFooter(Writer writer) throws IOException {
				writer.write("Created @ " + new Date());
			}
		});

		return flatFileItemWriter;
	}
	
	@StepScope
	@Bean
	public StaxEventItemWriter<StudentXml> staxEventItemWriter(
			@Value("#{jobParameters['outFiles']}") FileSystemResource fileSystemResource) {
		// String fileName = fileSystemResource.getPath();
		String fileName = base + "students.xml";
		File myFile = new File(fileName);

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StaxEventItemWriter<StudentXml> staxEventItemWriter = new StaxEventItemWriter<StudentXml>();
		FileSystemResource fileSystemResource1 = new FileSystemResource(new File(fileName));
		staxEventItemWriter.setResource(fileSystemResource1);
		staxEventItemWriter.setRootTagName("students");

		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});

		return staxEventItemWriter;
	}

	private Step stepCreateJsonError() {
		return stepBuilderFactory.get("First Chunk Step Json").<StudentJdbc, StudentJson>chunk(3)
				// .reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader()).processor(firstItemProcessorJson).writer(jsonFileItemWriterJson(null))
				.faultTolerant().skip(FlatFileParseException.class).skip(NullPointerException.class)
				// .skipLimit(Integer.MAX_VALUE)
				.skipPolicy(new AlwaysSkipItemSkipPolicy()).build();
	}

	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<StudentJdbc>();

		jdbcCursorItemReader.setDataSource(datasource);
		jdbcCursorItemReader
				.setSql("select id, first_name as firstName, last_name as lastName," + "email from student");

		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
			{
				setMappedClass(StudentJdbc.class);
			}
		});
		jdbcCursorItemReader.setCurrentItemCount(2);
		jdbcCursorItemReader.setMaxItemCount(8);

		return jdbcCursorItemReader;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriterJson(
			@Value("#{jobParameters['outFilesJson']}") FileSystemResource fileSystemResource) {
		// String fileName = fileSystemResource.getPath();
		String fileName = base + "students.json";
		File myFile = new File(fileName);

		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileSystemResource fileSystemResource1 = new FileSystemResource(new File(fileName));
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter<>(fileSystemResource1,
				new JacksonJsonObjectMarshaller<StudentJson>());

		return jsonFileItemWriter;
	}
	
	private Step firstStep() {
		return stepBuilderFactory.get("First step").tasklet(firstTasklet()).listener(firstStepListener).build();
	}

	private Step secondStep() {
		return stepBuilderFactory.get("Second step")
				// .tasklet(secondTaskletMetodo(1))
				.tasklet(secondTasklet).build();
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
