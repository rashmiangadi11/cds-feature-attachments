package com.sap.cds.feature.attachments.handler.processor.applicationevents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.Row;
import com.sap.cds.feature.attachments.handler.model.AttachmentFieldNames;
import com.sap.cds.feature.attachments.handler.processor.modifyevents.ModifyAttachmentEvent;
import com.sap.cds.feature.attachments.handler.processor.modifyevents.ModifyAttachmentEventFactory;
import com.sap.cds.impl.RowImpl;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.runtime.CdsRuntime;

abstract class ModifyApplicationEventTestBase {

		static CdsRuntime runtime;

		PersistenceService persistenceService;
		ModifyAttachmentEventFactory eventFactory;
		ArgumentCaptor<AttachmentFieldNames> fieldNamesArgumentCaptor;
		CdsData cdsData;
		ModifyAttachmentEvent event;

		void setup() {
				persistenceService = mock(PersistenceService.class);
				eventFactory = mock(ModifyAttachmentEventFactory.class);

				fieldNamesArgumentCaptor = ArgumentCaptor.forClass(AttachmentFieldNames.class);
				cdsData = mock(CdsData.class);
				event = mock(ModifyAttachmentEvent.class);
		}

		Row mockSelectionResult() {
				var row = RowImpl.row(cdsData);
				var result = mock(Result.class);
				when(result.single()).thenReturn(row);
				when(persistenceService.run(any(CqnSelect.class))).thenReturn(result);
				return row;
		}

		void verifyFilledFieldNames() {
				//field names taken from model for entity Attachments defined in csn which can be found in AttachmentsHandlerTestBase.CSN_FILE_PATH
				var fieldNames = fieldNamesArgumentCaptor.getValue();
				assertThat(fieldNames.keyField()).isEqualTo("ID");
				assertThat(fieldNames.documentIdField()).isPresent().contains("documentId");
				assertThat(fieldNames.mimeTypeField()).isPresent().contains("mimeType");
				assertThat(fieldNames.fileNameField()).isPresent().contains("filename");
		}

}