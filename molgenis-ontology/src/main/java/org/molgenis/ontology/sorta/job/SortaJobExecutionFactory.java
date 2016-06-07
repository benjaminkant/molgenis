package org.molgenis.ontology.sorta.job;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.ontology.sorta.meta.SortaJobExecutionMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SortaJobExecutionFactory
		extends AbstractSystemEntityFactory<SortaJobExecution, SortaJobExecutionMetaData, String>
{
	@Autowired
	SortaJobExecutionFactory(SortaJobExecutionMetaData sortaJobExecutionMetaData)
	{
		super(SortaJobExecution.class, sortaJobExecutionMetaData, String.class);
	}
}
