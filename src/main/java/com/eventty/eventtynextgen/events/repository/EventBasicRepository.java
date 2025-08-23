package com.eventty.eventtynextgen.events.repository;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventBasicRepository extends JpaRepository<EventBasic, Long> {

}
