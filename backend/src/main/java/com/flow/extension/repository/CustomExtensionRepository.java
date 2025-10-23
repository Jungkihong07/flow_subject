package com.flow.extension.repository;

import com.flow.extension.entity.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {
    Optional<CustomExtension> findByName(String name);

    boolean existsByName(String name);
}
