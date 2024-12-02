package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.TemaModel;

public interface TemaRepository extends JpaRepository<TemaModel, Long>{
	
	public List <TemaModel> findAllByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);
	
	//SELECT * FROM tb_temass WHERE descricao LIKE "%descricao%";
	

}
