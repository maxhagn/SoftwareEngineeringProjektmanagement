package at.ac.tuwien.sepm.groupphase.backend.security;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationAttemptException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecurityProperties securityProperties, JwtTokenizer jwtTokenizer, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
        this.userService = userService;
        setFilterProcessesUrl(securityProperties.getLoginUri());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginDto user = new ObjectMapper().readValue(request.getInputStream(), UserLoginDto.class);

            // Checks if to user is allowed to progress with signin
            at.ac.tuwien.sepm.groupphase.backend.entity.User persistedUser = userService.findUserByEmail(user.getEmail());
            if (persistedUser.getSignInAttempts() >= 5) {
                throw new BadCredentialsException("This user is locked. Pleas contact a admin.");
            }

            //Compares the user with CustomUserDetailService#loadUserByUsername and check if the credentials are correct
            try {
                return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            } catch (AuthenticationException e) {
                throw new AuthenticationAttemptException(user, "Bad credentials", e);
            }
        } catch (IOException e) {
            throw new BadCredentialsException("Wrong API request or JSON schema", e);
        } catch (NotFoundException e) {
            throw new BadCredentialsException("Bad credentials", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // Increase number of failed sign in attempts
        if(failed instanceof AuthenticationAttemptException) {
            AuthenticationAttemptException attemptException = (AuthenticationAttemptException) failed;
            try {
                userService.increaseUserSignInAttemptsByEmail(attemptException.getUserData().getEmail());
            } catch (NotFoundException e) {
                LOGGER.error("Could not increase user sign in attempts {}", e.getMessage());
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(failed.getMessage());
        LOGGER.debug("Invalid authentication attempt: {}", failed.getMessage());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = ((User) authResult.getPrincipal());

        try {
            userService.resetUserSignInAttemptsByEmail(user.getUsername());
        } catch (NotFoundException e) {
            LOGGER.error("Could not reset user sign in attempts {}", e.getMessage());
        }

        List<String> roles = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        response.getWriter().write(jwtTokenizer.getAuthToken(user.getUsername(), roles));
        LOGGER.info("Successfully authenticated user {}", user.getUsername());
    }
}
