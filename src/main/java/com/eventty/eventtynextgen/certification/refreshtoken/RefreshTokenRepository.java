package com.eventty.eventtynextgen.certification.refreshtoken;

import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
