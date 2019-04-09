package com.abc.grocefy.repository;

import com.abc.grocefy.domain.ShoppingList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ShoppingList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

}
