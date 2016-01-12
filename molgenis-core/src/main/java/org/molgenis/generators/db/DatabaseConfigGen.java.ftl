package ${package};

import java.beans.PropertyVetoException;
import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.molgenis.data.IdGenerator;
import org.molgenis.data.transaction.MolgenisTransactionManager;
/**
 * Database configuration
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DatabaseConfig implements TransactionManagementConfigurer
{
	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "molgenis";

	@Value("${r"${db_driver:com.mysql.jdbc.Driver}"}")
	private String dbDriverClass;
	@Value("${r"${db_uri:@null}"}")
	private String dbJdbcUri;
	@Value("${r"${db_user:@null}"}")
	private String dbUser;
	@Value("${r"${db_password:@null}"}")
	private String dbPassword;
	
	@Autowired
	private IdGenerator idGenerator;
	
	@Bean
	public DataSource dataSource()
	{
		if(dbDriverClass == null) throw new IllegalArgumentException("db_driver is null");
		if(dbJdbcUri == null) throw new IllegalArgumentException("db_uri is null");
		if(dbUser == null) throw new IllegalArgumentException("please configure the db_user property in your molgenis-server.properties");
		if(dbPassword == null) throw new IllegalArgumentException("please configure the db_password property in your molgenis-server.properties");
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try
		{
			dataSource.setDriverClass(dbDriverClass);
		}
		catch (PropertyVetoException e)
		{
			throw new RuntimeException(e);
		}
		dataSource
				.setJdbcUrl(dbJdbcUri);
		dataSource.setUser(dbUser);
		dataSource.setPassword(dbPassword);
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(150);
		dataSource.setTestConnectionOnCheckin(true);
		dataSource.setIdleConnectionTestPeriod(120);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager()
	{
		return new MolgenisTransactionManager(idGenerator, dataSource());
	}
	
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager()
	{
		return transactionManager();
	}
}
