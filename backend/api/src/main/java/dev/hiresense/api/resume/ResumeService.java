package dev.hiresense.api.resume;

import dev.hiresense.api.parse.ParserServiceResume;
import dev.hiresense.api.embed.EmbeddingService;
import dev.hiresense.api.skillgazetteer.SkillGazetteer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ResumeService {
    private final ResumeRepo resumeRepo;
    private final ResumeEmbeddingRepo embeddingRepo;
    private final ParserServiceResume parser;
    private final EmbeddingService embedSvc;

    @PersistenceContext
    private EntityManager em;

    public ResumeService(ResumeRepo resumeRepo, ResumeEmbeddingRepo embeddingRepo,
                         ParserServiceResume parser, EmbeddingService embedSvc) {
        this.resumeRepo = resumeRepo; this.embeddingRepo = embeddingRepo;
        this.parser = parser; this.embedSvc = embedSvc;
    }

    public Resume createResume(UUID userId, String title, String key) {
        // key is S3/MinIO path → we'll store as fileUrl
        var r = new Resume(userId, title, key);
        return resumeRepo.save(r);
    }

    @Transactional
    public Map<String,Object> parseNow(UUID resumeId) throws Exception {
        var r = resumeRepo.findById(resumeId).orElseThrow(() -> new RuntimeException("resume not found"));
        r.setParseStatus("PARSING");

        String text = parser.extractText(r.getFileUrl());

        // tiny gazetteer
//        List<String> gaz = Arrays.asList("Java","Spring","React","Next.js","AWS","Docker","Kubernetes","Python","SQL");

        List<String> gaz = SkillGazetteer.flatList();
//        List<String> skills = parser.extractSkills(text, gaz);


        List<String> skills = parser.extractSkills(text, gaz);

        System.out.println("skills");
        System.out.println(skills.toString());




        String vector = embedSvc.embedText(text);

        // Save embedding into job-resume_embeddings table via native insert to properly write vector column
        // Example INSERT using pgvector: vector = '[]'::vector not supported by JPA; we'll do native:
        em.createNativeQuery("INSERT INTO resume_embeddings(resume_id, model, vector) VALUES (?1, ?2, (?3)::vector)")
                .setParameter(1, resumeId)
                .setParameter(2, "mvp-embed-v1")
                .setParameter(3, "[" + vector + "]") // depends on pgvector input format; adjust if necessary
                .executeUpdate();

        r.setParseStatus("READY");
        resumeRepo.save(r);

        Map<String,Object> res = new HashMap<>();
        res.put("resumeId", resumeId);
        res.put("skills", skills);
        res.put("status", "READY");
        return res;
    }
}
