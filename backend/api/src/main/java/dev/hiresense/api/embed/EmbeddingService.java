package dev.hiresense.api.embed;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MVP: produce 1536-d vector as comma-separated string.
 * Replace with real model (OpenAI/Local) later.
 */
@Service
public class EmbeddingService {

    public String embedText(String text){
        // deterministic-ish pseudo-embedding: hash blocks -> float values
        int dim = 1536;
        double base = Math.abs(text.hashCode()) % 1000;
        List<Double> vec = IntStream.range(0, dim)
                .mapToDouble(i -> ((base + i * 13.37) % 1.0) )
                .boxed().collect(Collectors.toList());
        return vec.stream().map(d -> String.format(Locale.ROOT, "%.6f", d)).collect(Collectors.joining(","));
    }
}
