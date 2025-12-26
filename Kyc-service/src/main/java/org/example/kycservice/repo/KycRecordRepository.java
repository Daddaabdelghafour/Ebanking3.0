package org.example.kycservice.repo;

import org.example.kycservice.entity.KycRecord;
import org.example.kycservice.Enum.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KycRecordRepository extends JpaRepository<KycRecord, UUID> {

    // Trouver le KYC record d'un utilisateur
    Optional<KycRecord> findByUserId(UUID userId);

    // Trouver tous les KYC records par statut
    List<KycRecord> findByKycStatus(KycStatus status);

    // Vérifier si un utilisateur a déjà un KYC record
    boolean existsByUserId(UUID userId);

    // Trouver les KYC records en attente
    List<KycRecord> findByKycStatusIn(List<KycStatus> statuses);
}