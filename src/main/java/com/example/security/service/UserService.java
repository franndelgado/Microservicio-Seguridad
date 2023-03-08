package com.example.security.service;

import com.example.security.exception.OAuthServiceException;
import com.example.security.models.UserDto;

public interface UserService {

    UserDto getUserByName(String name) throws OAuthServiceException;
}
