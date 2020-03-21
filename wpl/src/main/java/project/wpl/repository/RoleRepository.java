package project.wpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.wpl.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {

}
