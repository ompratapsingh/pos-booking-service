package com.pos.booking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;

@Repository
public class MenuItemsRepository {

	private static final String QUERY_FOR_FETCH_CATEGORY = "select Code, Descr, ColorCode from ArticleMaster";

	private static final String QUERY_FOR_ITEMS_FETCH = "select itm.Code as item_master_code, itm.Name as item_master_name, itm.Article,mm.Code,mm.Name,mm.Rate,mm.DiscPercent,mm.TaxCode,mm.AppGroup,mm.KotPrinter from ItemMaster itm inner join MenuMaster mm on itm.code = mm.ItemCode where itm.Article= ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Category> fetchCategories() {

		return jdbcTemplate.query(QUERY_FOR_FETCH_CATEGORY,
				(ResultSet resultSet, int rowNumber) -> new Category(resultSet.getString("Code"),
						resultSet.getString("Descr"), resultSet.getString("ColorCode")));
	}

	public List<MenuItems> fetchMenuItems(String articles) {
		return jdbcTemplate.query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareCall(QUERY_FOR_ITEMS_FETCH);
					preparedStatement.setString(1, articles);
					return preparedStatement;
				
			}
		}, new RowMapper<MenuItems>() {

			@Override
			public MenuItems mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				MenuItems items = new MenuItems();
				items.setItem_master_code(resultSet.getString("item_master_code"));
				items.setItem_master_name(resultSet.getString("item_master_name"));
				items.setArticle(resultSet.getString("Article"));
				items.setCode(resultSet.getString("code"));
				items.setName(resultSet.getString("Name"));
				items.setRate(resultSet.getString("Rate"));
				items.setDiscPercent(resultSet.getString("DiscPercent"));
				items.setTaxCode(resultSet.getString("TaxCode"));
				items.setAppGroup(resultSet.getString("AppGroup"));
				items.setKotPrinter(resultSet.getString("KotPrinter"));
				return items;
			}

		});
	}

}
