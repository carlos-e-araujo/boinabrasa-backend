package com.cefetmg.boinabrasa.repository;

import com.cefetmg.boinabrasa.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    //por enqt nenhum metodo adicional alem dos findbyid etc etc
}