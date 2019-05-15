package com.abc.grocefy.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.abc.grocefy.domain.ShoppingList;
import com.abc.grocefy.domain.User;
import com.abc.grocefy.domain.enumeration.State;


/**
 * Spring Data  repository for the ShoppingList entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    Page<ShoppingList> findByOwnerOrShopper(User owner, User shopper, Pageable pageable);

    Page<ShoppingList> findByOwner(User owner, Pageable pageable);

    Page<ShoppingList> findByShopper(User shopper, Pageable pageable);

    Page<ShoppingList> findByOwnerAndState(User shopper, State state, Pageable pageable);

    Page<ShoppingList> findByShopperAndState(User shopper, State state, Pageable pageable);

    @Query(value = "SELECT DISTINCT * FROM SHOPPING_LIST WHERE \n"
        + "OWNER_ID IN (SELECT DISTINCT USER_ID FROM GROUPS_USER WHERE GROUPS_ID IN (SELECT GROUPS_ID FROM "
        + "GROUPS_USER WHERE USER_ID = ?1))",
        nativeQuery = true)
    Page<ShoppingList> findByOwnerInGroups(Long id, Pageable pageable);

}
