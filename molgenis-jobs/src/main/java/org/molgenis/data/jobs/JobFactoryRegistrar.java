package org.molgenis.data.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Discovers and registers {@link JobFactory} beans with the {@link JobFactoryRegistry}
 */
@Component
public class JobFactoryRegistrar
{
	private final JobFactoryRegistry jobFactoryRegistry;

	@Autowired
	public JobFactoryRegistrar(JobFactoryRegistry jobFactoryRegistry)
	{
		this.jobFactoryRegistry = requireNonNull(jobFactoryRegistry);
	}

	public void register(ContextRefreshedEvent event)
	{
		ApplicationContext ctx = event.getApplicationContext();
		Map<String, JobFactory> scriptRunnerMap = ctx.getBeansOfType(JobFactory.class);
		scriptRunnerMap.values().forEach(this::register);
	}

	private void register(JobFactory jobFactory)
	{
		jobFactoryRegistry.registerJobFactory(jobFactory);
	}
}
