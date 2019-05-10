package com.abc.grocefy.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.abc.grocefy.domain.Groups;
import org.javers.spring.annotation.JaversSpringDataAuditable;

/**
 * Spring Data  repository for the Groups entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface GroupsRepository extends JpaRepository<Groups, Long> {

    @Query(value = "select distinct groups from Groups groups left join fetch groups.users",
        countQuery = "select count(distinct groups) from Groups groups")
    Page<Groups> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct groups from Groups groups left join fetch groups.users")
    List<Groups> findAllWithEagerRelationships();

    @Query("select groups from Groups groups left join fetch groups.users where groups.id =:id")
    Optional<Groups> findOneWithEagerRelationships(@Param("id") Long id);

}
