package dev.hiresense.api.job;

import dev.hiresense.api.resume.Resume;
import dev.hiresense.api.security.JwtService;
import dev.hiresense.api.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;
    private final JobRepo jobRepo;
    private final JwtService jwt;
    private final UserRepo userRepo;
    private final JdbcTemplate jdbc;

    public JobController(JobService jobService, JobRepo jobRepo, JwtService jwt, UserRepo userRepo, JdbcTemplate jdbc) {
        this.jobService = jobService;
        this.jobRepo = jobRepo;
        this.jwt = jwt;
        this.userRepo = userRepo;
        this.jdbc = jdbc;
    }

    // Create job + index embedding synchronously (MVP)
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String auth, @RequestBody Map<String, Object> body) {
        String email = jwt.parseSubject(auth.substring(7));
        var user = userRepo.findByEmail(email).orElseThrow();


        var job = jobRepo.save(new Job(
                user.getId(),
                (String) body.getOrDefault("company", null),
                (String) body.getOrDefault("title", null),
                (String) body.getOrDefault("location", null),
                (String) body.getOrDefault("sourceUrl", null),
                (String) body.getOrDefault("jdText", null),
                (String) body.getOrDefault("seniority", null),
                body.get("salaryMin") == null ? null : ((Number) body.get("salaryMin")).intValue(),
                body.get("salaryMax") == null ? null : ((Number) body.get("salaryMax")).intValue()
        ));

        Job saved = jobService.createAndIndex(job);

        return ResponseEntity.ok(Map.of("jobId", saved.getId(), "status", "INDEXED"));
    }

    // Re-parse & re-index immediately
    @PostMapping("/{id}/parse-now")
    public ResponseEntity<?> parseNow(@RequestHeader("Authorization") String auth, @PathVariable("id") UUID id) {
        // optional: permission check that user owns this job
        jobService.reindex(id);

        // run simple skill extraction to return as response
        Job job = jobRepo.findById(id).orElseThrow();
        // reuse parser service via jobService (could expose)
        Map<String,Object> res = Map.of("jobId", id, "status", "REINDEXED");
        return ResponseEntity.ok(res);
    }

    // Find top matching resumes for this job using pgvector nearest-neighbor
    @GetMapping("/{id}/matches")
    public ResponseEntity<?> matches(@RequestHeader("Authorization") String auth,
                                     @PathVariable("id") UUID id,
                                     @RequestParam(name="topK", defaultValue = "10") int topK) {
        // This SQL returns resumes ordered by cosine similarity (using <=> operator)


        String sql = """
            SELECT re.resume_id::text AS resume_id, 1 - (re.vector <=> je.vector) AS score
            FROM resume_embeddings re, job_embeddings je
            WHERE je.job_id = ?
            ORDER BY re.vector <=> je.vector
            LIMIT ?ss
            """;

        System.out.println("SQL Output:");
        System.out.println(sql);

        List<Map<String,Object>> rows = jdbc.queryForList(sql, id, topK);
        return ResponseEntity.ok(Map.of("jobId", id, "topK", topK, "matches", rows));
    }

    // Optional: GET /jobs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable("id") UUID id) {
        return jobRepo.findById(id)
                .map(j -> ResponseEntity.ok(j))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
