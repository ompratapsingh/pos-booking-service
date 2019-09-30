package com.pos.booking.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;

@Repository
public class MenuItemsRepository {

	private static final String QUERY_FOR_FETCH_CATEGORY = "select Code, Descr, ColorCode from ArticleMaster";

	// private static final String QUERY_FOR_ITEMS_FETCH = "select itm.Code as
	// item_master_code, itm.Name as item_master_name,
	// itm.Article,mm.Code,mm.Name,mm.Rate,mm.DiscPercent,mm.TaxCode,mm.AppGroup,mm.KotPrinter
	// from ItemMaster itm inner join MenuMaster mm on itm.code = mm.ItemCode where
	// itm.Article= ?";

	private static final String QUERY_FOR_ITEMS_LIST = "select txm.Name as tax_name " + ",txm.Scope as tax_scope "
			+ ",txm.Pct as tax_percentage " + ",txm.Addtax as add_tax "
			+ ",txm.Surcharge as surcharge,itm.Code as item_master_code, itm.Name as item_master_name, itm.Article,mm.Code,mm.Name,mm.Rate,mm.DiscPercent,mm.TaxCode,mm.AppGroup,mm.KotPrinter from ItemMaster itm inner join MenuMaster mm on itm.code = mm.ItemCode "
			+ "left join TaxMaster txm on mm.TaxCode=txm.Code where txm.scope='S' " + "";

	private static final String QUERY_FOR_INSERT_CART_ITEMS = "insert into Kot (Prefix," + "Code," + "Qty," + "Rate,"
			+ "Disc, " + "DiscAmt," + "Docdate," + "Doctime, " + "TaxCode," + "NoofPrints," + "PaxNo," + "Remark," + "Store,"
			+ "TableCode," + "Type," + "Taxamt," + "AddtaxAmt," + "AddtaxAmt2, " + "Captain," + "HWSerial" + ") values (?,"
			+ "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?,"
			+ "?," + "?," + "?," + "?)";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Category> fetchCategories() {

		return jdbcTemplate.query(QUERY_FOR_FETCH_CATEGORY,
				(ResultSet resultSet, int rowNumber) -> new Category(resultSet.getString("Code"),
						resultSet.getString("Descr"), resultSet.getString("ColorCode")));
	}

	public List<MenuItems> fetchMenuItems() {
		return jdbcTemplate.query(QUERY_FOR_ITEMS_LIST, new RowMapper<MenuItems>() {

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
				items.setTax_name(resultSet.getString("tax_name"));
				items.setTax_scope(resultSet.getString("tax_scope"));
				items.setTax_percentage(resultSet.getString("tax_percentage"));
				items.setAdd_tax(resultSet.getString("tax_percentage"));
				items.setSurcharge(resultSet.getString("surcharge"));
				return items;
			}

		});

	}

	public boolean addToKot(List<CartItems> cartItems) {
		int[] insertedRow = jdbcTemplate.batchUpdate(QUERY_FOR_INSERT_CART_ITEMS, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement preparedStatement, int index) throws SQLException {
				CartItems cartItem = cartItems.get(index);
				preparedStatement.setString(1, cartItem.getPrefix());
				preparedStatement.setString(2, cartItem.getCode());
				preparedStatement.setString(3, String.valueOf(cartItem.getQty()));
				preparedStatement.setString(4, cartItem.getRate());
				preparedStatement.setString(5, cartItem.getDisc());
				preparedStatement.setString(6, cartItem.getDiscAmt());
				preparedStatement.setDate(7, new java.sql.Date(System.currentTimeMillis()));
				preparedStatement.setString(8, cartItem.getDoctime());
				preparedStatement.setString(9, cartItem.getTableCode());
				preparedStatement.setInt(10, cartItem.getNoofPrints());
				preparedStatement.setString(11, cartItem.getPaxNo());
				preparedStatement.setString(12, cartItem.getRemarks());
				preparedStatement.setString(13, cartItem.getStore());
				preparedStatement.setString(14, cartItem.getTableCode());
				preparedStatement.setString(15, cartItem.getType());
				preparedStatement.setString(16, cartItem.getTaxamt());
				preparedStatement.setString(17, cartItem.getAddtaxAmt());
				preparedStatement.setString(18, cartItem.getAddtaxAmt2());
				preparedStatement.setString(19, cartItem.getCaptain());
				preparedStatement.setString(20, cartItem.getHwserial());
			}

			@Override
			public int getBatchSize() {
				return cartItems.size();
			}

		});
		return insertedRow.length == cartItems.size();
	}

}
