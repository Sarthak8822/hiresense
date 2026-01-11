package dev.hiresense.api.parse;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ParserServiceResume {

    private final Tika tika = new Tika();
    private final MinioClient minioClient;

    public ParserServiceResume(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String extractText(String objectPath) throws Exception {

        String bucket = "hiresense";
        String objectName = objectPath.replaceFirst("^hiresense/", "");
        try (InputStream in = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        )) {
            return tika.parseToString(in);
        }
    }

    public List<String> extractSkills(String text, List<String> skillGazetteer) {
        var found = new LinkedHashSet<String>();
        String lower = text.toLowerCase();
        for (String s : skillGazetteer) {
            if (lower.contains(s.toLowerCase())) found.add(s);
        }
        return new ArrayList<>(found);
    }
}
