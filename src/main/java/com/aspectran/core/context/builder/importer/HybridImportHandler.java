/**
 * Copyright 2008-2016 Juho Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.builder.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.aspectran.core.context.builder.AssistantLocal;
import com.aspectran.core.context.builder.ContextBuilderAssistant;
import com.aspectran.core.context.builder.ShallowContextBuilderAssistant;
import com.aspectran.core.context.builder.apon.RootAponAssembler;
import com.aspectran.core.context.builder.apon.RootAponDisassembler;
import com.aspectran.core.context.builder.apon.params.RootParameters;
import com.aspectran.core.context.builder.xml.AspectranNodeParser;
import com.aspectran.core.context.rule.type.ImportFileType;
import com.aspectran.core.context.rule.type.ImporterType;
import com.aspectran.core.util.apon.AponReader;
import com.aspectran.core.util.apon.AponWriter;
import com.aspectran.core.util.apon.Parameters;

/**
 * The Class HybridImportHandler.
 */
public class HybridImportHandler extends AbstractImportHandler {
	
	private final ContextBuilderAssistant assistant;
	
	private final String encoding;
	
	private final boolean hybridLoading;
	
	private AspectranNodeParser aspectranNodeParser;
	
	private RootAponDisassembler rootAponDisassembler;
	
	public HybridImportHandler(ContextBuilderAssistant assistant, String encoding, boolean hybridLoading) {
		this.assistant = assistant;
		this.encoding = encoding;
		this.hybridLoading = hybridLoading;
	}

	@Override
	public void handle(Importer importer) throws Exception {
		AssistantLocal assistantLocal = assistant.backupAssistantLocal();

		boolean hybridon = false;
		
		if(importer.getImportFileType() == ImportFileType.APON) {
			Parameters rootParameters = AponReader.parse(importer.getReader(encoding), new RootParameters());
			
			if(rootAponDisassembler == null)
				rootAponDisassembler = new RootAponDisassembler(assistant);
			
			rootAponDisassembler.disassembleAspectran(rootParameters);
		} else {
			if(hybridLoading && importer.getImporterType() == ImporterType.FILE) {
				File aponFile = makeAponFile((FileImporter)importer);

				if(importer.getLastModified() == aponFile.lastModified()) {
					log.info("Rapid loading for Aspectran Context Configuration: " + aponFile);

					hybridon = true;

					Parameters rootParameters = AponReader.parse(aponFile, encoding, new RootParameters());
					
					if(rootAponDisassembler == null) {
						rootAponDisassembler = new RootAponDisassembler(assistant);
					}
					rootAponDisassembler.disassembleRoot(rootParameters);
				}
			}
			
			if(!hybridon) {
				if(aspectranNodeParser == null) {
					aspectranNodeParser = new AspectranNodeParser(assistant);
				}
				aspectranNodeParser.parse(importer.getInputStream());
			}
		}
		
		handle();

		// First default setting is held after configuration loading is completed.
		if(assistantLocal.getReplicatedCount() > 0) {
			assistant.restoreAssistantLocal(assistantLocal);
		}
		
		if(!hybridon && hybridLoading) {
			if(importer.getImporterType() == ImporterType.FILE && importer.getImportFileType() == ImportFileType.XML) {
				saveAsAponFormat((FileImporter)importer);
			}
		}
	}
	
	private void saveAsAponFormat(FileImporter fileImporter) throws Exception {
		log.info("Save as Apon Format: " + fileImporter);
		
		File aponFile = null;
		
		try {
			aponFile = makeAponFile(fileImporter);
			
			AponWriter aponWriter;
			
			if(encoding != null) {
				OutputStream outputStream = new FileOutputStream(aponFile);
				aponWriter = new AponWriter(new OutputStreamWriter(outputStream, encoding));
			} else {
				aponWriter = new AponWriter(new FileWriter(aponFile));
			}
			
			try {
				ContextBuilderAssistant assistant = new ShallowContextBuilderAssistant();
				AspectranNodeParser parser = new AspectranNodeParser(assistant, false);
				parser.parse(fileImporter.getInputStream());
				
				RootAponAssembler assembler = new RootAponAssembler(assistant);
				Parameters rootParameters = assembler.assembleRoot();
				
				aponWriter.comment(aponFile.getAbsolutePath());
				aponWriter.write(rootParameters);
			} finally {
				try {
					aponWriter.close();
				} catch(IOException e) {
					// ignore
				}
			}
			
			aponFile.setLastModified(fileImporter.getLastModified());
		} catch(Exception e) {
			log.error("Cannot save file " +  aponFile + " as APON Format.", e);
		}
	}
	
	private File makeAponFile(FileImporter fileImporter) {
		String basePath = fileImporter.getBasePath();
		String filePath = fileImporter.getFilePath() + "." + ImportFileType.APON.toString();

		return new File(basePath, filePath);
	}

}
