package com.example.security.service;

import com.example.security.ErrorResponse.ErrorResponse;
import com.example.security.exception.OAuthServiceException;
import com.example.security.models.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserImplService implements UserService, UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(UserImplService.class);
    private static String DOMAIN = "http://userMicroService";
    private static String GET_USER_BY_NAME = "/api/users/getUserByName/";


    @Override
    public UserDto getUserByName(String name){
            Map<String,String> pathVariables = new HashMap<String,String>();
            pathVariables.put("name", name);
            UserDto user = restTemplate.getForObject("http://localhost:8001/api/users/getUserByName/{name}", UserDto.class, pathVariables);
            System.out.println(user);
            return user;

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserDto user = getUserByName(username);
            if(user == null) {
                logger.error("No existe el usuario " + username);
                throw new UsernameNotFoundException("No existe el usuario");
            }
            List<GrantedAuthority> authorities = user.getRoles()
                    .stream()
                    .map(rol -> new SimpleGrantedAuthority(rol.getName()))
                    .collect(Collectors.toList());
            System.out.println(user);
            System.out.println(authorities);
            return new User(user.getName(), user.getPassword(), user.getActive(), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, authorities);
        } catch (Throwable t) {
            logger.error("[Error " + t.getClass() + "] " + t.getMessage());
            throw t;
        }
    }



    private List<HttpMessageConverter<?>> getJsonMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        return converters;
    }
}
