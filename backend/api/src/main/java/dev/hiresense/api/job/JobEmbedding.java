package dev.hiresense.api.job;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="job_embeddings")
public class JobEmbedding {
    @Id
    @Column(name="job_id")
    private UUID jobId;
    private String model;
    @Column(name = "vector", columnDefinition = "vector")
    private String vector;
    public JobEmbedding(){}
    public JobEmbedding(UUID jobId, String model, String vector){ this.jobId = jobId; this.model = model; this.vector = vector;}
    // getters
}
