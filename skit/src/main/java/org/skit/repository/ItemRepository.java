package org.skit.repository;


import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaSpecificationRepository<Item, Long> {
    Page<Item> findByTypeAndHandoverIsFalse(Type type, Pageable pageable);

    List<Item> findByTypeAndCategoryAndHandoverIsFalse(Type type, Category category);
}
