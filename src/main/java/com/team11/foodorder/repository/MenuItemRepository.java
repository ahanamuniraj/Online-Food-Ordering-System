package com.team11.foodorder.repository;
import com.team11.foodorder.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {}
