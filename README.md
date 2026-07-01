# Basic Authentication in Spring Security (Without JWT)

## What is Basic Authentication?

Basic Authentication is one of the simplest authentication mechanisms supported by HTTP and Spring Security.

With Basic Authentication:

- The client sends the **username** and **password** with **every HTTP request**.
- Spring Security verifies the credentials on every request.
- No token (such as JWT) is generated or stored.
- If the credentials are valid, the request is allowed.
- If they are invalid, Spring Security returns **401 Unauthorized**.

Unlike JWT authentication, the server does **not** issue a token after login. Every request must include the username and password.

---

## Authentication Flow

```text
                Client
                   |
                   |  GET /api/v1/users
                   |  Authorization: Basic base64(username:password)
                   |
                   v
        Spring Security Filter Chain
                   |
                   v
     BasicAuthenticationFilter
                   |
                   v
     Decode Authorization Header
                   |
                   v
 username + password extracted
                   |
                   v
 AuthenticationManager
                   |
                   v
 DaoAuthenticationProvider
                   |
                   v
 CustomUserDetailsService
                   |
                   v
 Load User from Database
                   |
                   v
 PasswordEncoder.matches()
                   |
          ---------------------
          |                   |
      Password OK        Password Wrong
          |                   |
          v                   v
 Authentication      401 Unauthorized
 Successful
          |
          v
 Controller Executes
```

---

## Authorization Header

The client sends the credentials in the HTTP Authorization header.

```
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
```

The value after `Basic` is simply:

```
Base64(username:password)
```

Example:

```
username = john
password = secret123
```

Becomes:

```
john:secret123
```

Base64 encoded:

```
am9objpzZWNyZXQxMjM=
```

Header:

```
Authorization: Basic am9objpzZWNyZXQxMjM=
```

> **Note:** Base64 is **not encryption**. It is only encoding. Always use **HTTPS** with Basic Authentication.

---

## Spring Security Configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Public APIs
                .requestMatchers("/api/v1/auth/**").permitAll()

                // All other APIs require authentication
                .anyRequest().authenticated()
            )

            // Enable HTTP Basic Authentication
            .httpBasic(Customizer.withDefaults())

            // Disable CSRF for REST APIs
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
}
```

---

## Public APIs

Some endpoints can be accessed without authentication.

```java
.requestMatchers("/api/v1/auth/**").permitAll()
```

Examples:

```
POST /api/v1/auth/register

POST /api/v1/auth/login

GET /api/v1/auth/health
```

Anyone can call these APIs.

---

## Protected APIs

Every other endpoint requires authentication.

```java
.anyRequest().authenticated()
```

Examples:

```
GET /api/v1/users

POST /api/v1/orders

PUT /api/v1/profile

DELETE /api/v1/admin/users
```

These requests must include the Authorization header.

---

## Example Request

Without authentication:

```http
GET /api/v1/users
```

Response:

```
401 Unauthorized
```

With authentication:

```http
GET /api/v1/users

Authorization: Basic am9objpzZWNyZXQxMjM=
```

Response:

```
200 OK
```

---

## Authentication Process

For every request:

1. Client sends username and password.
2. Spring Security reads the Authorization header.
3. Credentials are decoded.
4. `AuthenticationManager` is called.
5. `DaoAuthenticationProvider` authenticates the user.
6. `CustomUserDetailsService` loads the user from the database.
7. Password is compared using `PasswordEncoder`.
8. If valid, the request proceeds.
9. If invalid, a **401 Unauthorized** response is returned.

Since there is no JWT or session involved, this process is repeated for **every request**.

---

## Difference Between Basic Authentication and JWT

| Basic Authentication | JWT Authentication |
|----------------------|-------------------|
| Username/password sent with every request | JWT token sent with every request |
| No login token generated | Login generates a JWT |
| Server verifies username/password every request | Server verifies JWT signature |
| Database is typically accessed on every request | Database access can often be avoided after login |
| Simpler to implement | More scalable for distributed systems |
| Requires HTTPS because credentials are repeatedly transmitted | Requires HTTPS because the token grants access |

---

## Advantages

- Very easy to configure.
- Built into HTTP.
- Supported by browsers and tools like Postman.
- No token management.
- Good for internal services or development.

---

## Disadvantages

- Username and password are transmitted with every request.
- Requires HTTPS to protect credentials.
- Less efficient because authentication is performed on every request.
- Not suitable for most public production APIs where token-based authentication (such as JWT or OAuth2) is preferred.

---

## Summary

Basic Authentication is a simple authentication mechanism where the client includes the username and password in the `Authorization` header on every request. Spring Security validates these credentials for each request using an `AuthenticationManager`, `DaoAuthenticationProvider`, `UserDetailsService`, and `PasswordEncoder`. Public endpoints can be excluded from authentication using `requestMatchers(...).permitAll()`, while protected endpoints require valid credentials via `.anyRequest().authenticated()`.
