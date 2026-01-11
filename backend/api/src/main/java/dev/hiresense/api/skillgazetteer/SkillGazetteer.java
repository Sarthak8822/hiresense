package dev.hiresense.api.skillgazetteer;

import java.util.*;

public final class SkillGazetteer {

    private SkillGazetteer() {}

    public static Map<String, List<String>> SKILLS = Map.of(
            "programming_languages", List.of(
                    "Java", "Python", "C", "C++", "C#", "JavaScript", "TypeScript",
                    "Go", "Rust", "Kotlin", "Swift", "PHP", "Ruby"
            ),

            "frameworks", List.of(
                    "Spring", "Spring Boot", "Hibernate",
                    "React", "Next.js", "Angular", "Vue",
                    "Node.js", "Express",
                    "Django", "Flask",
                    ".NET", "Laravel"
            ),

            "databases", List.of(
                    "MySQL", "PostgreSQL", "Oracle", "SQL Server",
                    "MongoDB", "Redis", "Cassandra", "DynamoDB",
                    "SQLite"
            ),

            "cloud_devops", List.of(
                    "AWS", "Azure", "GCP",
                    "Docker", "Kubernetes",
                    "Terraform", "Ansible",
                    "CI/CD", "GitHub Actions", "Jenkins"
            ),

            "data_ml", List.of(
                    "Machine Learning", "Deep Learning",
                    "NLP", "Computer Vision",
                    "Pandas", "NumPy", "Scikit-learn",
                    "TensorFlow", "PyTorch"
            ),

            "mobile", List.of(
                    "Android", "iOS",
                    "React Native", "Flutter"
            ),

            "tools", List.of(
                    "Git", "GitHub", "GitLab",
                    "JIRA", "Confluence",
                    "Postman", "Swagger",
                    "Linux", "Unix"
            ),

            "soft_skills", List.of(
                    "Communication", "Leadership",
                    "Problem Solving", "Teamwork",
                    "Time Management"
            )
    );

    /** Flattened list for simple extraction */
    public static List<String> flatList() {
        return SKILLS.values()
                .stream()
                .flatMap(Collection::stream)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }
}
