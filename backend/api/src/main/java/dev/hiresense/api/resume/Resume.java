package dev.hiresense.api.resume;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(name="user_id", nullable=false)
    private UUID userId;

    private String title;
    @Column(name="file_url", nullable=false)
    private String fileUrl;

    @Column(name="parse_status")
    private String parseStatus = "PENDING";

    @Column(name="created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    public Resume() {}
    public Resume(UUID userId, String title, String fileUrl) {
        this.userId = userId; this.title = title; this.fileUrl = fileUrl;
    }
    // getters/setters
    public UUID getId(){ return id; }
    public UUID getUserId(){ return userId; }
    public String getTitle(){ return title; }
    public String getFileUrl(){ return fileUrl; }
    public String getParseStatus(){ return parseStatus; }
    public void setParseStatus(String s) { this.parseStatus = s; }
    public void setExtractedText(String text) { this.extractedText = text; }
}
