package dev.hiresense.api.job;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRepo extends JpaRepository<Job, UUID> {}
