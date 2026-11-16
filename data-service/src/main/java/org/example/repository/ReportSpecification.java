
package org.example.repository;

import org.example.model.Category;
import org.example.model.Gravity;
import org.example.model.Report;
import org.springframework.data.jpa.domain.Specification;

public class ReportSpecification {

    public static Specification<Report> withFilters(
            Category category,
            Gravity gravity,
            String q) {

        return (root, query, criteriaBuilder) -> {

            Specification<Report> specification =
                    (r, cq, cb) -> cb.conjunction();

            if (category != null) {
                specification = specification.and(
                        (r, cq, cb) ->
                                cb.equal(r.get("category"), category)
                );
            }

            if (gravity != null) {
                specification = specification.and(
                        (r, cq, cb) ->
                                cb.equal(r.get("gravity"), gravity)
                );
            }

            if (q != null && !q.trim().isEmpty()) {

                String search = "%" + q.trim().toLowerCase() + "%";

                specification = specification.and(
                        (r, cq, cb) ->
                                cb.or(
                                        cb.like(
                                                cb.lower(r.get("title")),
                                                search
                                        ),
                                        cb.like(
                                                cb.lower(r.get("description")),
                                                search
                                        ),
                                        cb.like(
                                                cb.lower(r.get("location")),
                                                search
                                        )
                                )
                );
            }

            return specification.toPredicate(
                    root,
                    query,
                    criteriaBuilder
            );
        };
    }
}
