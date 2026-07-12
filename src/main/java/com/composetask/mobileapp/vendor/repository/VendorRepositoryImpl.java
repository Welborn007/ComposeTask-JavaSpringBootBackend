package com.composetask.mobileapp.vendor.repository;

import com.composetask.mobileapp.vendor.model.Vendor;
import com.composetask.mobileapp.review.model.Review; // adjust if package differs
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VendorRepositoryImpl implements VendorRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Vendor> searchVendorsCustom(String search, String category, String location,
                                            Boolean verified, Double minRating, Double minTrustScore,
                                            Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Main select
        CriteriaQuery<Vendor> cq = cb.createQuery(Vendor.class);
        Root<Vendor> v = cq.from(Vendor.class);

        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                cb.like(cb.lower(v.get("businessName")), like),
                cb.like(cb.lower(v.get("description")), like),
                cb.like(cb.lower(v.get("category")), like),
                cb.like(cb.lower(v.get("location")), like)
            ));
        }

        if (category != null && !category.isBlank()) {
            predicates.add(cb.equal(cb.lower(v.get("category")), category.toLowerCase()));
        }

        if (location != null && !location.isBlank()) {
            predicates.add(cb.like(cb.lower(v.get("location")), "%" + location.toLowerCase() + "%"));
        }

        if (verified != null) {
            predicates.add(cb.equal(v.get("verified"), verified));
        }

        // correlated subquery to compute avgRating
        Subquery<Double> avgSub = cq.subquery(Double.class);
        Root<Review> r = avgSub.from(Review.class);
        avgSub.select(cb.coalesce(cb.avg(r.get("rating")), 0.0));
        avgSub.where(cb.equal(r.get("vendor"), v)); // correlated

        if (minRating != null) {
            predicates.add(cb.ge(avgSub, minRating));
        }

        // --- minTrustScore filter using numeric Expressions
        if (minTrustScore != null) {
            // normalizedRatingPercent: ((avgSub / 5.0) * 100.0 * 0.6)
            Expression<Number> avgDivFive = cb.quot(avgSub, cb.literal(5.0));
            Expression<Number> avgTimes100 = cb.prod(avgDivFive, cb.literal(100.0));
            Expression<Number> normalizedRatingPercent = cb.prod(avgTimes100, cb.literal(0.6));

            // verifiedScore: (CASE WHEN v.verified = true THEN 100.0 ELSE 50.0 END) * 0.4
            Expression<Number> verifiedCase = cb.<Number>selectCase()
                    .when(cb.isTrue(v.get("verified")), cb.literal(100.0))
                    .otherwise(cb.literal(50.0));
            Expression<Number> verifiedScore = cb.prod(verifiedCase, cb.literal(0.4));

            // trustExpr = normalizedRatingPercent + verifiedScore
            Expression<Number> trustExpr = cb.sum(normalizedRatingPercent, verifiedScore);

            // compare trustExpr >= minTrustScore
            predicates.add(cb.ge(trustExpr, cb.literal(minTrustScore)));
        }

        cq.where(predicates.toArray(new Predicate[0]));

// Sorting: handle trustScore specially if requested
        List<Order> orders = new ArrayList<>();
        for (Sort.Order s : pageable.getSort()) {
            if ("trustScore".equalsIgnoreCase(s.getProperty())) {
                // Recreate numeric trust expression for ORDER BY (same expressions)
                Expression<Number> avgDivFive = cb.quot(avgSub, cb.literal(5.0));
                Expression<Number> avgTimes100 = cb.prod(avgDivFive, cb.literal(100.0));
                Expression<Number> normalizedRatingPercent = cb.prod(avgTimes100, cb.literal(0.6));

                Expression<Number> verifiedCase = cb.<Number>selectCase()
                        .when(cb.isTrue(v.get("verified")), cb.literal(100.0))
                        .otherwise(cb.literal(50.0));
                Expression<Number> verifiedScore = cb.prod(verifiedCase, cb.literal(0.4));

                Expression<Number> trustExpr = cb.sum(normalizedRatingPercent, verifiedScore);

                orders.add(s.isAscending() ? cb.asc(trustExpr) : cb.desc(trustExpr));
            } else {
                // ensure property exists on Vendor; this will throw if unknown property is requested
                orders.add(s.isAscending() ? cb.asc(v.get(s.getProperty())) : cb.desc(v.get(s.getProperty())));
            }
        }
        if (!orders.isEmpty()) {
            cq.orderBy(orders);
        }

        TypedQuery<Vendor> query = em.createQuery(cq.select(v));
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        List<Vendor> content = query.getResultList();

        // Count query: rebuild same predicates for accurate total count
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Vendor> vCount = countCq.from(Vendor.class);

        // Rebuild correlated subquery for count query
        Subquery<Double> avgSubCount = countCq.subquery(Double.class);
        Root<Review> rCount = avgSubCount.from(Review.class);
        avgSubCount.select(cb.coalesce(cb.avg(rCount.get("rating")), 0.0));
        avgSubCount.where(cb.equal(rCount.get("vendor"), vCount)); // correlated to vCount

        List<Predicate> countPreds = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            countPreds.add(cb.or(
                cb.like(cb.lower(vCount.get("businessName")), like),
                cb.like(cb.lower(vCount.get("description")), like),
                cb.like(cb.lower(vCount.get("category")), like),
                cb.like(cb.lower(vCount.get("location")), like)
            ));
        }

        if (category != null && !category.isBlank()) {
            countPreds.add(cb.equal(cb.lower(vCount.get("category")), category.toLowerCase()));
        }

        if (location != null && !location.isBlank()) {
            countPreds.add(cb.like(cb.lower(vCount.get("location")), "%" + location.toLowerCase() + "%"));
        }

        if (verified != null) {
            countPreds.add(cb.equal(vCount.get("verified"), verified));
        }

        if (minRating != null) {
            countPreds.add(cb.ge(avgSubCount, minRating));
        }

        if (minTrustScore != null) {
            Expression<Number> avgDivFiveCount = cb.quot(avgSubCount, cb.literal(5.0));
            Expression<Number> avgTimes100Count = cb.prod(avgDivFiveCount, cb.literal(100.0));
            Expression<Number> normalizedRatingPercentCount = cb.prod(avgTimes100Count, cb.literal(0.6));

            Expression<Number> verifiedCaseCount = cb.<Number>selectCase()
                    .when(cb.isTrue(vCount.get("verified")), cb.literal(100.0))
                    .otherwise(cb.literal(50.0));
            Expression<Number> verifiedScoreCount = cb.prod(verifiedCaseCount, cb.literal(0.4));

            Expression<Number> trustExprCount = cb.sum(normalizedRatingPercentCount, verifiedScoreCount);
            countPreds.add(cb.ge(trustExprCount, cb.literal(minTrustScore)));
        }

        countCq.select(cb.countDistinct(vCount)).where(countPreds.toArray(new Predicate[0]));
        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}