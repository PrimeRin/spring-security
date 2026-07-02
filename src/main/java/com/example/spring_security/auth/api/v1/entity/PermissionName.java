// PermissionName.java
package com.example.spring_security.auth.api.v1.entity;

public enum PermissionName {
    // Book permissions
    BOOK_CREATE,
    BOOK_READ,
    BOOK_UPDATE,
    BOOK_DELETE,

    // User permissions
    USER_CREATE,
    USER_READ,
    USER_UPDATE,
    USER_DELETE,

    // Admin permissions
    ADMIN_ACCESS,
    SYSTEM_CONFIG
}
