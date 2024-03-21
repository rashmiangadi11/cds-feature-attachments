package com.sap.cds.feature.attachments.handler.applicationservice.processor.modifyevents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cds.feature.attachments.generated.test.cds4j.com.sap.attachments.Attachments;
import com.sap.cds.feature.attachments.service.AttachmentService;
import com.sap.cds.ql.cqn.Path;
import com.sap.cds.ql.cqn.ResolvedSegment;

class DeleteContentAttachmentEventTest {

	private DeleteContentAttachmentEvent cut;
	private AttachmentService attachmentService;
	private Path path;
	private Map<String, Object> currentData;

	@BeforeEach
	void setup() {
		attachmentService = mock(AttachmentService.class);
		cut = new DeleteContentAttachmentEvent(attachmentService);

		path = mock(Path.class);
		var target = mock(ResolvedSegment.class);
		currentData = new HashMap<>();
		when(path.target()).thenReturn(target);
		when(target.values()).thenReturn(currentData);
	}

	@Test
	void documentIsExternallyDeleted() {
		var value = "test";
		var documentId = "some id";
		var data = Attachments.create();
		data.setDocumentId(documentId);

		var expectedValue = cut.processEvent(path, null, value, data, null);

		assertThat(expectedValue).isEqualTo(value);
		assertThat(data.getDocumentId()).isEqualTo(documentId);
		verify(attachmentService).deleteAttachment(documentId);
		assertThat(currentData).containsEntry(Attachments.DOCUMENT_ID, null);
	}

	@Test
	void documentIsNotExternallyDeletedBecauseDoesNotExistBefore() {
		var value = "test";
		var data = Attachments.create();

		var expectedValue = cut.processEvent(path, null, value, data, null);

		assertThat(expectedValue).isEqualTo(value);
		assertThat(data.getDocumentId()).isNull();
		verifyNoInteractions(attachmentService);
		assertThat(currentData).containsEntry(Attachments.DOCUMENT_ID, null);
	}

}