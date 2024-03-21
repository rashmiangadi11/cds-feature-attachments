package com.sap.cds.feature.attachments.integrationtests.nondraftservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.sap.cds.feature.attachments.generated.integration.test.cds4j.com.sap.attachments.Attachments;
import com.sap.cds.feature.attachments.generated.integration.test.cds4j.testservice.AttachmentEntity;
import com.sap.cds.feature.attachments.integrationtests.constants.Profiles;
import com.sap.cds.feature.attachments.service.AttachmentService;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentCreateEventContext;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentDeleteEventContext;
import com.sap.cds.feature.attachments.service.model.servicehandler.AttachmentReadEventContext;

@ActiveProfiles(Profiles.TEST_HANDLER_ENABLED)
class OdataRequestValidationWithTestHandlerTest extends OdataRequestValidationBase {

	@Test
	void serviceHandlerAvailable() {
		assertThat(serviceHandler).isNotNull();
	}

	@Override
	protected void verifyTwoDeleteEvents(AttachmentEntity itemAttachmentEntityAfterChange, Attachments itemAttachmentAfterChange) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_READ_ATTACHMENT, AttachmentService.EVENT_CREATE_ATTACHMENT);
		var deleteEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_DELETE_ATTACHMENT);
		assertThat(deleteEvents).hasSize(2);
		assertThat(deleteEvents.stream().anyMatch(event -> ((AttachmentDeleteEventContext) event.context()).getDocumentId()
																																																							.equals(itemAttachmentEntityAfterChange.getDocumentId()))).isTrue();
		assertThat(deleteEvents.stream().anyMatch(event -> ((AttachmentDeleteEventContext) event.context()).getDocumentId()
																																																							.equals(itemAttachmentAfterChange.getDocumentId()))).isTrue();
	}

	@Override
	protected void verifyNumberOfEvents(String event, int number) {
		assertThat(serviceHandler.getEventContextForEvent(event)).hasSize(number);
	}

	@Override
	protected void verifyDocumentId(Attachments attachmentWithExpectedContent, String attachmentId, String documentId) {
		assertThat(attachmentWithExpectedContent.getDocumentId()).isNotEmpty().isNotEqualTo(documentId);
	}

	@Override
	protected void verifyContentAndDocumentId(Attachments attachment, String content, Attachments itemAttachment) {
		assertThat(attachment.getContent()).isNull();
		assertThat(attachment.getDocumentId()).isNotEmpty().isNotEqualTo(itemAttachment.getId());
	}

	@Override
	protected void verifyContentAndDocumentIdForAttachmentEntity(AttachmentEntity attachment, String content, AttachmentEntity itemAttachment) {
		assertThat(attachment.getContent()).isNull();
		assertThat(attachment.getDocumentId()).isNotEmpty().isNotEqualTo(itemAttachment.getId());
	}

	@Override
	protected void clearServiceHandlerContext() {
		serviceHandler.clearEventContext();
	}

	@Override
	protected void clearServiceHandlerDocuments() {
		serviceHandler.clearDocuments();
	}

	@Override
	protected void verifySingleCreateEvent(String documentId, String content) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_READ_ATTACHMENT, AttachmentService.EVENT_DELETE_ATTACHMENT);
		var createEvent = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_CREATE_ATTACHMENT);
		assertThat(createEvent).hasSize(1).first().satisfies(event -> {
			assertThat(event.context()).isInstanceOf(AttachmentCreateEventContext.class);
			var createContext = (AttachmentCreateEventContext) event.context();
			assertThat(createContext.getDocumentId()).isEqualTo(documentId);
			assertThat(createContext.getData().getContent().readAllBytes()).isEqualTo(content.getBytes(StandardCharsets.UTF_8));
		});
	}

	@Override
	protected void verifySingleDeletionEvent(String documentId) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_CREATE_ATTACHMENT, AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_READ_ATTACHMENT);
		var deleteEvents = serviceHandler.getEventContextForEvent(AttachmentService.EVENT_DELETE_ATTACHMENT);
		assertThat(deleteEvents).hasSize(1).first().satisfies(event -> {
			assertThat(event.context()).isInstanceOf(AttachmentDeleteEventContext.class);
			var deleteContext = (AttachmentDeleteEventContext) event.context();
			assertThat(deleteContext.getDocumentId()).isEqualTo(documentId);
		});
	}

	@Override
	protected void verifySingleReadEvent(String documentId) {
		verifyEventContextEmptyForEvent(AttachmentService.EVENT_CREATE_ATTACHMENT, AttachmentService.EVENT_UPDATE_ATTACHMENT, AttachmentService.EVENT_DELETE_ATTACHMENT);
		var readContext = serviceHandler.getEventContext();
		assertThat(readContext).hasSize(1).first().satisfies(event -> {
			assertThat(event.event()).isEqualTo(AttachmentService.EVENT_READ_ATTACHMENT);
			assertThat(((AttachmentReadEventContext) event.context()).getDocumentId()).isEqualTo(documentId);
		});
	}

	@Override
	protected void verifyNoAttachmentEventsCalled() {
		assertThat(serviceHandler.getEventContext()).isEmpty();
	}

	@Override
	protected void verifyEventContextEmptyForEvent(String... events) {
		Arrays.stream(events).forEach(event -> {
			assertThat(serviceHandler.getEventContextForEvent(event)).isEmpty();
		});
	}

}