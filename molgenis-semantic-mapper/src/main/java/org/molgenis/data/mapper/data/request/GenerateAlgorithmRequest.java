package org.molgenis.data.mapper.data.request;

import com.google.auto.value.AutoValue;
import org.hibernate.validator.constraints.NotEmpty;
import org.molgenis.gson.AutoGson;

import javax.validation.constraints.NotNull;
import java.util.List;

@AutoValue
@AutoGson(autoValueClass = AutoValue_GenerateAlgorithmRequest.class)
public abstract class GenerateAlgorithmRequest
{
	@NotNull
	public abstract String getTargetEntityTypeId();

	@NotNull
	public abstract String getTargetAttributeName();

	@NotNull
	public abstract String getSourceEntityTypeId();

	@NotEmpty
	public abstract List<String> getSourceAttributes();
}
