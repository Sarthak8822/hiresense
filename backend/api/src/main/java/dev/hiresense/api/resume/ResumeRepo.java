package dev.hiresense.api.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.util.List;
import java.util.UUID;

public interface ResumeRepo extends JpaRepository<Resume, UUID> {

    List<Resume> findAllResumeByUserId(UUID userId);
    @Query(value =
            "SELECT j.id as job_id, j.title, j.company, 1 - (re.vector <=> je.vector) as similarity " +
                    "FROM resume_embeddings re JOIN job_embeddings je ON true JOIN jobs j ON je.job_id = j.id " +
                    "WHERE re.resume_id = :resumeId " +
                    "ORDER BY re.vector <=> je.vector LIMIT :limit",
            nativeQuery = true)
    List<Map<String,Object>> findTopJobsForResume(@Param("resumeId") UUID resumeId, @Param("limit") int limit);
}
