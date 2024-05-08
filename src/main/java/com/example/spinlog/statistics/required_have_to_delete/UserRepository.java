package com.example.spinlog.statistics.required_have_to_delete;

import com.example.spinlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
