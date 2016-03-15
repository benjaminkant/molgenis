package org.molgenis.data.vcf.utils;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.molgenis.MolgenisFieldTypes.COMPOUND;
import static org.molgenis.MolgenisFieldTypes.LONG;
import static org.molgenis.MolgenisFieldTypes.MREF;
import static org.molgenis.MolgenisFieldTypes.STRING;
import static org.molgenis.MolgenisFieldTypes.XREF;
import static org.molgenis.data.EntityMetaData.AttributeRole.ROLE_ID;
import static org.molgenis.data.vcf.VcfRepository.ALT;
import static org.molgenis.data.vcf.VcfRepository.ALT_META;
import static org.molgenis.data.vcf.VcfRepository.CHROM;
import static org.molgenis.data.vcf.VcfRepository.CHROM_META;
import static org.molgenis.data.vcf.VcfRepository.FILTER;
import static org.molgenis.data.vcf.VcfRepository.FILTER_META;
import static org.molgenis.data.vcf.VcfRepository.ID;
import static org.molgenis.data.vcf.VcfRepository.ID_META;
import static org.molgenis.data.vcf.VcfRepository.INFO;
import static org.molgenis.data.vcf.VcfRepository.POS;
import static org.molgenis.data.vcf.VcfRepository.POS_META;
import static org.molgenis.data.vcf.VcfRepository.QUAL;
import static org.molgenis.data.vcf.VcfRepository.QUAL_META;
import static org.molgenis.data.vcf.VcfRepository.REF;
import static org.molgenis.data.vcf.VcfRepository.REF_META;
import static org.molgenis.data.vcf.VcfRepository.SAMPLES;
import static org.molgenis.data.vcf.utils.VcfUtils.checkPreviouslyAnnotatedAndAddMetadata;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.Entity;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.MolgenisInvalidFormatException;
import org.molgenis.data.support.DefaultAttributeMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.molgenis.data.support.MapEntity;
import org.molgenis.data.vcf.VcfRepository;
import org.molgenis.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

@Test
public class VcfUtilsTest
{
	private static final Logger LOG = LoggerFactory.getLogger(VcfUtilsTest.class);
	private final DefaultEntityMetaData annotatedEntityMetadata = new DefaultEntityMetaData("test");
	public DefaultEntityMetaData metaDataCanAnnotate = new DefaultEntityMetaData("test");
	public DefaultEntityMetaData metaDataCantAnnotate = new DefaultEntityMetaData("test");

	public AttributeMetaData attributeMetaDataChrom = new DefaultAttributeMetaData(CHROM,
			MolgenisFieldTypes.FieldTypeEnum.STRING);
	public AttributeMetaData attributeMetaDataPos = new DefaultAttributeMetaData(POS,
			MolgenisFieldTypes.FieldTypeEnum.LONG);
	public AttributeMetaData attributeMetaDataRef = new DefaultAttributeMetaData(REF,
			MolgenisFieldTypes.FieldTypeEnum.STRING);
	public AttributeMetaData attributeMetaDataAlt = new DefaultAttributeMetaData(ALT,
			MolgenisFieldTypes.FieldTypeEnum.STRING);
	public AttributeMetaData attributeMetaDataCantAnnotateChrom = new DefaultAttributeMetaData(CHROM,
			MolgenisFieldTypes.FieldTypeEnum.LONG);
	public ArrayList<Entity> input = new ArrayList<Entity>();
	public Entity entity;
	public Entity entity1;
	public Entity entity2;
	public Entity entity3;
	public Entity entity4;

	public ArrayList<Entity> entities;

