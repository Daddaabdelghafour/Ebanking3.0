package org.example.kycservice.repo;

import org.example.kycservice.entity.KycDocument;
import org.example.kycservice.entity.KycRecord;
import org.example.kycservice.Enum.KycDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {

    // Trouver par type et KycRecord
    Optional<KycDocument> findByTypeAndKycRecord(KycDocumentType type, KycRecord kycRecord);

    // Trouver tous les documents d'un KycRecord
    List<KycDocument> findByKycRecord(KycRecord kycRecord);

    // Trouver par type seulement
    List<KycDocument> findByType(KycDocumentType type);

    // Requête personnalisée pour trouver par type et kycRecordId
    @Query("SELECT d FROM KycDocument d WHERE d.type = :type AND d.kycRecord.id = :kycRecordId")
    Optional<KycDocument> findByTypeAndKycRecordId(
            @Param("type") KycDocumentType type,
            @Param("kycRecordId") UUID kycRecordId
    );
}