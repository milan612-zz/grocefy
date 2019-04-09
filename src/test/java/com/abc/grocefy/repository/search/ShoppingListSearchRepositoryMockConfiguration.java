package com.abc.grocefy.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of ShoppingListSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ShoppingListSearchRepositoryMockConfiguration {

    @MockBean
    private ShoppingListSearchRepository mockShoppingListSearchRepository;

}
