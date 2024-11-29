package com.delani.shelfShare.auth;

import com.delani.shelfShare.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<User, Integer> {
}
