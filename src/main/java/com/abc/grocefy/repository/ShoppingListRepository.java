package com.abc.grocefy.repository;

import java.util.List;
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
        + "?#{principal.username} OR shopping_list.shopper.login = "
        + "?#{principal.username}",
        countQuery = "select count(shopping_list) from ShoppingList shopping_list")
    Page<ShoppingList> findByCurrentUser(Pageable pageable);



}
