package com.jll.cibus.user.specification;

import com.jll.cibus.user.entity.UserEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class UserSpecification {

    private UserSpecification() {}  // para evitar que se instancie

    public static PredicateSpecification<UserEntity> nameContains(String name) {
        return (root, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }

    public static PredicateSpecification<UserEntity> dniEquals(Long dni) {
        return (root, cb) -> {
            if (dni == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("dni"), dni);
        };
    }

    public static PredicateSpecification<UserEntity> emailEquals(String email) {
        return (root, cb) -> {
            if (email == null || email.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("email")), email.toLowerCase());
        };
    }

    public static PredicateSpecification<UserEntity> phoneNumberEquals(String phoneNumber) {
        return (root, cb) -> {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("phoneNumber")), phoneNumber.toLowerCase());
        };
    }

    public static PredicateSpecification<UserEntity> branchIdEquals(Long branchId) {
        return (root, cb) -> {
            if (branchId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("branch").get("id"), branchId);
        };
    }

    public static PredicateSpecification<UserEntity> userRoleIdEquals(Long userRoleId) {
        return (root, cb) -> {
            if (userRoleId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("role").get("id"), userRoleId);
        };
    }

}
