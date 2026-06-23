package org.example.specification;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.Report;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    // =========================
    // SEARCH TEXT (SAFE)
    // =========================
    public static Specification<Report> containsText(String q) {
        return (root, query, cb) -> {

            if (q == null || q.isBlank()) {
                return cb.conjunction(); // ✅ IMPORTANT (au lieu de null)
            }

            String like = "%" + q.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    // =========================
    // CATEGORY FILTER (SAFE)
    // =========================
    public static Specification<Report> hasCategory(Category category) {
        return (root, query, cb) -> {

            if (category == null) {
                return cb.conjunction(); // ✅ IMPORTANT
            }

            return cb.equal(root.get("category"), category);
        };
    }

    // =========================
    // GRAVITY FILTER (SAFE)
    // =========================
    public static Specification<Report> hasGravity(Gravity gravity) {
        return (root, query, cb) -> {

            if (gravity == null) {
                return cb.conjunction(); // ✅ IMPORTANT
            }

            return cb.equal(root.get("gravity"), gravity);
        };
    }
}