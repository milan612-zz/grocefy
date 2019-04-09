package com.abc.grocefy.repository;

import com.abc.grocefy.domain.Groups;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Groups entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GroupsRepository extends JpaRepository<Groups, Long> {

    @Query("select groups from Groups groups where groups.user.login = ?#{principal.username}")
    List<Groups> findByUserIsCurrentUser();

}
