package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;
}
