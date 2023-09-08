package com.programacionparaaprender.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.programacionparaaprender.app.SpringBatchApplication;
import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJdbc;
import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentResponse;
import com.programacionparaaprender.model.StudentXml;
import com.programacionparaaprender.processor.FirstItemProcessor;
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
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
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
	
	/*
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	@Primary
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.universitydatasource")
	public DataSource universitydatasource() {
		return DataSourceBuilder.create().build();
	}
	*/
	
	@Bean
	public Job mysqlToXmlJob() {
		//mysqlToXmlJob
		return jobBuilderFactory.get("mysqlToXmlJob")
		.incrementer(new RunIdIncrementer())
		.start(stepMysqlToXml())
		//.next(secondStep())
		.build();
	}
	
	@Bean
	public Job mysqlToJsonJob() {
		return jobBuilderFactory.get("Job mysql a json")
		.incrementer(new RunIdIncrementer())
		.start(stepMysqlToJson())
		//.next(secondStep())
		.build();
	}
	
	@Bean
	public Job mysqlToCsvJob() {
		return jobBuilderFactory.get("Job mysql a csv")
		.incrementer(new RunIdIncrementer())
		.start(StepMysqlToCsv())
		//.next(secondStep())
		.build();
	}
	
	@Bean
	public Job secondJob() {
		//csvToMysqlJob
		return jobBuilderFactory.get("secondJob")
		.incrementer(new RunIdIncrementer())
		.start(firstChunkStepNew10())
		//.next(secondStep())
		.build();
	}
	
	private Step firstChunkStepNew10() {
		return stepBuilderFactory.get("First Chunk Step Json")
				.<StudentCsv, StudentCsv>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(flatFileItemReaderDatabase(null))
				//.processor(firstItemProcessorXml)
				.writer(jdbcBatchItemWriter1())
				.build();
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter1() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = 
				new JdbcBatchItemWriter<StudentCsv>();
		
		jdbcBatchItemWriter.setDataSource(datasource);
		jdbcBatchItemWriter.setSql(
				"insert into student2(id, first_name, last_name, email) "
				+ "values (?,?,?,?)");
		
		jdbcBatchItemWriter.setItemPreparedStatementSetter(
				new ItemPreparedStatementSetter<StudentCsv>() {
			
			@Override
			public void setValues(StudentCsv item, PreparedStatement ps) throws SQLException {
				ps.setLong(1, item.getId());
				ps.setString(2, item.getFirstName());
				ps.setString(3, item.getLastName());
				ps.setString(4, item.getEmail());
			}
		});
		
		return jdbcBatchItemWriter;
	}
	
	private Step firstChunkStepNew9() {
		return stepBuilderFactory.get("First Chunk Step Json")
				.<StudentCsv, StudentCsv>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(flatFileItemReaderDatabase(null))
				//.processor(firstItemProcessorXml)
				.writer(jdbcBatchItemWriter())
				.build();
	}
	
	@StepScope
	@Bean
	public FlatFileItemReader<StudentCsv> flatFileItemReaderDatabase(
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
	
	
	@Bean
	public JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter() {
		JdbcBatchItemWriter<StudentCsv> jdbcBatchItemWriter = 
				new JdbcBatchItemWriter<StudentCsv>();
		
		jdbcBatchItemWriter.setDataSource(datasource);
		jdbcBatchItemWriter.setSql(
				"insert into student2(id, first_name, last_name, email) "
				+ "values (:id, :firstName, :lastName, :email)");
		
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<StudentCsv>());
		
		return jdbcBatchItemWriter;
	}
	
	private Step stepMysqlToXml() {
		return stepBuilderFactory.get("First Chunk Step Json")
				.<StudentJdbc, StudentXml>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				.processor(firstItemProcessorXml)
				.writer(staxEventItemWriter(null))
				.build();
	}
	
	@StepScope
	@Bean
	public StaxEventItemWriter<StudentXml> staxEventItemWriter(
			@Value("#{jobParameters['outFiles']}") FileSystemResource fileSystemResource) {
		//String fileName = fileSystemResource.getPath();
		String fileName = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.xml";
		File myFile = new File(fileName);
	    
		if(!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		StaxEventItemWriter<StudentXml> staxEventItemWriter = 
				new StaxEventItemWriter<StudentXml>();
		FileSystemResource fileSystemResource1 = new FileSystemResource(
				new File("C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.xml"));
		staxEventItemWriter.setResource(fileSystemResource1);
		staxEventItemWriter.setRootTagName("students");
		
		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});
		
		return staxEventItemWriter;
	}
	
	private Step stepMysqlToJson() {
		return stepBuilderFactory.get("First Chunk Step Json")
				.<StudentJdbc, StudentJson>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				.processor(firstItemProcessorJson)
				.writer(jsonFileItemWriterJson(null))
				.build();
	}
	
	private Step firstChunkStepNew6() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentJdbc, StudentJdbc>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				//.processor(firstItemProcessor)
				.writer(jsonFileItemWriterJdbc(null))
				.build();
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriterJson(
			@Value("#{jobParameters['outFilesJson']}") FileSystemResource fileSystemResource) {
		//String fileName = fileSystemResource.getPath();
		String fileName = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.json";
		File myFile = new File(fileName);
	    
		if(!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		FileSystemResource fileSystemResource1 = new FileSystemResource(
				new File("C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.json"));
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = 
				new JsonFileItemWriter<>(fileSystemResource1, 
						new JacksonJsonObjectMarshaller<StudentJson>());
		
		return jsonFileItemWriter;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJdbc> jsonFileItemWriterJdbc(
			@Value("#{jobParameters['outFilesJson']}") FileSystemResource fileSystemResource) {
		String fileName = fileSystemResource.getPath();
		File myFile = new File(fileName);
	    
		if(!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		JsonFileItemWriter<StudentJdbc> jsonFileItemWriter = 
				new JsonFileItemWriter<>(fileSystemResource, 
						new JacksonJsonObjectMarshaller<StudentJdbc>());
		
		return jsonFileItemWriter;
	}
	
	private Step StepMysqlToCsv() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentJdbc, StudentJdbc>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				//.processor(firstItemProcessor)
				.writer(flatFileItemWriter(null))
				.build();
	}
	
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = 
				new JdbcCursorItemReader<StudentJdbc>();
		
		jdbcCursorItemReader.setDataSource(datasource);
		jdbcCursorItemReader.setSql(
				"select id, first_name as firstName, last_name as lastName,"
				+ "email from student");
		
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
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(
			@Value("#{jobParameters['outFiles']}") FileSystemResource fileSystemResource) {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = 
				new FlatFileItemWriter<StudentJdbc>();
		
		//String fileName = fileSystemResource.getPath();
		String fileName = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.csv";
	    File myFile = new File(fileName);
	    
		if(!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		FileSystemResource fileSystemResource1 = new FileSystemResource(
				new File("C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\outFiles\\students.csv"));
		flatFileItemWriter.setResource(fileSystemResource1);
		
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name,Last Name,Email");
			}
		});
		
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>() {
			{
				//setDelimiter("|");
				setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>() {
					{
						setNames(new String[] {"id", "firstName", "lastName", "email"});
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
	
	
	
	private Step firstChunkStepNew4() {
		return stepBuilderFactory.get("First Chunk Step StudentResponse")
				.<StudentResponse, StudentResponse>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(itemReaderAdapter())
				//.processor(firstItemProcessor)
				.writer(firstItemWriterResponse)
				.build();
	}
	
	public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
		ItemReaderAdapter<StudentResponse> itemReaderAdapter = 
				new ItemReaderAdapter<StudentResponse>();
		
		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("getStudent");
		itemReaderAdapter.setArguments(new Object[] {1L, "Ejemplo"});
		return itemReaderAdapter;
	}
	
	private Step firstChunkStepNew3() {
		return stepBuilderFactory.get("First Chunk Step")
				.<StudentJdbc, StudentJdbc>chunk(3)
				//.reader(flatFileItemReader(null))
				.reader(jdbcCursorItemReader())
				//.processor(firstItemProcessor)
				.writer(firstItemWriterJdbc)
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
	
	
	
	//@StepScope
	//@Bean
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
