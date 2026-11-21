package com.bridgelabz.fundoonotes.repository;

import com.bridgelabz.fundoonotes.entity.Label;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByOwnerId(Long ownerId);
    Optional<Label> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
}
