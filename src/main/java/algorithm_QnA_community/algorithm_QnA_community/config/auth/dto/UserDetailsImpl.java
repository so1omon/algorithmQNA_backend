package algorithm_QnA_community.algorithm_QnA_community.config.auth.dto;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth.dto
 * fileNmae         : UserDetailsImpl
 * author           : janguni
 * date             : 2023-06-04
 * description      :   Authentication 인증 성공 후 만들어 지는 사용자 정보 객체
 *                      Member객체에 password가 없기 때문에 getPassword는 Null 반환으로 처리
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/06/04       janguni         최초 생성
 */
@Getter
@ToString
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String nickName;

    private String email;
    private Role role;

    @Builder(builderClassName = "createUserDetail", builderMethodName = "createUserDetail")
    public UserDetailsImpl(Member member){
        this.id = member.getId();
        this.nickName = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(role.value()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return nickName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}
