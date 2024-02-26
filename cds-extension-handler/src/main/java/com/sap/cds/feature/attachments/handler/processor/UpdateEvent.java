package com.sap.cds.feature.attachments.handler.processor;

import java.io.InputStream;
import java.util.Objects;

import com.sap.cds.CdsData;
import com.sap.cds.feature.attachments.handler.model.AttachmentFieldNames;
import com.sap.cds.feature.attachments.service.AttachmentAccessException;
import com.sap.cds.feature.attachments.service.AttachmentService;
import com.sap.cds.feature.attachments.service.model.AttachmentUpdateEventContext;
import com.sap.cds.ql.cqn.Path;
import com.sap.cds.reflect.CdsElement;

public class UpdateEvent implements AttachmentEvent {

		private final AttachmentService attachmentService;

		public UpdateEvent(AttachmentService attachmentService) {
				this.attachmentService = attachmentService;
		}

		@Override
		public Object processEvent(Path path, CdsElement element, AttachmentFieldNames fieldNames, Object value, CdsData existingData, String attachmentId) throws AttachmentAccessException {
				var storageContext = AttachmentUpdateEventContext.create();
				storageContext.setAttachmentId(attachmentId);

				var values = path.target().values();
				storageContext.setContent((InputStream) value);

				fieldNames.mimeTypeField().ifPresent(anno -> {
						var annotationValue = values.get(anno);
						var mimeType = Objects.nonNull(annotationValue) ? annotationValue : existingData.get(anno);
						storageContext.setMimeType((String) mimeType);
				});

				fieldNames.fileNameField().ifPresent(anno -> {
						var annotationValue = values.get(anno);
						var fileName = Objects.nonNull(annotationValue) ? annotationValue : existingData.get(anno);
						storageContext.setFileName((String) fileName);
				});
				storageContext.setDocumentId((String) existingData.get(fieldNames.documentIdField().get()));

				var result = attachmentService.updateAttachment(storageContext);
				fieldNames.documentIdField().ifPresent(doc -> path.target().values().put(doc, result.documentId()));
				return result.isExternalStored() ? null : value;
		}
}