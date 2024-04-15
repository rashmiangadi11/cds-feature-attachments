package com.sap.cds.feature.attachments.service.model.servicehandler;

import com.sap.cds.feature.attachments.service.AttachmentService;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.EventName;

/**
	* The {@link AttachmentMarkAsDeletedEventContext} is used to mark an attachment as deleted.
	*/
@EventName(AttachmentService.EVENT_MARK_AS_DELETED)
public interface AttachmentMarkAsDeletedEventContext extends EventContext {

	/**
		* Creates an {@link EventContext} already overlayed with this interface. The event is set to be
		* {@link AttachmentService#EVENT_MARK_AS_DELETED}
		*
		* @return the {@link AttachmentMarkAsDeletedEventContext}
		*/
	static AttachmentMarkAsDeletedEventContext create() {
		return EventContext.create(AttachmentMarkAsDeletedEventContext.class, null);
	}

	/**
		* @return The document id for the attachment to be deleted or {@code null} if no id was specified
		*/
	String getDocumentId();

	/**
		* Sets the attachment id for the attachment to be deleted
		*
		* @param documentId The document id of the attachment to be deleted
		*/
	void setDocumentId(String documentId);

}