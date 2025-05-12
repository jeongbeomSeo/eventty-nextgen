package com.eventty.eventtynextgen.certification.core.autority;

import com.eventty.eventtynextgen.certification.core.GrantAuthority;
import org.springframework.util.Assert;

public class SimpleGrantedAuthority implements GrantAuthority {

    private final String role;

    private SimpleGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    public static SimpleGrantedAuthority of(String role) {
        return new SimpleGrantedAuthority(role);
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimpleGrantedAuthority sga) {
            return this.role.equals(sga.getAuthority());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
