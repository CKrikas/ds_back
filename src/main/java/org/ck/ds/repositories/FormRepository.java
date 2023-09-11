package org.ck.ds.repositories;

import org.ck.ds.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Integer> {
    List<Form> findBySpouse1idOrSpouse2idOrLawyerPrimaryidOrLawyerSecondaryidOrNotaryid(int spouse1id, int spouse2id, int lawyerPrimaryid, int lawyerSecondaryid, int notaryid);
}
