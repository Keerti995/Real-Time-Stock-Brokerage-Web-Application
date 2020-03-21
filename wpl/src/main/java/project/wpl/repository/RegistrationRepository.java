package project.wpl.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.wpl.model.UserRegistry;

@Repository
public interface RegistrationRepository extends CrudRepository<UserRegistry,String> {
    UserRegistry findByUsername(String username);
}
