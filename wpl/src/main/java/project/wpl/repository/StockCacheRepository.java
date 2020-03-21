package project.wpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.wpl.model.SingleStock;

import java.io.Serializable;

@Repository
public interface StockCacheRepository extends JpaRepository<SingleStock,String> {


}
