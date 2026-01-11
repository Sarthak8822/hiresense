package dev.hiresense.api.job;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class JobService {
    private final JobRepo jobRepo;
    private final ParserService parser;
    private final VectorService vectorService;
    private final JdbcTemplate jdbc;

    public JobService(JobRepo jobRepo, ParserService parser, VectorService vectorService, JdbcTemplate jdbc) {
        this.jobRepo = jobRepo;
        this.parser = parser;
        this.vectorService = vectorService;
        this.jdbc = jdbc;
    }

    @Transactional
    public Job createAndIndex(Job job) {
        // Save job row
        Job saved = jobRepo.save(job);

        // Parse text
        String text = parser.extractText(job.getSourceUrl(), job.getJdText());

        // Create embedding
        float[] vec = vectorService.embed(text);

        // Persist into job_embeddings (pgvector)
        // We use PGobject with type 'vector' and value like '[0.1, 0.2, ...]'
        PGobject pg = new PGobject();
        try {
            pg.setType("vector");
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < vec.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(vec[i]);
            }
            sb.append(']');
            pg.setValue(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO job_embeddings (job_id, model, vector) VALUES (?, ?, ?)";
        jdbc.update(sql, ps -> {
            ps.setObject(1, saved.getId());
            ps.setString(2, vectorService.modelName());
            ps.setObject(3, pg);
        });

        return saved;
    }

    @Transactional
    public void reindex(UUID jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow();
        String text = parser.extractText(job.getSourceUrl(), job.getJdText());
        float[] vec = vectorService.embed(text);

        PGobject pg = new PGobject();
        try {
            pg.setType("vector");
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < vec.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(vec[i]);
            }
            sb.append(']');
            pg.setValue(sb.toString());
        } catch (Exception e) { throw new RuntimeException(e); }

        // upsert
        String upsert = "INSERT INTO job_embeddings (job_id, model, vector) VALUES (?, ?, ?) " +
                "ON CONFLICT (job_id) DO UPDATE SET model = EXCLUDED.model, vector = EXCLUDED.vector";
        jdbc.update(upsert, ps -> {
            ps.setObject(1, job.getId());
            ps.setString(2, vectorService.modelName());
            ps.setObject(3, pg);
        });
    }
}
