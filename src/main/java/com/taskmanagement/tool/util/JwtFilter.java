package com.taskmanagement.tool.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.tool.dto.UserDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
  

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = fetchUserDetails(username,token,request);
           
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
    
        filterChain.doFilter(request, response);
    }
    
    public UserDetails fetchUserDetails(String username, String token, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
        String url = "http://localhost:8080/api/v1/auth/users/username/" + username;

       
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

       
        HttpEntity<String> entity = new HttpEntity<>(headers);

        
//        ResponseEntity<UserDTO> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                UserDTO.class
//        );
//        
//     System.out.println(response.getBody());

//        UserDTO userDTO = response.getBody();
        
        ResponseEntity<String> response = restTemplate.exchange(
        	    url,
        	    HttpMethod.GET,
        	    entity,
        	    String.class
        	);
        	System.out.println("Response: " + response.getBody());

        	ObjectMapper mapper = new ObjectMapper();
        	UserDTO userDTO = mapper.readValue(response.getBody(), UserDTO.class);

        if (userDTO == null) {
            throw new RuntimeException("User not found!");
        }
    
        HttpSession session = request.getSession();
        session.setAttribute("userid",userDTO.getId().toString());
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
           
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userDTO.getRoles()));
            
        }

       
        return new User(
                userDTO.getUsername(),
                userDTO.getPassword(),
                authorities
        );
}
}
