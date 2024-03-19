package com.sap.cds.feature.attachments.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sap.cds.CdsData;
import com.sap.cds.CdsException;
import com.sap.cds.Result;
import com.sap.cds.Row;
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
	CdsData cdsData;
	ModifyAttachmentEvent event;

	void setup() {
		persistenceService = mock(PersistenceService.class);
		eventFactory = mock(ModifyAttachmentEventFactory.class);

		cdsData = mock(CdsData.class);
		event = mock(ModifyAttachmentEvent.class);
	}

	Row mockSelectionResult() {
		return mockSelectionResult(1L);
	}

	Row mockSelectionResult(long rowCount) {
		var row = RowImpl.row(cdsData);
		var result = mock(Result.class);
		when(result.single()).thenReturn(row);
		when(result.rowCount()).thenReturn(rowCount);
		if (rowCount > 1) {
			when(result.single()).thenThrow(new CdsException("More than one result"));
		}
		when(persistenceService.run(any(CqnSelect.class))).thenReturn(result);
		return row;
	}

}