package dev.hiresense.api.resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ResumeEmbeddingRepo extends JpaRepository<ResumeEmbedding, UUID> { }
