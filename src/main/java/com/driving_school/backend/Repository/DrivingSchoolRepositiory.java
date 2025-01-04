package com.driving_school.backend.Repository;


import com.driving_school.backend.Entity.DrivingSchoolsTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrivingSchoolRepositiory extends JpaRepository<DrivingSchoolsTable,Long> {

    @Query("SELECT L FROM DrivingSchoolsTable L WHERE L.school_name=:school_name")
    public DrivingSchoolsTable findByschool_name(@Param("school_name")String school_name);

    @Query("SELECT s FROM DrivingSchoolsTable s WHERE LOWER(s.city) = LOWER(:city)")
    public List<DrivingSchoolsTable> findBycity(@Param("city")String city);

    boolean existsByCityIgnoreCase(String city);


    @Query("SELECT DISTINCT d.city FROM DrivingSchoolsTable d")
    List<String> findDistinctCity();
}
