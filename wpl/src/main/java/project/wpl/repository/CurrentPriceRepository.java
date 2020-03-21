package project.wpl.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.wpl.model.CurrentPrice;

@Repository
public interface CurrentPriceRepository extends CrudRepository<CurrentPrice,String> {



}
