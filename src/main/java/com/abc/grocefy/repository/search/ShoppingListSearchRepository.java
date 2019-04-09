package com.abc.grocefy.repository.search;

import com.abc.grocefy.domain.ShoppingList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ShoppingList entity.
 */
public interface ShoppingListSearchRepository extends ElasticsearchRepository<ShoppingList, Long> {
}
