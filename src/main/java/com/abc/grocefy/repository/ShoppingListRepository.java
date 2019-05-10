package com.abc.grocefy.repository;

import java.util.List;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.abc.grocefy.domain.ShoppingList;

/**
 * Spring Data  repository for the ShoppingList entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    @Query("select shopping_list from ShoppingList shopping_list where shopping_list.owner.login = ?#{principal.username}")
    List<ShoppingList> findByOwnerIsCurrentUser();

    @Query(value = "select shopping_list from ShoppingList shopping_list where shopping_list.owner.login = "
        + "?#{principal.username}",
        countQuery = "select count(shopping_list) from ShoppingList shopping_list")
    Page<ShoppingList> findByOwnerIsCurrentUser(Pageable pageable);

    @Query("select shopping_list from ShoppingList shopping_list where shopping_list.shopper.login = ?#{principal.username}")
    List<ShoppingList> findByShopperIsCurrentUser();

    @Query(value = "select shopping_list from ShoppingList shopping_list where shopping_list.owner.login = "
        + "?#{principal.username} AND shopping_list.shopper.login = "
        + "?#{principal.username}",
        countQuery = "select count(shopping_list) from ShoppingList shopping_list")
    Page<ShoppingList> findByCurrentUser(Pageable pageable);

//    @Query("update ShoppingList shopping_list set shopping_list.shopper = ?#{principal} where shopping_list.id =:id "
//        + "AND shopping_list.shopper is null")
//    int updateShopper(@Param("id") Long id);
//
//    @Query("update ShoppingList shopping_list set shopping_list.shopper = null where shopping_list.id =:id ")
//    int removeShopper(@Param("id") Long id);
}