	@BeforeMethod
	public void beforeMethod() throws IOException
	{
		metaDataCanAnnotate.addAttributeMetaData(attributeMetaDataChrom, ROLE_ID);
		metaDataCanAnnotate.addAttributeMetaData(attributeMetaDataPos);

		metaDataCantAnnotate.addAttributeMetaData(attributeMetaDataCantAnnotateChrom);
		metaDataCantAnnotate.addAttributeMetaData(attributeMetaDataPos);
		metaDataCantAnnotate.addAttributeMetaData(attributeMetaDataRef);
		metaDataCantAnnotate.addAttributeMetaData(attributeMetaDataAlt);

		entity = new MapEntity(metaDataCanAnnotate);
		entity1 = new MapEntity(metaDataCanAnnotate);
		entity2 = new MapEntity(metaDataCanAnnotate);
		entity3 = new MapEntity(metaDataCanAnnotate);
		entity4 = new MapEntity(metaDataCanAnnotate);

		metaDataCanAnnotate.addAttributeMetaData(
				new DefaultAttributeMetaData(VcfRepository.ID, MolgenisFieldTypes.FieldTypeEnum.STRING));
		metaDataCanAnnotate.addAttributeMetaData(attributeMetaDataRef);
		metaDataCanAnnotate.addAttributeMetaData(attributeMetaDataAlt);
		metaDataCanAnnotate.addAttributeMetaData(
				new DefaultAttributeMetaData(VcfRepository.QUAL, MolgenisFieldTypes.FieldTypeEnum.STRING));
		metaDataCanAnnotate.addAttributeMetaData(
				new DefaultAttributeMetaData(VcfRepository.FILTER, MolgenisFieldTypes.FieldTypeEnum.STRING));
		DefaultAttributeMetaData INFO = new DefaultAttributeMetaData(VcfRepository.INFO,
				MolgenisFieldTypes.FieldTypeEnum.COMPOUND);
		DefaultAttributeMetaData AC = new DefaultAttributeMetaData("AC", MolgenisFieldTypes.FieldTypeEnum.STRING);
		DefaultAttributeMetaData AN = new DefaultAttributeMetaData("AN", MolgenisFieldTypes.FieldTypeEnum.STRING);
		DefaultAttributeMetaData GTC = new DefaultAttributeMetaData("GTC", MolgenisFieldTypes.FieldTypeEnum.STRING);
		INFO.addAttributePart(AC);
		INFO.addAttributePart(AN);
		INFO.addAttributePart(GTC);
		metaDataCanAnnotate.addAttributeMetaData(INFO);

		annotatedEntityMetadata.addAttributeMetaData(attributeMetaDataChrom, ROLE_ID);
		annotatedEntityMetadata.addAttributeMetaData(attributeMetaDataPos);
		annotatedEntityMetadata.addAttributeMetaData(
				new DefaultAttributeMetaData(VcfRepository.ID, MolgenisFieldTypes.FieldTypeEnum.STRING));
		annotatedEntityMetadata.addAttributeMetaData(attributeMetaDataRef);
		annotatedEntityMetadata.addAttributeMetaData(attributeMetaDataAlt);

		annotatedEntityMetadata.addAttributeMetaData(
				new DefaultAttributeMetaData(VcfRepository.QUAL, MolgenisFieldTypes.FieldTypeEnum.STRING));
		annotatedEntityMetadata.addAttributeMetaData(
				(new DefaultAttributeMetaData(VcfRepository.FILTER, MolgenisFieldTypes.FieldTypeEnum.STRING))
						.setDescription(
								"Test that description is not: '" + VcfRepository.DEFAULT_ATTRIBUTE_DESCRIPTION + "'"));
		INFO.addAttributePart(new DefaultAttributeMetaData("ANNO", MolgenisFieldTypes.FieldTypeEnum.STRING));
		annotatedEntityMetadata.addAttributeMetaData(INFO);

		entity1.set(VcfRepository.CHROM, "1");
		entity1.set(VcfRepository.POS, 10050000);
		entity1.set(VcfRepository.ID, "test21");
		entity1.set(VcfRepository.REF, "G");
		entity1.set(VcfRepository.ALT, "A");
		entity1.set(VcfRepository.QUAL, ".");
		entity1.set(VcfRepository.FILTER, "PASS");
		entity1.set("AC", "21");
		entity1.set("AN", "22");
		entity1.set("GTC", "0,1,10");

		entity2.set(VcfRepository.CHROM, "1");
		entity2.set(VcfRepository.POS, 10050001);
		entity2.set(VcfRepository.ID, "test22");
		entity2.set(VcfRepository.REF, "G");
		entity2.set(VcfRepository.ALT, "A");
		entity2.set(VcfRepository.QUAL, ".");
		entity2.set(VcfRepository.FILTER, "PASS");

		entity3.set(VcfRepository.CHROM, "1");
		entity3.set(VcfRepository.POS, 10050002);
		entity3.set(VcfRepository.ID, "test23");
		entity3.set(VcfRepository.REF, "G");
		entity3.set(VcfRepository.ALT, "A");
		entity3.set(VcfRepository.QUAL, ".");
		entity3.set(VcfRepository.FILTER, "PASS");

		entities = new ArrayList<>();
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
	}

