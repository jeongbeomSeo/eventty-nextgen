package com.eventty.eventtynextgen.events.repository;

import com.eventty.eventtynextgen.events.entity.EventDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailsRepository extends JpaRepository<EventDetails, Long> {

}
