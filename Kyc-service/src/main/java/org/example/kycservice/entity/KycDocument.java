package org.example.kycservice.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import org.example.kycservice.Enum.KycDocumentType;

@Entity
@Table(name = "kyc_document")
@Data
public class KycDocument {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "kyc_record_id")
    private KycRecord kycRecord;

    @Enumerated(EnumType.STRING)
    private KycDocumentType type;
    private String description;
    private String motifRefusal;
    private String url;
    private LocalDateTime uploadedAt;
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        uploadedAt = LocalDateTime.now();
    }

}