	// regression test for https://github.com/molgenis/molgenis/issues/3643
	@Test
	public void convertToVcfInfoGtFirst() throws MolgenisDataException, IOException
	{
		String formatDpAttrName = "DP";
		String formatEcAttrName = "EC";
		String formatGtAttrName = VcfRepository.FORMAT_GT;

		String idAttrName = "idAttr";
		String sampleIdAttrName = VcfRepository.NAME;

		DefaultEntityMetaData sampleEntityMeta = new DefaultEntityMetaData("vcfSampleEntity");
		sampleEntityMeta.addAttribute(sampleIdAttrName, ROLE_ID);
		sampleEntityMeta.addAttribute(formatDpAttrName);
		sampleEntityMeta.addAttribute(formatEcAttrName);
		sampleEntityMeta.addAttribute(formatGtAttrName);

		DefaultEntityMetaData entityMeta = new DefaultEntityMetaData("vcfEntity");
		entityMeta.addAttribute(idAttrName, ROLE_ID);
		entityMeta.addAttributeMetaData(CHROM_META);
		entityMeta.addAttributeMetaData(POS_META);
		entityMeta.addAttributeMetaData(ID_META);
		entityMeta.addAttributeMetaData(REF_META);
		entityMeta.addAttributeMetaData(ALT_META);
		entityMeta.addAttributeMetaData(QUAL_META);
		entityMeta.addAttributeMetaData(FILTER_META);
		entityMeta.addAttribute(VcfRepository.INFO).setDataType(MolgenisFieldTypes.COMPOUND);
		entityMeta.addAttribute(SAMPLES).setDataType(MolgenisFieldTypes.MREF).setRefEntity(sampleEntityMeta);

		Entity sampleEntity = new MapEntity(sampleEntityMeta);
		sampleEntity.set(sampleIdAttrName, "0");
		sampleEntity.set(formatDpAttrName, "5");
		sampleEntity.set(formatEcAttrName, "5");
		sampleEntity.set(formatGtAttrName, "1/1");

		Entity vcfEntity = new MapEntity(entityMeta);
		vcfEntity.set(idAttrName, "0");
		vcfEntity.set(CHROM, "1");
		vcfEntity.set(POS, "565286");
		vcfEntity.set(ID, "rs1578391");
		vcfEntity.set(REF, "C");
		vcfEntity.set(ALT, "T");
		vcfEntity.set(QUAL, null);
		vcfEntity.set(FILTER, "flt");
		vcfEntity.set(INFO, null);
		vcfEntity.set(SAMPLES, Arrays.asList(sampleEntity));
		vcfEntity.set(formatDpAttrName, "AB_val");
		vcfEntity.set(formatEcAttrName, "AD_val");
		vcfEntity.set(formatGtAttrName, "GT_val");

		StringWriter strWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(strWriter);
		try
		{
			VcfUtils.writeToVcf(vcfEntity, writer);
		}
		finally
		{
			writer.close();
		}
		assertEquals(strWriter.toString(), "1	565286	rs1578391	C	T	.	flt	.	GT:DP:EC	1/1:5:5");
	}

	@Test
	public void createId()
	{
		assertEquals(VcfUtils.createId(entity1), "yCiiynjHRAtJPcdn7jFDGA");
	}

