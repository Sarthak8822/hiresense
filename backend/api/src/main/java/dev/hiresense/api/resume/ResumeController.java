package dev.hiresense.api.resume;

import dev.hiresense.api.security.JwtService;
import dev.hiresense.api.user.UserRepo;
import dev.hiresense.api.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.presigner.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/resumes")
public class ResumeController {

    private final S3Presigner presigner;
    private final ResumeRepo repo;
    private final JwtService jwt;
    private final UserRepo userRepo;

    private final ResumeService service;

    private final String bucket = "hiresense";

    public ResumeController(S3Presigner presigner, ResumeRepo repo, JwtService jwt, UserRepo userRepo, ResumeService service) {
        this.presigner = presigner;
        this.repo = repo;
        this.jwt = jwt;
        this.userRepo = userRepo;
        this.service = service;
    }


    @PostMapping("/presign")
    public ResponseEntity<?> presign(@RequestHeader("Authorization") String auth) {
        String email = jwt.parseSubject(auth.substring(7));

        String key = "hiresense/" + UUID.randomUUID() + ".pdf";

        var request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(p -> p.bucket(bucket).key(key))
                .build();

        var presigned = presigner.presignPutObject(request);

        return ResponseEntity.ok(Map.of(
                "url", presigned.url().toString(),
                "key", key
        ));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> body
    ) {
        String email = jwt.parseSubject(auth.substring(7));

        String title = body.get("title");
        String key = body.get("key");

        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        var resume = repo.save(new Resume(
                u.getId(),
                title,
                key
        ));

        return ResponseEntity.ok(Map.of(
                "resumeId", resume.getId(),
                "status", "PENDING"
        ));
    }

    @GetMapping
    public ResponseEntity<?> getResumeIds(
            @RequestHeader("Authorization") String auth
    ) {
        String email = jwt.parseSubject(auth.substring(7));

        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        var resumeIds = repo.findAllResumeByUserId(user.getId())
                .stream()
                .map(Resume::getId)
                .toList();

        return ResponseEntity.ok(resumeIds);
    }


    @PostMapping("/{id}/parse-now")
    public ResponseEntity<?> parseNow(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID id
    ) {
//        String email = jwt.parseSubject(auth.substring(7));
//
//        var user = userRepo.findByEmail(email).orElseThrow();
//        var resume = repo.findById(id).orElseThrow();
//
//        if (!resume.getUserId().equals(user.getId())) {
//            return ResponseEntity.status(403).body("Not your resume");
//        }
//
//        // ---- download file and parse ----
//        String extracted = service.downloadText(bucket, resume.getFileUrl());
//
//        resume.setParseStatus("DONE");
//        resume.setExtractedText(extracted);
//        repo.save(resume);
//
//        return ResponseEntity.ok(Map.of(
//                "resumeId", resume.getId(),
//                "parseStatus", "DONE",
//                "preview", extracted.substring(0, Math.min(200, extracted.length()))
//        ));

        String email = jwt.parseSubject(auth.substring(7));

        var user = userRepo.findByEmail(email).orElseThrow();
        var resume = repo.findById(id).orElseThrow();

        
        try {
            System.out.println("Enter Parser Controller");
            var res = service.parseNow(id);

            System.out.println(res);
            return ResponseEntity.ok(res);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }


    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<?> matches(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID id
    ) {
        String email = jwt.parseSubject(auth.substring(7));

        var user = userRepo.findByEmail(email).orElseThrow();
        var resume = repo.findById(id).orElseThrow();

        if (!resume.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Not your resume");
        }

        // TODO: Replace with real vector search when JD entries exist
        return ResponseEntity.ok(Map.of(
                "resumeId", resume.getId(),
                "matches", new String[]{"JD matching will come after JD upload"},
                "score", 0.75
        ));
    }



}
