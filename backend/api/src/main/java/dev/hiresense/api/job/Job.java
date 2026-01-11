package dev.hiresense.api.job;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String company;
    private String title;
    private String location;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Column(name = "jd_text", columnDefinition = "text")
    private String jdText;

    private String seniority;
    @Column(name = "salary_min")
    private Integer salaryMin;
    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // getters/setters (or use Lombok)
    // ... generate via IDE
    // Constructors
    public Job() {}
    public Job(UUID userId, String company, String title, String location, String sourceUrl, String jdText, String seniority, Integer salaryMin, Integer salaryMax) {
        this.userId = userId;
        this.company = company;
        this.title = title;
        this.location = location;
        this.sourceUrl = sourceUrl;
        this.jdText = jdText;
        this.seniority = seniority;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getJdText() {
        return jdText;
    }

    public Object getId() {
        return id;
    }
    // getters/setters omitted for brevity


}
