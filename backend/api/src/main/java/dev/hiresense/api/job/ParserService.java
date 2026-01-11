package dev.hiresense.api.job;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Responsible for turning a JD (text or source url) into:
 * - plain text (jdText)
 * - list of extracted skills (simple heuristics now)
 */
@Service
public class ParserService {

    // ❗ TODO: Replace with PDF/Tika parsing + LLM normalization
    public String extractText(String sourceUrl, String rawText) {
        if (rawText != null && !rawText.isBlank()) return rawText;
        // naive: return sourceUrl as text if no body provided (placeholder)
        return "Parsed text placeholder for: " + sourceUrl;
    }

    // very small heuristic skill extractor; replace with better pipeline
    public List<String> extractSkills(String text) {
        // TODO: use gazetteer + fuzzy matching; for now, pick some tokens
        return List.of(); // empty list for MVP; fill later
    }
}
