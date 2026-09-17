package io.github.roony11_1.temp_monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.jwt.secret=testsecretkeytestsecretkeytestsecretkey1234567890"
})
class ContextLoadTest {

    @Autowired
    ApplicationContext ctx;

    @Test
    void contextLoads() {
        assertThat(ctx).isNotNull();
        assertThat(ctx.containsBean("filterParserAdapter")).isTrue();
        assertThat(ctx.containsBean("defaultRoleAssignmentPolicy")).isTrue();
        assertThat(ctx.containsBean("scopeStrategyFactory")).isTrue();
        assertThat(ctx.containsBean("cascadeStateService")).isTrue();
        assertThat(ctx.containsBean("compactionService")).isTrue();
    }
}