	@Test
	public void vcfWriterRoundtripTest() throws IOException, MolgenisInvalidFormatException
	{
		final File outputVCFFile = File.createTempFile("output", ".vcf");
		try
		{
			BufferedWriter outputVCFWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputVCFFile), UTF_8));

			File inputVcfFile = new File(ResourceUtils.getFile(getClass(), "/testWriter.vcf").getPath());

			VcfUtils.checkPreviouslyAnnotatedAndAddMetadata(inputVcfFile, outputVCFWriter, Collections.emptyList());

			for (Entity entity : entities)
			{
				VcfUtils.writeToVcf(entity, outputVCFWriter);
				outputVCFWriter.newLine();
			}
			outputVCFWriter.close();

			assertTrue(FileUtils.contentEqualsIgnoreEOL(inputVcfFile, outputVCFFile, "UTF8"));
		}
		finally
		{
			boolean outputVCFFileIsDeleted = outputVCFFile.delete();
			LOG.info("Result test file named: " + outputVCFFile.getName() + " is "
					+ (outputVCFFileIsDeleted ? "" : "not ") + "deleted");
		}
	}

	@Test
	public void vcfWriterAnnotateTest() throws IOException, MolgenisInvalidFormatException
	{
		entity1.set("ANNO", "TEST_test21");
		entity2.set("ANNO", "TEST_test22");
		final File outputVCFFile = File.createTempFile("output", ".vcf");
		try
		{
			BufferedWriter outputVCFWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputVCFFile), UTF_8));

			File inputVcfFile = new File(ResourceUtils.getFile(getClass(), "/testWriter.vcf").getPath());
			File resultVCFWriter = new File(ResourceUtils.getFile(getClass(), "/result_vcfWriter.vcf").getPath());

			VcfUtils.checkPreviouslyAnnotatedAndAddMetadata(inputVcfFile, outputVCFWriter,
					Lists.newArrayList(annotatedEntityMetadata.getAttributes()));

			for (Entity entity : entities)
			{
				MapEntity mapEntity = new MapEntity(entity, annotatedEntityMetadata);
				VcfUtils.writeToVcf(mapEntity, outputVCFWriter);
				outputVCFWriter.newLine();
			}
			outputVCFWriter.close();
			assertTrue(FileUtils.contentEqualsIgnoreEOL(resultVCFWriter, outputVCFFile, "UTF8"));
		}
		finally
		{
			boolean outputVCFFileIsDeleted = outputVCFFile.delete();
			LOG.info("Result test file named: " + outputVCFFile.getName() + " is "
					+ (outputVCFFileIsDeleted ? "" : "not ") + "deleted");
		}
	}

	@Test
	public void vcfWriteMrefTest() throws IOException, MolgenisInvalidFormatException
	{
		DefaultEntityMetaData geneMeta = new DefaultEntityMetaData("GENES");
		geneMeta.addAttribute("id", ROLE_ID).setDataType(STRING).setDescription("GENE Identifier (HGNC symbol)");

		Entity geneEntity1 = new MapEntity(geneMeta);
		geneEntity1.set("id", "BRCA1");

		Entity geneEntity2 = new MapEntity(geneMeta);
		geneEntity2.set("id", "BRCA2");

		List<Entity> geneEntities = Arrays.asList(geneEntity1, geneEntity2);

		DefaultEntityMetaData effectMeta = new DefaultEntityMetaData("EFFECT");
		effectMeta.addAttribute("id", ROLE_ID).setDataType(STRING).setDescription("effect identifier");
		effectMeta.addAttribute(ALT).setDataType(STRING).setDescription("Alternative allele");
		effectMeta.addAttribute("ALT_GENE").setDataType(STRING).setDescription("Alternative allele and gene");
		effectMeta.addAttribute("GENE").setDataType(XREF).setRefEntity(geneMeta)
				.setDescription("Gene identifier (HGNC symbol)");
		effectMeta.addAttribute("EFFECT").setDataType(STRING).setDescription("Level of effect on the gene");
		effectMeta.addAttribute("TYPE").setDataType(STRING).setDescription("Type of mutation");

		Entity effectEntity1 = new MapEntity(effectMeta);
		effectEntity1.set("id", "eff1");
		effectEntity1.set(ALT, "C");
		effectEntity1.set("ALT_GENE", "C_BRCA1");
		effectEntity1.set("GENE", geneEntity1);
		effectEntity1.set("EFFECT", "HIGH");
		effectEntity1.set("TYPE", "STOP_GAIN");

		Entity effectEntity2 = new MapEntity(effectMeta);
		effectEntity2.set("id", "eff2");
		effectEntity2.set(ALT, "T");
		effectEntity2.set("ALT_GENE", "T_BRCA1");
		effectEntity2.set("GENE", geneEntity1);
		effectEntity2.set("EFFECT", "MEDIUM");
		effectEntity2.set("TYPE", "NON_SYNONYMOUS");

		Entity effectEntity3 = new MapEntity(effectMeta);
		effectEntity3.set("id", "eff3");
		effectEntity3.set(ALT, "C");
		effectEntity3.set("ALT_GENE", "C_BRCA2");
		effectEntity3.set("GENE", geneEntity2);
		effectEntity3.set("EFFECT", "LOW");
		effectEntity3.set("TYPE", "SYNONYMOUS");

		Entity effectEntity4 = new MapEntity(effectMeta);
		effectEntity4.set("id", "eff4");
		effectEntity4.set(ALT, "T");
		effectEntity4.set("ALT_GENE", "T_BRCA2");
		effectEntity4.set("GENE", geneEntity2);
		effectEntity4.set("EFFECT", "LOW");
		effectEntity4.set("TYPE", "MISSENSE");

		List<Entity> effectEntities = Arrays.asList(effectEntity1, effectEntity2, effectEntity3, effectEntity4);

		DefaultEntityMetaData vcfMeta = new DefaultEntityMetaData("vcfMeta");
		vcfMeta.addAttribute(CHROM, ROLE_ID).setDataType(STRING);
		vcfMeta.addAttribute(POS).setDataType(LONG);
		vcfMeta.addAttribute(ID).setDataType(STRING);
		vcfMeta.addAttribute(REF).setDataType(STRING);
		vcfMeta.addAttribute(ALT).setDataType(STRING);
		vcfMeta.addAttribute(FILTER).setDataType(COMPOUND);
		vcfMeta.addAttribute(QUAL).setDataType(STRING);

		DefaultAttributeMetaData INFO = new DefaultAttributeMetaData(VcfRepository.INFO,
				MolgenisFieldTypes.FieldTypeEnum.COMPOUND);
		DefaultAttributeMetaData AC = new DefaultAttributeMetaData("AC", MolgenisFieldTypes.FieldTypeEnum.STRING);
		DefaultAttributeMetaData AN = new DefaultAttributeMetaData("AN", MolgenisFieldTypes.FieldTypeEnum.STRING);
		DefaultAttributeMetaData GTC = new DefaultAttributeMetaData("GTC", MolgenisFieldTypes.FieldTypeEnum.STRING);
		INFO.addAttributePart(AC);
		INFO.addAttributePart(AN);
		INFO.addAttributePart(GTC);

		vcfMeta.addAttributeMetaData(INFO);
		vcfMeta.addAttribute("EFFECT").setDataType(MREF).setRefEntity(effectMeta)
				.setDescription("Reference to a list of effects causes by mutations");
		vcfMeta.addAttribute("GENES").setDataType(MREF).setRefEntity(geneMeta).setDescription("Reference to genes");

		Entity vcfEntity = new MapEntity(vcfMeta);
		vcfEntity.set(CHROM, "1");
		vcfEntity.set(POS, "100");
		vcfEntity.set(ID, ".");
		vcfEntity.set(REF, "A");
		vcfEntity.set(ALT, "C,T");
		vcfEntity.set(FILTER, ".");
		vcfEntity.set(QUAL, ".");
		vcfEntity.set("AC", "21");
		vcfEntity.set("AN", "22");
		vcfEntity.set("GTC", "0,1,10");
		vcfEntity.set("EFFECT", effectEntities);
		vcfEntity.set("GENES", geneEntities);

		List<Entity> vcfEntities = newArrayList(vcfEntity);

		List<AttributeMetaData> attributes = newArrayList();
		vcfMeta.getAttributes().forEach(attribute -> attributes.add(attribute));
		effectMeta.getAttributes().forEach(attribute -> attributes.add(attribute));
		geneMeta.getAttributes().forEach(attribute -> attributes.add(attribute));

		final File actualOutputFile = File.createTempFile("output", ".vcf");
		try
		{
			BufferedWriter actualOutputFileWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(actualOutputFile), UTF_8));

			File inputVcfFile = new File(ResourceUtils.getFile(getClass(), "/testMrefVcfWriter_input.vcf").getPath());
			File expectedVcfFile = new File(
					ResourceUtils.getFile(getClass(), "/testMrefVcfWriter_expected_output.vcf").getPath());

			checkPreviouslyAnnotatedAndAddMetadata(inputVcfFile, actualOutputFileWriter, attributes);

			for (Entity entity : vcfEntities)
			{
				MapEntity mapEntity = new MapEntity(entity, vcfMeta);
				VcfUtils.writeToVcf(mapEntity, actualOutputFileWriter);
				actualOutputFileWriter.newLine();
			}
			actualOutputFileWriter.close();
			assertTrue(FileUtils.contentEqualsIgnoreEOL(expectedVcfFile, actualOutputFile, "UTF8"));
		}
		finally
		{
			boolean outputVCFFileIsDeleted = actualOutputFile.delete();
			LOG.info("Result test file named: " + actualOutputFile.getName() + " is "
					+ (outputVCFFileIsDeleted ? "" : "not ") + "deleted");
		}
	}
}