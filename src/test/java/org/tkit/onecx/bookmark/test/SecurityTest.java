package org.tkit.onecx.bookmark.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/bookmarks/search", 400, List.of("ocx-bm:read"), "post");
        config.addConfig("delete", "/internal/bookmarks/id", 204, List.of("ocx-bm:delete"), "delete");
        return config;
    }
}
