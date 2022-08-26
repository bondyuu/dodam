package com.team1.dodam.service;

import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> member = userRepository.findByUsername(username);
    return member
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException(String.valueOf(ErrorCode.USER_NOT_FOUND)));
  }
}
