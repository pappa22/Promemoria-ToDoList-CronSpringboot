package it.dstech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.dstech.models.Activity;


@Repository
public interface ActivityRepository extends  JpaRepository<Activity, Long> {

}
