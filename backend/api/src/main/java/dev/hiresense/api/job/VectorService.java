package dev.hiresense.api.job;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Provide embedding vector for a text. Replace with real model call.
 */
@Service
public class VectorService {

    private static final int DIM = 1536; // match your pgvector size
    private final Random rnd = new Random();

    public float[] embed(String text) {
        // TODO: call an embedding model (OpenAI/Azure/Local) and return float[]
        float[] v = new float[DIM];
        for (int i = 0; i < DIM; i++) v[i] = rnd.nextFloat() - 0.5f; // placeholder
        return v;
    }

    public String modelName() { return "mvp-stub-1"; }
}
