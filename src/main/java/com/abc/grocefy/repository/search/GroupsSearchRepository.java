package com.abc.grocefy.repository.search;

import com.abc.grocefy.domain.Groups;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Groups entity.
 */
public interface GroupsSearchRepository extends ElasticsearchRepository<Groups, Long> {
}
