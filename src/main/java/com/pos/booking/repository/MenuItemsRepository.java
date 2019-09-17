package com.pos.booking.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.Category;

@Repository
public class MenuItemsRepository {

	private static final String QUERY_FOR_FETCH_CATEGORY = "select Code, Descr, ColorCode from ArticleMaster";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Category> fetchCategories() {

		return jdbcTemplate.query(QUERY_FOR_FETCH_CATEGORY,
				(ResultSet resultSet, int rowNumber) -> new Category(resultSet.getString("Code"),
						resultSet.getString("Descr"), resultSet.getString("ColorCode"))
		);
	}

}
