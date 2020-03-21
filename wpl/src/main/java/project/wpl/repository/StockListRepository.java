package project.wpl.repository;

import org.springframework.data.repository.CrudRepository;
import project.wpl.model.StocksList;

public interface StockListRepository extends CrudRepository<StocksList,String> {

}
