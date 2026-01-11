package dev.hiresense.api.resume;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "resume_embeddings")
public class ResumeEmbedding {
    @Id
    @Column(name = "resume_id")
    private UUID resumeId;

    private String model;

    // store as byte[] or String if you convert; we will use SQL insert into vector column in service
    @Column(name = "vector", columnDefinition = "vector")
    private String vector; // store JSON/text representation for now; we'll use raw SQL when writing actual vector

    public ResumeEmbedding(){ }
    public ResumeEmbedding(UUID resumeId, String model, String vector){
        this.resumeId = resumeId; this.model = model; this.vector = vector;
    }
    // getters/setters
    public UUID getResumeId(){ return resumeId; }
    public String getModel(){ return model; }
    public String getVector(){ return vector; }
}
