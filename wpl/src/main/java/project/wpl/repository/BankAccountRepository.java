package project.wpl.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import project.wpl.model.BankAccount;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Integer>{


}
