package de.digitalcollections.commons.springsecurity.service;

import de.digitalcollections.commons.springsecurity.test.SpringConfigTest;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = { "security.userproperties.location=classpath:/users.properties"})
@ContextConfiguration(classes = {SpringConfigTest.class})
class PlainUsernamePasswordUserDetailsServiceTest {

  @Autowired
  PlainUsernamePasswordUserDetailsService service;

  @Test
  void testUnknownUsernameThrowsUsernameNotFoundException() {
    Executable testMethod = () -> service.loadUserByUsername("unknown");

    assertThrows(UsernameNotFoundException.class, testMethod);
  }

  @Test
  public void testUserWithOneRole() {
    UserDetails user1 = service.loadUserByUsername("user1");
    assertThat(user1.getUsername()).isEqualTo("user1");
    assertThat(user1.getPassword()).isEqualTo("{noop}password1");
    assertThat(user1.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_TEST1");
  }

  @Test
  public void testUserWithMultipleRoles() {
    UserDetails user2 = service.loadUserByUsername("user2");
    assertThat(user2.getUsername()).isEqualTo("user2");
    assertThat(user2.getPassword()).isEqualTo("{noop}password2");
    assertThat(user2.getAuthorities().stream().map(obj -> obj.getAuthority()).collect(Collectors.joining(","))).isEqualTo("ROLE_TEST1,ROLE_TEST2");
  }
}