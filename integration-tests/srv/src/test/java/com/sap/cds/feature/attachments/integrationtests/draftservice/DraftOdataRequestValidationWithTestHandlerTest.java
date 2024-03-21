package com.sap.cds.feature.attachments.integrationtests.draftservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.sap.cds.feature.attachments.integrationtests.constants.Profiles;
import com.sap.cds.feature.attachments.integrationtests.testhandler.EventContextHolder;
import com.sap.cds.feature.attachments.service.AttachmentService;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentCreateEventContext;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentDeleteEventContext;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentUpdateEventContext;

@ActiveProfiles(Profiles.TEST_HANDLER_ENABLED)
class DraftOdataRequestValidationWithTestHandlerTest extends DraftOdataRequestValidationBase {

	@Test
	void serviceHandlerIsNotEmpty() {
		assertThat(serviceHandler).isNotNull();
		verifyNoAttachmentEventsCalled();
	}

	@Override
	protected void verifyDocumentId(String documentId, String attachmentId) {
		assertThat(documentId).isNotEmpty().isNotEqualTo(attachmentId);
	}

	@Override
	protected void verifyContent(InputStream attachment, String testContent) {
		assertThat(attachment).isNull();
	}

	@Override
	protected void verifyNoAttachmentEventsCalled() {
		assertThat(serviceHandler.getEventContext()).isEmpty();
	}

	@Override
	protected void clearServiceHandlerContext() {
		serviceHandler.clearEventContext();
	}

	@Override
	protected void verifyEventContextEmptyForEvent(String... events) {
		Arrays.stream(events).forEach(event -> {
			assertThat(serviceHandler.getEventContextForEvent(event)).isEmpty();
		});
	}

	@Override
	protected void verifyTwoCreateEvents(String newAttachmentContent, String newAttachmentEntityContent) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_DELETE_ATTACHMENT, AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_READ_ATTACHMENT);
		var createEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_CREATE_ATTACHMENT);
		assertThat(createEvents).hasSize(2);
		var attachmentContentFound = isAttachmentContentFoundInCreateEvent(createEvents, newAttachmentContent);
		assertThat(attachmentContentFound).isTrue();
		var attachmentEntityContentFound = isAttachmentContentFoundInCreateEvent(createEvents, newAttachmentEntityContent);
		assertThat(attachmentEntityContentFound).isTrue();
	}

	@Override
	protected void verifyTwoReadEvents() {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_DELETE_ATTACHMENT, AttachmentService.EVENT_CREATE_ATTACHMENT, AttachmentService.EVENT_UPDATE_ATTACHMENT);
		var readEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_READ_ATTACHMENT);
		assertThat(readEvents).hasSize(2);
	}

	@Override
	protected void verifyTwoDeleteEvents(String attachmentDocumentId, String attachmentEntityDocumentId) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_CREATE_ATTACHMENT, AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_READ_ATTACHMENT);
		var deleteEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_DELETE_ATTACHMENT);
		assertThat(deleteEvents).hasSize(2);
		verifyDeleteEventContainsDocumentId(deleteEvents, attachmentDocumentId);
		verifyDeleteEventContainsDocumentId(deleteEvents, attachmentEntityDocumentId);
	}

	@Override
	protected void verifyTwoUpdateEvents(String newAttachmentContent, String attachmentDocumentId, String newAttachmentEntityContent, String attachmentEntityDocumentId) {
		var updateEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_UPDATE_ATTACHMENT);
		assertThat(updateEvents).hasSize(2);
		verifyUpdateEventFound(updateEvents, newAttachmentContent, attachmentDocumentId);
		verifyUpdateEventFound(updateEvents, newAttachmentEntityContent, attachmentEntityDocumentId);
	}

	private void verifyUpdateEventFound(List<EventContextHolder> updateEvents, String newContent, String documentId) {
		var eventContentFound = updateEvents.stream().anyMatch(event -> {
			var updateContext = (AttachmentUpdateEventContext) event.context();
			try {
				return Arrays.equals(updateContext.getData().getContent()
																											.readAllBytes(), newContent.getBytes(StandardCharsets.UTF_8)) && updateContext.getDocumentId()
																																																																																														.equals(documentId);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		assertThat(eventContentFound).isTrue();
	}

	private boolean isAttachmentContentFoundInCreateEvent(List<EventContextHolder> createEvents, String newAttachmentContent) {
		return createEvents.stream().anyMatch(event -> {
			var createContext = (AttachmentCreateEventContext) event.context();
			try {
				return Arrays.equals(createContext.getData().getContent()
																											.readAllBytes(), newAttachmentContent.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void verifyDeleteEventContainsDocumentId(List<EventContextHolder> deleteEvents, String documentId) {
		var eventFound = deleteEvents.stream().anyMatch(event -> {
			var deleteContext = (AttachmentDeleteEventContext) event.context();
			return deleteContext.getDocumentId().equals(documentId);
		});
		assertThat(eventFound).isTrue();
	}

}