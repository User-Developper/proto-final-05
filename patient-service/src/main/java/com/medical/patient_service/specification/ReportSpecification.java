package com.medical.patient_service.specification;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;
import com.medical.patient_service.model.Report;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    public static Specification<Report> containsText(String q) {
        return (root, query, cb) -> {

            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + q.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Report> hasCategory(Category category) {
        return (root, query, cb) -> {

            if (category == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Report> hasGravity(Gravity gravity) {
        return (root, query, cb) -> {

            if (gravity == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("gravity"), gravity);
        };
    }
}