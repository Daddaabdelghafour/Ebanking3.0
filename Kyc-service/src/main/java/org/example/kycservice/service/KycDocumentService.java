package org.example.kycservice.service;

import jakarta.transaction.Transactional;
import org.example.kycservice.Enum.KycDocumentType;
import org.example.kycservice.dto.KycDocumentDTO;
import org.example.kycservice.entity.KycDocument;
import org.example.kycservice.entity.KycRecord;
import org.example.kycservice.repo.KycDocumentRepository;
import org.example.kycservice.repo.KycRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
@Service
public class KycDocumentService {
    private final KycDocumentRepository kycDocumentRepository;
    private final KycRecordRepository kycRecordRepository;
    public KycDocumentService(KycDocumentRepository kycDocumentRepository, KycRecordRepository kycRecordRepository) {
        this.kycDocumentRepository = kycDocumentRepository;
        this.kycRecordRepository = kycRecordRepository;
    }


    @Transactional
    public void uploadDocument(UUID userId, Set<KycDocumentDTO> documents ) {
            KycRecord record = kycRecordRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("KYC record not found for userId: " + userId));

            for(KycDocumentDTO doc : documents)
            {
                if(doc!=null)
                {
                    KycDocument existing = record.getDocuments().stream()
                            .filter(d -> d.getType().name().equalsIgnoreCase(doc.type()))
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {
                        existing.setUrl(doc.url());
                        existing.setDescription(doc.description());
                        existing.setMotifRefusal(null);
                        kycDocumentRepository.save(existing);
                    } else {
                        KycDocument document = new KycDocument();
                        document.setKycRecord(record);
                        document.setType(KycDocumentType.valueOf(doc.type().toUpperCase()));
                        document.setDescription(doc.description());
                        document.setUrl(doc.url());
                        document.setMotifRefusal(null);
                        kycDocumentRepository.save(document);
                        record.getDocuments().add(document);
                    }


                }
            }
    }



}
